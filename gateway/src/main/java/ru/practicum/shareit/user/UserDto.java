package ru.practicum.shareit.user;

import lombok.Data;
import ru.practicum.shareit.groups.ForCreate;
import ru.practicum.shareit.groups.ForUpdate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UserDto {
    private long id;
    @NotBlank(groups = ForCreate.class, message = "Имя не может быть пустым")
    private String name;

    @Email(groups = {ForCreate.class, ForUpdate.class}, message = "Неверный email")
    @NotBlank(groups = ForCreate.class, message = "Email не может быть пустым")
    private String email;
}
