package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.groups.ForCreate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ItemRequestDto {
    @NotBlank(groups = ForCreate.class, message = "Имя не может быть пустым")
    private String name;

    @NotBlank(groups = ForCreate.class, message = "Описание не может быть пустым")
    private String description;

    @NotNull(groups = ForCreate.class, message = "Доступность не может быть пустой")
    private Boolean available;

    private Long requestId;
}
