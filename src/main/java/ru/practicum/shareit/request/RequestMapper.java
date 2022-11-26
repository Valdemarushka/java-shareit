package ru.practicum.shareit.request;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.ResponseDto;

import java.util.stream.Collectors;

@UtilityClass
public class RequestMapper {
    public static Request dtoToItemRequest(RequestDto itemRequestDto) {
        Request itemRequest = new Request();

        itemRequest.setDescription(itemRequestDto.getDescription());
        return itemRequest;
    }

    public static ResponseDto itemRequestToDto(Request itemRequest) {
        ResponseDto itemRequestDto = new ResponseDto();

        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setCreated(itemRequest.getCreated());

        if (itemRequest.getItems() != null) {
            itemRequestDto.setItems(
                    itemRequest.getItems().stream()
                            .map(item -> new ResponseDto.Item(item.getId(),
                                    item.getName(),
                                    item.getDescription(),
                                    item.isAvailable(),
                                    item.getRequest().getId()))
                            .collect(Collectors.toList())
            );
        }

        return itemRequestDto;
    }

}
