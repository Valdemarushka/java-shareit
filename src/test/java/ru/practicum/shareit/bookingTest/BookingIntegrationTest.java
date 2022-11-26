package ru.practicum.shareit.bookingTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BookingIntegrationTest {
    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User itemOwner;
    private User booker;

    private Item firstItem;
    private Item secondItem;
    private Item thirdItem;

    private BookingRequestDto firstBookingRequestDto;
    private BookingRequestDto secondBookingRequestDto;
    private BookingRequestDto thirdBookingRequestDto;
    private BookingRequestDto fourthBookingRequestDto;

    private BookingResponseDto firstBookingResponseDto;
    private BookingResponseDto secondBookingResponseDto;
    private BookingResponseDto thirdBookingResponseDto;
    private BookingResponseDto fourthBookingResponseDto;

    @BeforeEach
    void setUp() {
        itemOwner = new User();
        itemOwner.setId(1);
        itemOwner.setName("name1");
        itemOwner.setEmail("user1@email.com");

        booker = new User();
        booker.setId(2);
        booker.setName("name2");
        booker.setEmail("user2@email.com");

        firstItem = new Item();
        firstItem.setId(1);
        firstItem.setName("name item 1");
        firstItem.setDescription("description item 1");
        firstItem.setAvailable(true);
        firstItem.setOwner(itemOwner);

        secondItem = new Item();
        secondItem.setId(2);
        secondItem.setName("name item 2");
        secondItem.setDescription("description item 2");
        secondItem.setAvailable(true);
        secondItem.setOwner(itemOwner);

        thirdItem = new Item();
        thirdItem.setId(3);
        thirdItem.setName("name item 2");
        thirdItem.setDescription("description item 2");
        thirdItem.setAvailable(true);
        thirdItem.setOwner(itemOwner);

        firstBookingRequestDto = new BookingRequestDto();
        firstBookingRequestDto.setItemId(firstItem.getId());
        firstBookingRequestDto.setStart(LocalDateTime.now().minusDays(2));
        firstBookingRequestDto.setEnd(LocalDateTime.now().plusDays(2));

        secondBookingRequestDto = new BookingRequestDto();
        secondBookingRequestDto.setItemId(secondItem.getId());
        secondBookingRequestDto.setStart(LocalDateTime.now().minusDays(1));
        secondBookingRequestDto.setEnd(LocalDateTime.now().plusDays(2));

        thirdBookingRequestDto = new BookingRequestDto();
        thirdBookingRequestDto.setItemId(thirdItem.getId());
        thirdBookingRequestDto.setStart(LocalDateTime.now().minusDays(4));
        thirdBookingRequestDto.setEnd(LocalDateTime.now().minusDays(2));

        fourthBookingRequestDto = new BookingRequestDto();
        fourthBookingRequestDto.setItemId(thirdItem.getId());
        fourthBookingRequestDto.setStart(LocalDateTime.now().plusDays(2));
        fourthBookingRequestDto.setEnd(LocalDateTime.now().plusDays(4));

        userRepository.save(itemOwner);
        userRepository.save(booker);

        itemRepository.save(firstItem);
        itemRepository.save(secondItem);
        itemRepository.save(thirdItem);

        firstBookingResponseDto = bookingService.createBooking(booker.getId(), firstBookingRequestDto);
        secondBookingResponseDto = bookingService.createBooking(booker.getId(), secondBookingRequestDto);
        thirdBookingResponseDto = bookingService.createBooking(booker.getId(), thirdBookingRequestDto);
        fourthBookingResponseDto = bookingService.createBooking(booker.getId(), fourthBookingRequestDto);
        firstBookingResponseDto = bookingService.changeStatus(firstBookingResponseDto.getId(), itemOwner.getId(), false);
    }

    @Test
    void getAllBookingsTest() {
        List<BookingResponseDto> bookings = bookingService.getBookings("ALL", booker.getId(), 0, 10);
        assertThat(bookings, hasSize(4));
        assertThat(bookings.get(0).getId(), equalTo(fourthBookingResponseDto.getId()));
        assertThat(bookings.get(1).getId(), equalTo(secondBookingResponseDto.getId()));
        assertThat(bookings.get(2).getId(), equalTo(firstBookingResponseDto.getId()));
        assertThat(bookings.get(3).getId(), equalTo(thirdBookingResponseDto.getId()));
    }

    @Test
    void getRejectedBookingsTest() {
        List<BookingResponseDto> bookings = bookingService.getBookings("REJECTED", booker.getId(), 0, 10);
        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getId(), equalTo(firstBookingResponseDto.getId()));
    }

    @Test
    void getWaitingBookingsTest() {
        List<BookingResponseDto> bookings = bookingService.getBookings("WAITING", booker.getId(), 0, 10);
        assertThat(bookings, hasSize(3));
        assertThat(bookings.get(0).getId(), equalTo(fourthBookingResponseDto.getId()));
        assertThat(bookings.get(1).getId(), equalTo(secondBookingResponseDto.getId()));
        assertThat(bookings.get(2).getId(), equalTo(thirdBookingResponseDto.getId()));
    }

    @Test
    void getCurrentBookingsTest() {
        List<BookingResponseDto> bookings = bookingService.getBookings("CURRENT", booker.getId(), 0, 10);
        assertThat(bookings, hasSize(2));
        assertThat(bookings.get(0).getId(), equalTo(secondBookingResponseDto.getId()));
        assertThat(bookings.get(1).getId(), equalTo(firstBookingResponseDto.getId()));
    }

    @Test
    void getPastBookingsTest() {
        List<BookingResponseDto> bookings = bookingService.getBookings("PAST", booker.getId(), 0, 10);
        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getId(), equalTo(thirdBookingResponseDto.getId()));
    }

    @Test
    void getFutureBookingsTest() {
        List<BookingResponseDto> bookings = bookingService.getBookings("FUTURE", booker.getId(), 0, 10);
        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getId(), equalTo(fourthBookingResponseDto.getId()));
    }
}
