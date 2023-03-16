package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.RequestBodyItemDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface ItemService {
    ItemDto addNewItem(RequestBodyItemDto requestBodyItemDto, Long userId);

    CommentDto addNewComment(Comment comment, Long userId, long itemId);

    ItemDto updateItem(long itemId, RequestBodyItemDto requestBodyItemDto, Long userId);

    List<ItemDto> getAllItems(Long userId, Integer from, Integer size);

    ItemDto getItemById(long itemId, Long userId);

    List<ItemDto> getItemByNameOrDescription(String text, Long userId, Integer from, Integer size);

}
