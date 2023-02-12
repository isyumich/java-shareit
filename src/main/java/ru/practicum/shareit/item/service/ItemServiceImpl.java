package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.controller.UserController;

import java.util.List;

@Slf4j
@Service
@Qualifier("ItemServiceImpl")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemServiceImpl implements ItemService {
    final ItemRepository itemRepository;
    final UserController userController;
    final ItemValidation itemValidation = new ItemValidation();

    @Autowired
    public ItemServiceImpl(@Qualifier("InMemoryItemRepository") ItemRepository itemRepository, UserController userController) {
        this.itemRepository = itemRepository;
        this.userController = userController;
    }

    @Override
    public ItemDto addNewItem(Item item, Long ownerId) {
        if (itemValidation.itemValidation(item, ownerId)) {
            if (userController.getUserById(ownerId) != null) {
                return itemRepository.addNewItem(item, ownerId);
            } else {
                log.info("Пользователь, указанный в товаре не найден");
                throw new NotFoundException("Пользователь, указанный в товаре не найден");
            }
        } else {
            log.info("Поля заполнены неверно или не указан id пользователя");
            throw new ValidationException("Поля заполнены неверно или не указан id пользователя");
        }
    }

    @Override
    public ItemDto updateItem(long itemId, Item item, Long ownerId) {
        return itemRepository.updateItem(itemId, item, ownerId);
    }

    @Override
    public List<ItemDto> getAllItems(Long ownerId) {
        return itemRepository.getAllItems(ownerId);
    }

    @Override
    public ItemDto getItemById(long itemId) {
        return itemRepository.getItemById(itemId);
    }

    @Override
    public List<ItemDto> getItemByNameOrDescription(String text) {
        return itemRepository.getItemByNameOrDescription(text);
    }
}
