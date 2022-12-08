package ru.practicum.shareit.exeption;

public class WrongOwnerExeption extends RuntimeException {
    public WrongOwnerExeption(String message) {
        super(message);
    }
}
