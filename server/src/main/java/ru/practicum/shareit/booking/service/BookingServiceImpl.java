package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exeption.*;
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
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository,
                              UserRepository userRepository,
                              ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public BookingResponseDto getBookingById(long userId, long bookingId) {
        getUserById(userId);
        log.info(String.format("Ищем запись с id %s", bookingId));
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
        return BookingMapper.bookingToDto(booking);
    }

    @Override
    public List<BookingResponseDto> getBookings(String state, long userId, int from, int size) {
        getUserById(userId);
        BookingState stateEnum = BookingState.valueOf(state.trim().toUpperCase());
        log.info(String.format("Ищем записи бронирования юзера с id %s", userId));
        Pageable pagination = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (stateEnum) {
            case ALL:
                bookings = bookingRepository.findByBookerId(userId, pagination);
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING, pagination);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED, pagination);
                break;
            case CURRENT:
                bookings = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(userId, now, now, pagination);
                break;
            case PAST:
                bookings = bookingRepository.findByBookerIdAndEndIsBefore(userId, now, pagination);
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerIdAndStartIsAfter(userId, now, pagination);
                break;
            default:
                throw new WrongBookingStateException("Unknown state: " + state);
        }
        return bookings.stream()
                .map(BookingMapper::bookingToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getBookingsByItemOwner(String state, long userId, int from, int size) {
        getUserById(userId);

        BookingState stateEnum = BookingState.valueOf(state.trim().toUpperCase());
        log.info(String.format("Ищем записи бронирования владельца с id %s", userId));

        List<Long> userItemsId = itemRepository
                .findItemsByOwner_Id(userId).stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        Pageable pagination = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        if (userItemsId.isEmpty()) {
            return Collections.emptyList();
        }

        switch (stateEnum) {
            case ALL:
                bookings = bookingRepository.findByItemIdIn(userItemsId, pagination);
                break;
            case WAITING:
                bookings = bookingRepository.findByItemIdInAndStatus(userItemsId, BookingStatus.WAITING, pagination);
                break;
            case REJECTED:
                bookings = bookingRepository.findByItemIdInAndStatus(userItemsId, BookingStatus.REJECTED, pagination);
                break;
            case CURRENT:
                bookings = bookingRepository.findByItemIdInAndStartIsBeforeAndEndIsAfter(userItemsId, now, now,
                        pagination);
                break;
            case PAST:
                bookings = bookingRepository.findByItemIdInAndEndIsBefore(userItemsId, now, pagination);
                break;
            case FUTURE:
                bookings = bookingRepository.findByItemIdInAndStartIsAfter(userItemsId, now, pagination);
                break;
            default:
                throw new WrongBookingStateException("Unknown state: " + state);
        }
        return bookings.stream()
                .map(BookingMapper::bookingToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingResponseDto createBooking(long userId, BookingRequestDto bookingRequestDto) {
        Booking booking = BookingMapper.dtoToBooking(bookingRequestDto);
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
        return BookingMapper.bookingToDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingResponseDto changeStatus(long bookingId, long userId, boolean approved) {
        getUserById(userId);
        log.info("Меняем статус бронирования");
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(EntryNotFoundException.entryNotFoundException("Запись бронирования не найдена"));

        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new StatusIsConfirmedException("Бронирование уже было подтверждено");
        }

        Item item = booking.getItem();

        if (!checkItemOwner(userId, item)) {
            throw new WrongOwnerExeption("Юзер не является владельцем вещи");
        }

        if (approved && booking.getStatus().equals(BookingStatus.WAITING)) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        bookingRepository.save(booking);
        return BookingMapper.bookingToDto(booking);
    }

    private boolean checkItemOwner(long userId, Item item) {
        return item.getOwner().getId() == userId;
    }

    private boolean checkBookingOwner(long userId, Booking booking) {
        return booking.getBooker().getId() == userId;
    }

    private User getUserById(long userId) {
        log.info(String.format("Ищем юзера с id %s", userId));
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
