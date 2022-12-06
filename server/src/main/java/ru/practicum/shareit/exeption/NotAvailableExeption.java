package ru.practicum.shareit.exeption;

public class NotAvailableExeption extends RuntimeException {
    public NotAvailableExeption(String message) {
        super(message);
    }
}
