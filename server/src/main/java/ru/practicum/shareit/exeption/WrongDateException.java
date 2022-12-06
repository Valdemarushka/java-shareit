package ru.practicum.shareit.exeption;

public class WrongDateException extends RuntimeException {
    public WrongDateException(String message) {
        super(message);
    }
}
