package ru.practicum.shareit.request.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
public class RequestDto {
    @NotBlank(message = "Описание не может быть пустым")
    private String description;
}
