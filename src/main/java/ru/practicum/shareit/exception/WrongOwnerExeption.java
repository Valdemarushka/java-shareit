package ru.practicum.shareit.exception;

public class WrongOwnerExeption extends RuntimeException {
    public WrongOwnerExeption(String message) {
        super(message);
    }
}
