package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.EntryNotFoundException;
import ru.practicum.shareit.exception.NotAvailableExeption;
import ru.practicum.shareit.exception.WrongOwnerExeption;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentRequestDto;
import ru.practicum.shareit.item.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final RequestRepository itemRequestRepository;


    @Override
    public List<ItemResponseDto> getItemsByUserId(long userId, int from, int size) {
        getUserById(userId);
        log.info(String.format("Возвращаем вещи юзера с id %s", userId));
        return itemRepository
                .findItemsByOwner_Id(userId, PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id")))
                .stream()
                .peek(item -> addBookings(item, userId))
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemResponseDto getItemById(long itemId, long userId) {
        log.info(String.format("Ищем вещь с id %s", itemId));
        Item item = getItemFromId(itemId);
        addBookings(item, userId);
        return ItemMapper.itemToDto(item);
    }

    @Override
    @Transactional
    public ItemResponseDto createItem(long userId, ItemRequestDto itemRequestDto) {
        log.info("Создаем вещь");
        User owner = getUserById(userId);
        Item item = ItemMapper.dtoToItem(itemRequestDto, getItemRequestById(itemRequestDto.getRequestId()));
        item.setOwner(owner);
        return ItemMapper.itemToDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemResponseDto updateItem(long userId, long itemId, ItemRequestDto itemRequestDto) {
        getUserById(userId);
        log.info(String.format("Обновляем вещь с id %s", itemId));
        Item itemToUpdate = getItemFromId(itemId);
        Item item = ItemMapper.dtoToItem(itemRequestDto, getItemRequestById(itemRequestDto.getRequestId()));

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

        return ItemMapper.itemToDto(itemToUpdate);
    }

    @Override
    public List<ItemResponseDto> searchItem(String query, int from, int size) {
        log.info(String.format("Ищем вещь %s", query));
        if (query.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository
                .searchItems(query, PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id")))
                .stream()
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentResponseDto addComment(long itemId, long userId, CommentRequestDto commentRequestDto) {
        Item item = getItemFromId(itemId);
        User user = getUserById(userId);
        Comment comment = CommentMapper.dtoToComment(commentRequestDto);

        if (user.getBookings().stream().noneMatch(booking -> booking.getItem().equals(item)
                && booking.getStatus().equals(BookingStatus.APPROVED)
                && booking.getStart().isBefore(LocalDateTime.now()))) {
            throw new NotAvailableExeption("Юзер не может оставить комментарий к этой вещи," +
                    " так как еще не бронировал ее");
        }
        comment.setItem(item);
        comment.setAuthor(user);

        return CommentMapper.commentToDto(commentRepository.save(comment));
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

    private Item getItemFromId(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(EntryNotFoundException.entryNotFoundException("Вещь не найдена"));
    }

    private Request getItemRequestById(Long itemRequestId) {
        if (itemRequestId != null) {
            return itemRequestRepository.findById(itemRequestId).orElse(null);
        }
        return null;
    }
}