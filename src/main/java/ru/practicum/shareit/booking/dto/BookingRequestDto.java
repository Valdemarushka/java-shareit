package ru.practicum.shareit.booking.dto;

import lombok.Data;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class BookingRequestDto {
    private long id;

    @NotNull(message = "Не указана вещь")
    private Long itemId;

    @NotNull(message = "Не указана дата начала бронирования")
    @FutureOrPresent(message = "Дата начала бронирования уже прошла")
    private LocalDateTime start;

    @NotNull(message = "Не указана дата конца бронирования")
    @Future(message = "Дата конца бронирования уже прошла")
    private LocalDateTime end;
}
