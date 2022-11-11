package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public BookingService(BookingRepository bookingRepository,
                          UserRepository userRepository,
                          ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    public Booking getBookingById(long bookingId, long userId) {
        log.info(String.format("Ищем запись с id %s",bookingId));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(EntryNotFoundException.entryNotFoundException("Запись бронирования не найдена"));
        Item item = booking.getItem();

        if (!(checkItemOwner(userId, item) || checkBookingOwner(userId, booking))) {
            throw new WrongOwnerExeption(
                    String.format("Юзер с id = %d должен быть либо владельцем вещи с id = %d, либо автором бронирования с id = %d",
                            userId,
                            item.getId(),
                            booking.getId()));
        }
        return booking;
    }

    public List<Booking> getBookings(BookingState state, long userId) {
        log.info(String.format("Ищем записи бронирования юзера с id %s",userId));
        getUserById(userId);
        Sort sorting = Sort.by(Sort.Direction.DESC, "start");
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case ALL:
                return bookingRepository.findBookingsByBookerId(userId, sorting);
            case WAITING:
                return bookingRepository.findBookingsByBookerIdAndStatus(userId, BookingStatus.WAITING, sorting);
            case REJECTED:
                return bookingRepository.findBookingsByBookerIdAndStatus(userId, BookingStatus.REJECTED, sorting);
            case CURRENT:
                return bookingRepository.findBookingsByBookerIdAndStartIsBeforeAndEndIsAfter(userId, now, now, sorting);
            case PAST:
                return bookingRepository.findBookingsByBookerIdAndEndIsBefore(userId, now, sorting);
            case FUTURE:
                return bookingRepository.findBookingsByBookerIdAndStartIsAfter(userId, now, sorting);
            default:
                throw new WrongBookingStateException("Unknown state: " + state);
        }
    }

    public List<Booking> getBookingsByItemOwner(BookingState state, long userId) {
        log.info(String.format("Ищем записи бронирования владельца с id %s",userId));
        getUserById(userId);
        List<Long> userItemsId = itemRepository.findItemsByOwner_Id(userId).stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        Sort sorting = Sort.by(Sort.Direction.DESC, "start");
        LocalDateTime now = LocalDateTime.now();

        if (userItemsId.isEmpty()) {
            return Collections.emptyList();
        }

        switch (state) {
            case ALL:
                return bookingRepository.findBookingsByItemIdIn(userItemsId,
                        sorting);
            case WAITING:
                return bookingRepository.findBookingsByItemIdInAndStatus(userItemsId,
                        BookingStatus.WAITING,
                        sorting);
            case REJECTED:
                return bookingRepository.findBookingsByItemIdInAndStatus(userItemsId,
                        BookingStatus.REJECTED,
                        sorting);
            case CURRENT:
                return bookingRepository.findBookingsByItemIdInAndStartIsBeforeAndEndIsAfter(userItemsId,
                        now,
                        now,
                        sorting);
            case PAST:
                return bookingRepository.findBookingsByItemIdInAndEndIsBefore(userItemsId,
                        now,
                        sorting);
            case FUTURE:
                return bookingRepository.findBookingsByItemIdInAndStartIsAfter(userItemsId,
                        now,
                        sorting);
            default:
                throw new WrongBookingStateException("Unknown state: " + state);
        }
    }

    @Transactional
    public Booking createBooking(long userId, Booking booking) {
        log.info("Создаем запись бронирования");
        extendBooking(userId, booking);

        if (!booking.getItem().isAvailable()) {
            throw new NotAvailableExeption("Эта вещь недоступна для бронирования");
        }
        if (booking.getItem().getOwner().getId() == userId) {
            throw new WrongOwnerExeption("Вы являетесь владельцем вещи");
        }
        if (booking.getStart().isAfter(booking.getEnd())) {
            throw new WrongDateException("Конец бронирования не может быть раньше, чем начало");
        }
        booking.setStatus(BookingStatus.WAITING);
        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking changeStatus(long bookingId, long userId, boolean approved) {
        log.info("Меняем статус бронирования");
        Booking booking = getBookingById(bookingId, userId);

        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new StatusIsConfirmedException("Бронирование уже было подтверждено");
        }

        Item item = booking.getItem();

        if (!checkItemOwner(userId, item)) {
            throw new WrongOwnerExeption("Юзер не является владельцем вещи");
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return booking;
    }

    private boolean checkItemOwner(long userId, Item item) {
        return item.getOwner().getId() == userId;
    }

    private boolean checkBookingOwner(long userId, Booking booking) {
        return booking.getBooker().getId() == userId;
    }

    private User getUserById(long userId) {
        log.info(String.format("Ищем юзера с id %s",userId));
        return userRepository.findById(userId)
                .orElseThrow(EntryNotFoundException.entryNotFoundException("Юзер не найден"));
    }

    private void extendBooking(long userId, Booking booking) {
        log.info("Дополняем бронирование");
        User booker = getUserById(userId);
        Item item = itemRepository.findById(booking.getItem().getId())
                .orElseThrow(EntryNotFoundException.entryNotFoundException("Вещь не найдена"));
        booking.setBooker(booker);
        booking.setItem(item);
    }
}
