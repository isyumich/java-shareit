package ru.practicum.shareit.item.repository;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.InternalServerException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Qualifier("InMemoryItemRepository")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InMemoryItemRepository implements ItemRepository {
    long nextId = 1;
    final Map<Long, Item> items = new HashMap<>();

    @Override
    public ItemDto addNewItem(Item item, Long ownerId) {
        item.setOwnerId(ownerId);
        item.setId(nextId++);
        items.put(item.getId(), item);
        log.info(String.format("%s %d %s", "Товар с id =", item.getId(), "добавлен"));
        return ItemDtoMapper.mapRow(item);
    }

    @Override
    public ItemDto updateItem(long itemId, Item item, Long ownerId) {
        Item checkedItem = checkFieldsForUpdate(item, itemId, ownerId);
        checkedItem.setOwnerId(ownerId);
        checkedItem.setId(itemId);
        items.put(itemId, checkedItem);
        return ItemDtoMapper.mapRow(checkedItem);
    }

    @Override
    public List<ItemDto> getAllItems(Long ownerId) {
        List<ItemDto> itemsOfOwner = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwnerId().equals(ownerId)) {
                itemsOfOwner.add(ItemDtoMapper.mapRow(item));
            }
        }
        return itemsOfOwner;
    }

    @Override
    public ItemDto getItemById(long itemId) {
        return ItemDtoMapper.mapRow(items.get(itemId));
    }

    @Override
    public List<ItemDto> getItemByNameOrDescription(String text) {
        System.out.println(text);
        System.out.println(items);
        List<ItemDto> itemsForSearch = new ArrayList<>();
        if (!text.equals("")) {
            for (Item item : items.values()) {
                if ((item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                        && item.getAvailable()) {
                    itemsForSearch.add(ItemDtoMapper.mapRow(item));
                }
            }
        }
        System.out.println(itemsForSearch);
        return itemsForSearch;
    }

    private Item checkFieldsForUpdate(Item item, long itemId, Long ownerId) {
        if (!items.containsKey(itemId)) {
            log.info(String.format("%s %d %s", "Товар с id =", itemId, "не найден"));
            throw new NotFoundException(String.format("%s %d %s", "Товар с id = ", itemId, "не найден"));
        }
        if (ownerId == null) {
            log.info("Не указан id пользователя");
            throw new InternalServerException("Не указан id пользователя");
        }
        if (!items.get(itemId).getOwnerId().equals(ownerId)) {
            log.info("Попытка редактирования товара другого пользователя");
            throw new ForbiddenException("Вам нельзя редактировать товар другого пользователя");
        }
        if (item.getName() == null) {
            item.setName(items.get(itemId).getName());
        }
        if (item.getDescription() == null) {
            item.setDescription(items.get(itemId).getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(items.get(itemId).getAvailable());
        }
        return item;
    }
}
