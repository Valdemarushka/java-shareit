package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.comment.dto.CommentResponseDto;
import ru.practicum.shareit.user.UserDto;

import java.util.List;

@Setter
@Getter
@Builder
public class ItemResponseDto {
    private long id;
    private String name;
    private String description;
    private UserDto owner;
    private boolean available;
    private Long requestId;
    private Booking lastBooking;
    private Booking nextBooking;
    private List<CommentResponseDto> comments;

    @Data
    public static class Booking {
        private final long id;
        private final long bookerId;
    }
}
