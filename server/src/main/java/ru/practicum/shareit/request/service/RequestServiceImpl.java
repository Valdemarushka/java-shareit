package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.EntryNotFoundException;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.RequestMapper;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.ResponseDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final RequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Autowired
    public RequestServiceImpl(RequestRepository itemRequestRepository,
                              UserRepository userRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ResponseDto getItemRequestById(long itemRequestId, long userId) {
        getUserById(userId);
        Request itemRequest = itemRequestRepository
                .findById(itemRequestId)
                .orElseThrow(EntryNotFoundException.entryNotFoundException("Вещь не найдена"));
        return RequestMapper.itemRequestToDto(itemRequest);

    }

    @Override
    public List<ResponseDto> getItemRequestsByUser(long userId) {
        getUserById(userId);
        return itemRequestRepository
                .findByRequestorId(userId, Sort.by(Sort.Direction.DESC, "created"))
                .stream()
                .map(RequestMapper::itemRequestToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResponseDto> getItemRequests(long userId, int from, int size) {
        getUserById(userId);
        return itemRequestRepository
                .findByRequestorIdIsNot(userId, PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created")))
                .stream()
                .map(RequestMapper::itemRequestToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ResponseDto createItemRequest(long userId, RequestDto requestDto) {
        Request itemRequest = RequestMapper.dtoToItemRequest(requestDto);
        User user = getUserById(userId);
        itemRequest.setRequestor(user);
        return RequestMapper.itemRequestToDto(itemRequestRepository.save(itemRequest));
    }

    private User getUserById(long userId) {
        log.info(String.format("Возвращаем юзера с id %s", userId));
        return userRepository.findById(userId)
                .orElseThrow(EntryNotFoundException.entryNotFoundException("Юзер не найден"));
    }
}


