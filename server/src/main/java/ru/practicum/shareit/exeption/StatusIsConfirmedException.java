package ru.practicum.shareit.exeption;

public class StatusIsConfirmedException extends RuntimeException {
    public StatusIsConfirmedException(String message) {
        super(message);
    }
}
