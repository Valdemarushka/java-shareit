package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.user.UserMapper;

import java.util.stream.Collectors;

@UtilityClass
public class ItemMapper {
    public static ItemResponseDto itemToDto(Item item) {
        ItemResponseDto.ItemResponseDtoBuilder dtoBuilder = ItemResponseDto.builder();

        dtoBuilder
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .owner(UserMapper.userToDto(item.getOwner()))
                .available(item.isAvailable());

        if (item.getRequest() != null) {
            dtoBuilder.requestId(item.getRequest().getId());
        }

        if (item.getLastBooking() != null) {
            ItemResponseDto.Booking lastBooking = new ItemResponseDto.Booking(item.getLastBooking().getId(),
                    item.getLastBooking().getBooker().getId());
            dtoBuilder.lastBooking(lastBooking);
        }

        if (item.getNextBooking() != null) {
            ItemResponseDto.Booking nextBooking = new ItemResponseDto.Booking(item.getNextBooking().getId(),
                    item.getNextBooking().getBooker().getId());
            dtoBuilder.nextBooking(nextBooking);
        }

        if (item.getComments() != null) {
            dtoBuilder.comments(
                    item.getComments().stream()
                            .map(CommentMapper::commentToDto)
                            .collect(Collectors.toList()));
        }

        return dtoBuilder.build();
    }

    public static Item dtoToItem(ItemRequestDto itemDto, Request itemRequest) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setRequest(itemRequest);

        if (itemDto.getAvailable() == null) {
            item.setAvailable(true);
        } else {
            item.setAvailable(itemDto.getAvailable());
        }
        return item;
    }
}
