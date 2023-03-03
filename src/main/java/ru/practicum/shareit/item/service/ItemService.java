package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto addNewItem(Item item, Long userId);

    CommentDto addNewComment(Comment comment, Long userId, long itemId);

    ItemDto updateItem(long itemId, Item item, Long userId);

    List<ItemDto> getAllItems(Long userId);

    ItemDto getItemById(long itemId, Long userId);

    List<ItemDto> getItemByNameOrDescription(String text, Long userId);

}
