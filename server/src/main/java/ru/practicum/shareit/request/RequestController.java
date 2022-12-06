package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.ResponseDto;
import ru.practicum.shareit.request.service.RequestServiceImpl;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
public class RequestController {
    private final RequestServiceImpl requestService;

    @Autowired
    public RequestController(RequestServiceImpl requestService) {
        this.requestService = requestService;
    }

    @GetMapping("/{requestId}")
    public ResponseDto getRequestById(@PathVariable long requestId,
                                      @RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.getItemRequestById(requestId, userId);
    }

    @GetMapping
    public List<ResponseDto> getRequestsByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.getItemRequestsByUser(userId);
    }

    @GetMapping("/all")
    public List<ResponseDto> getRequests(@RequestParam int from,
                                         @RequestParam int size,
                                         @RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.getItemRequests(userId, from, size);
    }

    @PostMapping
    public ResponseDto createRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @RequestBody RequestDto requestDto) {
        return requestService.createItemRequest(userId, requestDto);
    }


}
