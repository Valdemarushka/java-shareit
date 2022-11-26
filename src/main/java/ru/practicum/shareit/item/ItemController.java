package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.groups.ForCreate;
import ru.practicum.shareit.groups.ForUpdate;
import ru.practicum.shareit.item.comment.dto.CommentRequestDto;
import ru.practicum.shareit.item.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemServiceImpl itemService;

    @Autowired
    public ItemController(ItemServiceImpl itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<ItemResponseDto> getUserItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                              @RequestParam(defaultValue = "10") @Positive int size) {
        return itemService.getItemsByUserId(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto getItemById(@PathVariable long itemId,
                                       @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getItemById(itemId, userId);
    }

    @PostMapping
    public ResponseEntity<ItemResponseDto> createItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                                      @RequestBody @Validated(ForCreate.class) ItemRequestDto itemDto) {
        return new ResponseEntity<>(itemService.createItem(userId, itemDto), HttpStatus.CREATED);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @PathVariable long itemId,
                                      @RequestBody @Validated(ForUpdate.class) ItemRequestDto itemDto) {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/search")
    public List<ItemResponseDto> searchItems(@RequestParam String text,
                                             @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                             @RequestParam(defaultValue = "10") @Positive int size) {
        return itemService.searchItem(text, from, size);
    }

    @PostMapping("{itemId}/comment")
    public CommentResponseDto addComment(@PathVariable long itemId,
                                         @RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestBody @Valid CommentRequestDto commentDto) {
        return itemService.addComment(itemId, userId, commentDto);
    }
}
