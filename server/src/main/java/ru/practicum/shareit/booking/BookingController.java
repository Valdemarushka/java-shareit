package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingServiceImpl;

import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingServiceImpl bookingService;

    @Autowired
    public BookingController(BookingServiceImpl bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBooking(@PathVariable long bookingId,
                                         @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> getBookings(@RequestParam String state,
                                                @RequestParam int from,
                                                @RequestParam int size,
                                                @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.getBookings(state, userId, from, size);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getBookingByItemOwner(@RequestParam String state,
                                                          @RequestParam int from,
                                                          @RequestParam int size,
                                                          @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.getBookingsByItemOwner(state, userId, from, size);
    }

    @PostMapping
    public BookingResponseDto createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @RequestBody BookingRequestDto bookingDto) {
        return bookingService.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto changeStatus(@PathVariable long bookingId,
                                           @RequestParam boolean approved,
                                           @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.changeStatus(bookingId, userId, approved);
    }
}
