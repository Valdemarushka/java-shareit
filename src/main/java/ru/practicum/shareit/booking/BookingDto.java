package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.UserDto;

import java.time.LocalDate;

@Data
public class BookingDto {
    private long id;
    private ItemDto item;
    private LocalDate start;
    private LocalDate end;
    private UserDto booker;
    private BookingStatus status;
}
