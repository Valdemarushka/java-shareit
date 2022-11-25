package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.ResponseDto;

import java.util.List;

public interface RequestService {
    ResponseDto getItemRequestById(long itemRequestId, long userId);

    List<ResponseDto> getItemRequestsByUser(long userId);

    List<ResponseDto> getItemRequests(long userId, int from, int size);

    ResponseDto createItemRequest(long userId, RequestDto itemReqRequestDto);
}
