package ru.practicum.shareit.item.comment.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class CommentRequestDto {
    @NotBlank(message = "Комментарий не может быть пустым")
    private String text;
}

