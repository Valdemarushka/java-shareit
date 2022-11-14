package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.EntryNotFoundException;
import ru.practicum.shareit.exception.NotAvailableExeption;
import ru.practicum.shareit.exception.WrongOwnerExeption;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public List<Item> getItemsByUserId(long userId) {
        log.info(String.format("Возвращаем вещи юзера с id %s", userId));
        return itemRepository.findItemsByOwner_Id(userId, Sort.by(Sort.Direction.ASC, "id"));
    }

    @Override
    public Item getItemById(long itemId) {
        log.info(String.format("Ищем вещь с id %s", itemId));
        return itemRepository.findById(itemId)
                .orElseThrow(EntryNotFoundException.entryNotFoundException("Вещь не найдена"));
    }

    @Override
    @Transactional
    public Item createItem(long userId, Item item) {
        log.info("Создаем вещь");
        User owner = getUserById(userId);
        item.setOwner(owner);
        return itemRepository.save(item);
    }

    @Override
    @Transactional
    public Item updateItem(long userId, long itemId, Item item) {
        log.info(String.format("Обновляем вещь с id %s", itemId));
        Item itemToUpdate = getItemById(itemId);

        if (!checkOwner(userId, itemToUpdate)) {
            throw new WrongOwnerExeption("Юзер не является владельцем этой вещи");
        }

        if (item.getName() != null) {
            itemToUpdate.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemToUpdate.setDescription(item.getDescription());
        }
        itemToUpdate.setAvailable(item.isAvailable());
        if (item.getRequest() != null) {
            itemToUpdate.setRequest(item.getRequest());
        }

        return itemToUpdate;
    }

    @Override
    public List<Item> searchItem(String query) {
        log.info(String.format("Ищем вещь %s", query));
        return itemRepository.searchItems(query);
    }

    @Override
    @Transactional
    public Comment addComment(long itemId, long userId, Comment comment) {
        Item item = getItemById(itemId);
        User user = getUserById(userId);

        if (user.getBookings().stream().noneMatch(booking -> booking.getItem().equals(item)
                && booking.getStatus().equals(BookingStatus.APPROVED)
                && booking.getStart().isBefore(LocalDateTime.now()))) {
            throw new NotAvailableExeption("Юзер не может оставить комментарий к этой вещи," +
                    " так как еще не бронировал ее");
        }
        comment.setItem(item);
        comment.setAuthor(user);

        return commentRepository.save(comment);
    }

    @Override
    public void addBookings(Item item, long userId) {
        log.info("Добавляем бронирование");

        if (item.getBookings() != null && item.getOwner().getId() == userId) {

            item.setLastBooking(
                    bookingRepository.findByItemIdAndStartIsBeforeAndStatus(item.getId(),
                                    LocalDateTime.now(),
                                    BookingStatus.APPROVED,
                                    Sort.by(Sort.Direction.DESC, "start")).stream()
                            .findFirst()
                            .orElse(null));

            item.setNextBooking(
                    bookingRepository.findByItemIdAndStartIsAfterAndStatus(item.getId(),
                                    LocalDateTime.now(),
                                    BookingStatus.APPROVED,
                                    Sort.by(Sort.Direction.ASC, "start")).stream()
                            .findFirst()
                            .orElse(null));
        }
    }

    private boolean checkOwner(long userId, Item item) {
        log.info(String.format("Проверяем хозяина вещи с id %s", userId));
        return item.getOwner().getId() == userId;
    }

    private User getUserById(long userId) {
        log.info(String.format("Возвращаем юзера с id %s", userId));
        return userRepository.findById(userId)
                .orElseThrow(EntryNotFoundException.entryNotFoundException("Юзер не найден"));
    }
}