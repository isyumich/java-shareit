package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto addNewItem(Item item, Long ownerId);

    ItemDto updateItem(long itemId, Item item, Long ownerId);

    List<ItemDto> getAllItems(Long ownerId);

    ItemDto getItemById(long itemId);

    List<ItemDto> getItemByNameOrDescription(String text);

}
