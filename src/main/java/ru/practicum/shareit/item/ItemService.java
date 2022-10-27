package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundExeption;
import ru.practicum.shareit.exception.WrongOwnerExeption;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public List<ItemDto> getItemsByUserId(long userId) {
        return itemRepository.findItemsByUserId(userId).stream()
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());
    }

    public ItemDto getItemById(long itemId) {
        return itemRepository.findItemById(itemId)
                .map(ItemMapper::itemToDto)
                .orElseThrow(() -> new NotFoundExeption("Такой вещи нет"));
    }

    public ItemDto createItem(long userId, ItemDto itemDto) {
        User owner = userRepository.findUserById(userId)
                .orElseThrow(() -> new NotFoundExeption("Такого пользователя нет"));
        Item item = ItemMapper.dtoToItem(itemDto);
        return ItemMapper.itemToDto(itemRepository.createItem(owner, item));
    }

    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        User owner = userRepository.findUserById(userId)
                .orElseThrow(() -> new NotFoundExeption("Такого пользователя нет"));
        ItemDto itemToUpdate = getItemById(itemId);
        checkOwner(userId, itemToUpdate);
        Item item = ItemMapper.dtoToItem(itemDto);
        return ItemMapper.itemToDto(itemRepository.updateItem(itemId, item));
    }

    public void deleteItem(long itemId) {
        itemRepository.deleteItem(itemId);
    }

    public List<ItemDto> searchItem(String query) {
        return itemRepository.searchItems(query).stream()
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());
    }

    private void checkOwner(long userId, ItemDto itemDto) {
        if (itemDto.getOwner().getId() != userId) {
            throw new WrongOwnerExeption("Указанный пользователь не является пользователем указанной вещи");
        }
    }
}