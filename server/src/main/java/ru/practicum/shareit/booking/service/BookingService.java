package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto getBookingById(long bookingId, long userId);

    List<BookingResponseDto> getBookings(String state, long userId, int from, int size);

    List<BookingResponseDto> getBookingsByItemOwner(String state, long userId, int from, int size);

    BookingResponseDto createBooking(long userId, BookingRequestDto booking);

    BookingResponseDto changeStatus(long bookingId, long userId, boolean approved);
}
