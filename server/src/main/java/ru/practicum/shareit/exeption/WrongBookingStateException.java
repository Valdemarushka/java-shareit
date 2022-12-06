package ru.practicum.shareit.exeption;

public class WrongBookingStateException extends RuntimeException {
    public WrongBookingStateException(String message) {
        super(message);
    }
}
