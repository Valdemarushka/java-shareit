package ru.practicum.shareit.bookingTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);
    }

    @Test
    void getBookingByItemOwnerTest() {
        User itemOwner = new User();
        itemOwner.setId(1);

        User booker = new User();
        booker.setId(2);

        Item item = new Item();
        item.setId(1);
        item.setOwner(itemOwner);

        Booking booking = new Booking();
        booking.setId(1);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemOwner));

        bookingService.getBookingById(1, 1);

        Mockito.verify(bookingRepository, Mockito.times(1)).findById(1L);
        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void getBookingByBookerTest() {
        User itemOwner = new User();
        itemOwner.setId(1);

        User booker = new User();
        booker.setId(2);

        Item item = new Item();
        item.setId(1);
        item.setOwner(itemOwner);

        Booking booking = new Booking();
        booking.setId(1);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().plusDays(2));

        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));

        bookingService.getBookingById(1, 2);

        Mockito.verify(bookingRepository, Mockito.times(1)).findById(1L);
        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void getBookingUserIsNotOwnerAndIsNotBookerTest() {
        User itemOwner = new User();
        itemOwner.setId(1);

        User booker = new User();
        booker.setId(2);

        Item item = new Item();
        item.setId(1);
        item.setOwner(itemOwner);

        Booking booking = new Booking();
        booking.setId(1);
        booking.setBooker(booker);
        booking.setItem(item);

        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User()));

        Exception exception = assertThrows(WrongOwnerExeption.class,
                () -> bookingService.getBookingById(1, 3));
        assertThat(exception.getMessage(), equalTo("Юзер с id = 3 должен быть либо владельцем вещи с id = 1," +
                " либо автором бронирования с id = 1"));

        Mockito.verify(userRepository, Mockito.times(1)).findById(3L);
        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void getBookingUserNotFoundTest() {
        Exception exception = assertThrows(EntryNotFoundException.class,
                () -> bookingService.getBookingById(1, 1));
        assertThat(exception.getMessage(), equalTo("Юзер не найден"));
        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void bookingNotFoundTest() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User()));

        Exception exception = assertThrows(EntryNotFoundException.class,
                () -> bookingService.getBookingById(1, 1));
        assertThat(exception.getMessage(), equalTo("Запись бронирования не найдена"));

        Mockito.verify(bookingRepository, Mockito.times(1)).findById(1L);
        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void getAllBookingsTest() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User()));

        bookingService.getBookings("ALL", 1, 0, 10);

        Mockito
                .verify(bookingRepository, Mockito.times(1))
                .findByBookerId(anyLong(), any(Pageable.class));
        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void getBookingsWrongStatusTest() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User()));

        Exception exception = assertThrows(WrongBookingStateException.class,
                () -> bookingService.getBookings("TEST", 1, 0, 10));
        assertThat(exception.getMessage(), equalTo("Unknown state: TEST"));

        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void getBookingsUserNotFoundTest() {
        Exception exception = assertThrows(EntryNotFoundException.class,
                () -> bookingService.getBookings("ALL", 1, 1, 1));
        assertThat(exception.getMessage(), equalTo("Юзер не найден"));

        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void getAllBookingsByItemOwnerTest() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User()));
        Mockito
                .when(itemRepository.findItemsByOwner_Id(anyLong()))
                .thenReturn(List.of(new Item(), new Item()));

        bookingService.getBookingsByItemOwner("ALL", 1, 0, 10);

        Mockito
                .verify(bookingRepository, Mockito.times(1))
                .findByItemIdIn(anyList(), any(Pageable.class));
        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void getWaitingBookingsByItemOwnerTest() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User()));
        Mockito
                .when(itemRepository.findItemsByOwner_Id(anyLong()))
                .thenReturn(List.of(new Item(), new Item()));

        bookingService.getBookingsByItemOwner("WAITING", 1, 0, 10);

        Mockito
                .verify(bookingRepository, Mockito.times(1))
                .findByItemIdInAndStatus(anyList(), any(BookingStatus.class), any(Pageable.class));
        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void getRejectedBookingsByItemOwnerTest() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User()));
        Mockito
                .when(itemRepository.findItemsByOwner_Id(anyLong()))
                .thenReturn(List.of(new Item(), new Item()));

        bookingService.getBookingsByItemOwner("REJECTED", 1, 0, 10);

        Mockito
                .verify(bookingRepository, Mockito.times(1))
                .findByItemIdInAndStatus(anyList(), any(BookingStatus.class), any(Pageable.class));
        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void getCurrentBookingsByItemOwnerTest() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User()));
        Mockito
                .when(itemRepository.findItemsByOwner_Id(anyLong()))
                .thenReturn(List.of(new Item(), new Item()));

        bookingService.getBookingsByItemOwner("CURRENT", 1, 0, 10);

        Mockito
                .verify(bookingRepository, Mockito.times(1))
                .findByItemIdInAndStartIsBeforeAndEndIsAfter(anyList(), any(LocalDateTime.class),
                        any(LocalDateTime.class), any(Pageable.class));
        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void getPastBookingsByItemOwnerTest() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User()));
        Mockito
                .when(itemRepository.findItemsByOwner_Id(anyLong()))
                .thenReturn(List.of(new Item(), new Item()));

        bookingService.getBookingsByItemOwner("PAST", 1, 0, 10);

        Mockito
                .verify(bookingRepository, Mockito.times(1))
                .findByItemIdInAndEndIsBefore(anyList(), any(LocalDateTime.class), any(Pageable.class));
        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void getFutureBookingsByItemOwnerTest() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User()));
        Mockito
                .when(itemRepository.findItemsByOwner_Id(anyLong()))
                .thenReturn(List.of(new Item(), new Item()));

        bookingService.getBookingsByItemOwner("FUTURE", 1, 0, 10);

        Mockito
                .verify(bookingRepository, Mockito.times(1))
                .findByItemIdInAndStartIsAfter(anyList(), any(LocalDateTime.class), any(Pageable.class));
        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void getBookingByItemOwnerWrongStatusTest() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User()));
        Mockito
                .when(itemRepository.findItemsByOwner_Id(anyLong()))
                .thenReturn(List.of(new Item(), new Item()));

        Exception exception = assertThrows(WrongBookingStateException.class,
                () -> bookingService.getBookingsByItemOwner("TEST", 1, 0, 10));
        assertThat(exception.getMessage(), equalTo("Unknown state: TEST"));

        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void getBookingByWrongOwnerTest() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User()));

        List<BookingResponseDto> items = bookingService.getBookingsByItemOwner("ALL", 1, 0, 10);

        assertThat(items, hasSize(0));
        Mockito.verify(itemRepository, Mockito.times(1)).findItemsByOwner_Id(1);
        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void getBookingsByOwnerNotFoundTest() {
        Exception exception = assertThrows(EntryNotFoundException.class,
                () -> bookingService.getBookingsByItemOwner("ALL", 1, 1, 1));
        assertThat(exception.getMessage(), equalTo("Юзер не найден"));

        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void createBookingTest() {
        ArgumentCaptor<Booking> itemArgumentCaptor = ArgumentCaptor.forClass(Booking.class);

        User booker = new User();
        booker.setId(1);

        User itemOwner = new User();
        itemOwner.setId(2);

        Item item = new Item();
        item.setId(1);
        item.setAvailable(true);
        item.setOwner(itemOwner);

        Booking booking = new Booking();
        booking.setId(1);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().plusDays(2));

        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(item.getId());
        bookingRequestDto.setStart(LocalDateTime.now().minusDays(2));
        bookingRequestDto.setEnd(LocalDateTime.now().plusDays(2));

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        bookingService.createBooking(1, bookingRequestDto);

        Mockito.verify(bookingRepository, Mockito.times(1)).save(itemArgumentCaptor.capture());

        Booking capturedBooking = itemArgumentCaptor.getValue();

        assertThat(capturedBooking.getItem(), equalTo(item));
        assertThat(capturedBooking.getBooker(), equalTo(booker));
        assertThat(capturedBooking.getStart(), equalTo(bookingRequestDto.getStart()));
        assertThat(capturedBooking.getEnd(), equalTo(bookingRequestDto.getEnd()));
        assertThat(capturedBooking.getStatus(), equalTo(BookingStatus.WAITING));
        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void createBookingUserNotFoundTest() {
        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(1L);

        Exception exception = assertThrows(EntryNotFoundException.class,
                () -> bookingService.createBooking(1, bookingRequestDto));
        assertThat(exception.getMessage(), equalTo("Юзер не найден"));

        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void createBookingItemNotFoundTest() {
        Item item = new Item();
        item.setId(1);

        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(item.getId());

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User()));

        Exception exception = assertThrows(EntryNotFoundException.class,
                () -> bookingService.createBooking(1, bookingRequestDto));
        assertThat(exception.getMessage(), equalTo("Вещь не найдена"));

        Mockito.verify(itemRepository, Mockito.times(1)).findById(1L);
        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void createBookingItemIsNotAvailableTest() {
        User booker = new User();
        booker.setId(1);

        User itemOwner = new User();
        itemOwner.setId(2);

        Item item = new Item();
        item.setId(1);
        item.setOwner(itemOwner);

        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(item.getId());

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        Exception exception = assertThrows(NotAvailableExeption.class,
                () -> bookingService.createBooking(1, bookingRequestDto));
        assertThat(exception.getMessage(), equalTo("Эта вещь недоступна для бронирования"));

        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void createBookingByItemOwnerTest() {
        User booker = new User();
        booker.setId(1);

        Item item = new Item();
        item.setId(1);
        item.setOwner(booker);
        item.setAvailable(true);

        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(item.getId());

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        Exception exception = assertThrows(WrongOwnerExeption.class,
                () -> bookingService.createBooking(1, bookingRequestDto));
        assertThat(exception.getMessage(), equalTo("Вы являетесь владельцем вещи"));

        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void createBookingWrongDateTest() {
        User booker = new User();
        booker.setId(1);

        User itemOwner = new User();
        itemOwner.setId(2);

        Item item = new Item();
        item.setId(1);
        item.setOwner(itemOwner);
        item.setAvailable(true);

        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(item.getId());
        bookingRequestDto.setStart(LocalDateTime.now().plusDays(2));
        bookingRequestDto.setEnd(LocalDateTime.now().minusDays(2));

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        Exception exception = assertThrows(WrongDateException.class,
                () -> bookingService.createBooking(1, bookingRequestDto));
        assertThat(exception.getMessage(), equalTo("Конец бронирования не может быть раньше, чем начало"));

        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void changeStatusApprovedTest() {
        User user = new User();
        user.setId(1);

        Item item = new Item();
        item.setId(1);
        item.setOwner(user);

        Booking booking = new Booking();
        booking.setId(1);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(BookingStatus.WAITING);

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        BookingResponseDto bookingResponseDto = bookingService.changeStatus(1, 1, true);

        assertThat(bookingResponseDto.getId(), equalTo(booking.getId()));
        assertThat(bookingResponseDto.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void changeStatusRejectedTest() {
        User user = new User();
        user.setId(1);

        Item item = new Item();
        item.setId(1);
        item.setOwner(user);

        Booking booking = new Booking();
        booking.setId(1);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(BookingStatus.WAITING);

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        BookingResponseDto bookingResponseDto = bookingService.changeStatus(1, 1, false);

        assertThat(bookingResponseDto.getId(), equalTo(booking.getId()));
        assertThat(bookingResponseDto.getStatus(), equalTo(BookingStatus.REJECTED));
    }

    @Test
    void statusAlreadyApprovedTest() {
        User user = new User();
        user.setId(1);

        Item item = new Item();
        item.setId(1);
        item.setOwner(user);

        Booking booking = new Booking();
        booking.setId(1);
        booking.setItem(item);
        booking.setStatus(BookingStatus.APPROVED);

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        Exception exception = assertThrows(StatusIsConfirmedException.class,
                () -> bookingService.changeStatus(1, 1, true));
        assertThat(exception.getMessage(), equalTo("Бронирование уже было подтверждено"));

        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void changeStatusByBookerTest() {
        User user = new User();
        user.setId(1);

        User itemOwner = new User();
        itemOwner.setId(2);

        Item item = new Item();
        item.setId(1);
        item.setOwner(itemOwner);

        Booking booking = new Booking();
        booking.setId(1);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        Exception exception = assertThrows(WrongOwnerExeption.class,
                () -> bookingService.changeStatus(1, 1, true));
        assertThat(exception.getMessage(), equalTo("Юзер не является владельцем вещи"));

        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void changeStatusBookingNotFoundTest() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User()));

        Exception exception = assertThrows(EntryNotFoundException.class,
                () -> bookingService.changeStatus(1, 1, true));
        assertThat(exception.getMessage(), equalTo("Запись бронирования не найдена"));

        Mockito.verify(bookingRepository, Mockito.times(1)).findById(1L);
        Mockito.verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }
}
