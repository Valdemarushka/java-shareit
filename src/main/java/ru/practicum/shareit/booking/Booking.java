package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDate;

@Data
public class Booking {
    private long id;
    private Item item;
    private LocalDate start;
    private LocalDate end;
    private User booker;
    private BookingStatus status;
}
