package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundExeption;
import ru.practicum.shareit.exception.WrongOwnerExeption;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public List<ItemDto> getItemsByUserId(long userId) {
        log.info(String.format("Возвращаем вещи юзера с id %s",userId));
        return itemRepository.findItemsByUserId(userId).stream()
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());
    }

    public ItemDto getItemById(long itemId) {
        log.info(String.format("Ищем вещь с id %s",itemId));
        return itemRepository.findItemById(itemId)
                .map(ItemMapper::itemToDto)
                .orElseThrow(() -> new NotFoundExeption("Такой вещи нет"));
    }

    public ItemDto createItem(long userId, ItemDto itemDto) {
        log.info("Создаем вещь");
        User owner = userRepository.findUserById(userId)
                .orElseThrow(() -> new NotFoundExeption("Такого пользователя нет"));
        Item item = ItemMapper.dtoToItem(itemDto);
        return ItemMapper.itemToDto(itemRepository.createItem(owner, item));
    }

    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        log.info(String.format("Обновляем вещь с id %s",itemId));
        User owner = userRepository.findUserById(userId)
                .orElseThrow(() -> new NotFoundExeption("Такого пользователя нет"));
        ItemDto itemToUpdate = getItemById(itemId);
        checkOwner(userId, itemToUpdate);
        Item item = ItemMapper.dtoToItem(itemDto);
        return ItemMapper.itemToDto(itemRepository.updateItem(itemId, item));
    }

    public void deleteItem(long itemId) {
        log.info(String.format("Удаляем вещь с id %s",itemId));
        itemRepository.deleteItem(itemId);
    }

    public List<ItemDto> searchItem(String query) {
        log.info(String.format("Ищем вещь %s",query));

        return itemRepository.searchItems(query).stream()
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());
    }

    private void checkOwner(long userId, ItemDto itemDto) {
        log.info(String.format("Проверяем хозяина вещи с id %s",userId));
        if (itemDto.getOwner().getId() != userId) {
            throw new WrongOwnerExeption("Указанный пользователь не является пользователем указанной вещи");
        }
    }
}