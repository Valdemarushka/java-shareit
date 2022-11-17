package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.groups.ForCreate;
import ru.practicum.shareit.user.UserDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ItemDto {
    private long id;

    @NotBlank(groups = ForCreate.class,
            message = "Имя не может быть пустым")
    private String name;

    @NotBlank(groups = ForCreate.class,
            message = "Описание не может быть пустым")
    private String description;
    private UserDto owner;

    @NotNull(groups = ForCreate.class,
            message = "Статус не может быть пустым")
    private Boolean available;
    private long requestId;
}
