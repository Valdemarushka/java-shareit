package ru.practicum.shareit.exception;

public class StatusIsConfirmedException extends RuntimeException {
    public StatusIsConfirmedException(String message) {
        super(message);
    }
}
