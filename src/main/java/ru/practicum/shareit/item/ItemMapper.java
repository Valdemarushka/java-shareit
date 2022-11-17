package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.UserMapper;

import java.util.stream.Collectors;

public class ItemMapper {
    public static ItemResponseDto itemToDto(Item item) {
        ItemResponseDto itemDto = new ItemResponseDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setOwner(UserMapper.userToDto(item.getOwner()));
        itemDto.setAvailable(item.isAvailable());
        if (item.getLastBooking() != null) {
            itemDto.setLastBooking(new ItemResponseDto.Booking(item.getLastBooking().getId(),
                    item.getLastBooking().getBooker().getId()));
        }
        if (item.getNextBooking() != null) {
            itemDto.setNextBooking(new ItemResponseDto.Booking(item.getNextBooking().getId(),
                    item.getNextBooking().getBooker().getId()));
        }
        if (item.getComments() != null) {
            itemDto.setComments(item.getComments().stream()
                    .map(CommentMapper::commentToDto)
                    .collect(Collectors.toList()));
        }
        return itemDto;
    }

    public static Item dtoToItem(ItemRequestDto itemDto) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() == null) {
            item.setAvailable(true);
        } else {
            item.setAvailable(itemDto.getAvailable());
        }
        return item;
    }
}
