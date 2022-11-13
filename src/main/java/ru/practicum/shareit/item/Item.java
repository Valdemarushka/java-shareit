package ru.practicum.shareit.item;

import lombok.Data;
import ru.practicum.shareit.user.User;

@Data
public class Item {
    private long id;
    private String name;
    private String description;
    private User owner;
    private Boolean available;
    private long requestId;
}
