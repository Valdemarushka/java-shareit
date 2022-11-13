package ru.practicum.shareit.item;

import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ItemRepository {
    List<Item> findItemsByUserId(long userId);

    Optional<Item> findItemById(long itemId);

    Item createItem(User user, Item item);

    Item updateItem(long itemId, Item item);

    void deleteItem(long itemId);

    Set<Item> searchItems(String query);
}
