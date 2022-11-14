package ru.practicum.shareit.item.comment.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CommentRequestDto {
    @NotBlank(message = "Комментарий не может быть пустым")
    @NotNull(message = "Комментарий не может быть пустым")
    private String text;
}

