package ru.practicum.shareit.item.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.comment.Comment;

import java.util.List;

public interface ItemService {
    List<Item> getItemsByUserId(long userId);

    Item getItemById(long itemId);

    @Transactional
    Item createItem(long userId, Item item);

    @Transactional
    Item updateItem(long userId, long itemId, Item item);

    List<Item> searchItem(String query);

    @Transactional
    Comment addComment(long itemId, long userId, Comment comment);

    void addBookings(Item item, long userId);
}
