package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.groups.ForCreate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ItemRequestDto {
    @NotBlank(groups = ForCreate.class, message = "Параметр name не может быть пустым")
    private String name;

    @NotBlank(groups = ForCreate.class, message = "Параметр description не может быть пустым")
    private String description;

    @NotNull(groups = ForCreate.class, message = "Параметр available не может быть пустым")
    private Boolean available;
}
