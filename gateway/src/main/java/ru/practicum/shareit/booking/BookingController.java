package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.WrongDateException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@PathVariable long bookingId,
                                             @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestParam(defaultValue = "ALL") String state,
                                              @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                              @RequestParam(defaultValue = "10") @Positive int size,
                                              @RequestHeader("X-Sharer-User-Id") long userId) {
        checkBookingState(state);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingByItemOwner(@RequestParam(defaultValue = "ALL") String state,
                                                        @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                        @RequestParam(defaultValue = "10") @Positive int size,
                                                        @RequestHeader("X-Sharer-User-Id") long userId) {
        checkBookingState(state);
        return bookingClient.getBookingsByItemOwner(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestBody @Valid BookingRequestDto bookingDto) {
        checkBookingDate(bookingDto);
        return bookingClient.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> changeStatus(@PathVariable long bookingId,
                                               @RequestParam boolean approved,
                                               @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingClient.changeStatus(userId, bookingId, approved);
    }

    private void checkBookingDate(BookingRequestDto bookingRequestDto) {
        if (!bookingRequestDto.getStart().isBefore(bookingRequestDto.getEnd())) {
            throw new WrongDateException("Дата окончания бронирования меньше даты начала бронирования");
        }
    }

    private void checkBookingState(String state) {
        try {
            Enum.valueOf(BookingState.class, state);
        } catch (ConversionFailedException e) {
        }
    }
}
