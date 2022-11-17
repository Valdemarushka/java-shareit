package ru.practicum.shareit.exception;

public class NotValidEmailExeption extends RuntimeException {
    public NotValidEmailExeption(String message) {
        super(message);
    }
}
