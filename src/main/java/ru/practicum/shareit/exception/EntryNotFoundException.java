package ru.practicum.shareit.exception;

import java.util.function.Supplier;

public class EntryNotFoundException extends RuntimeException {
    public EntryNotFoundException(String message) {
        super(message);
    }

    public static Supplier<EntryNotFoundException> entryNotFoundException(String message) {
        return () -> new EntryNotFoundException(message);
    }

}
