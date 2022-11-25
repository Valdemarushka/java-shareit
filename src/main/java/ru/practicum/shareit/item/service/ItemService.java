package ru.practicum.shareit.item.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.comment.dto.CommentRequestDto;
import ru.practicum.shareit.item.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;

public interface ItemService {
    List<ItemResponseDto> getItemsByUserId(long userId, int from, int size);

    ItemResponseDto getItemById(long itemId, long userId);

    @Transactional
    ItemResponseDto createItem(long userId, ItemRequestDto itemRequestDto);

    @Transactional
    ItemResponseDto updateItem(long userId, long itemId, ItemRequestDto itemRequestDto);

    List<ItemResponseDto> searchItem(String query, int from, int size);

    @Transactional
    CommentResponseDto addComment(long itemId, long userId, CommentRequestDto commentRequestDto);

    void addBookings(Item item, long userId);
}
