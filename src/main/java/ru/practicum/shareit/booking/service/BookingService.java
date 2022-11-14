package ru.practicum.shareit.booking.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;

import java.util.List;

public interface BookingService {
    Booking getBookingById(long bookingId, long userId);

    List<Booking> getBookings(String state, long userId);

    List<Booking> getBookingsByItemOwner(String state, long userId);

    @Transactional
    Booking createBooking(long userId, Booking booking);

    @Transactional
    Booking changeStatus(long bookingId, long userId, boolean approved);
}
