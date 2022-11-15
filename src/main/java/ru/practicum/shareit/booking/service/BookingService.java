package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.Booking;

import java.util.List;

public interface BookingService {
    Booking getBookingById(long bookingId, long userId);

    List<Booking> getBookings(String state, long userId);

    List<Booking> getBookingsByItemOwner(String state, long userId);

    Booking createBooking(long userId, Booking booking);

    Booking changeStatus(long bookingId, long userId, boolean approved);
}
