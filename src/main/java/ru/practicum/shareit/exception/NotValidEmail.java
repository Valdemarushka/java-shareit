package ru.practicum.shareit.exception;

public class NotValidEmail extends RuntimeException {
    public NotValidEmail(String message) {
        super(message);
    }
}
