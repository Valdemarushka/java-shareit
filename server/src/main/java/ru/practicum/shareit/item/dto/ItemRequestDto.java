package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ItemRequestDto {
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
}
