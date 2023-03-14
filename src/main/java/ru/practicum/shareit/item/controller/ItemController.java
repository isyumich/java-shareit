package ru.practicum.shareit.item.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.RequestBodyItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/items")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemController {
    final ItemService itemService;

    final String pathIdItem = "/{itemId}";
    final String headerUserValue = "X-Sharer-User-Id";

    @Autowired
    public ItemController(@Qualifier("ItemServiceImpl") ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto addNewItem(@RequestHeader(value = headerUserValue, required = false) Long userId, @RequestBody RequestBodyItemDto requestBodyItemDto) {
        log.info("Request for adding a new item");
        return itemService.addNewItem(requestBodyItemDto, userId);
    }

    @PostMapping(pathIdItem + "/comment")
    public CommentDto addNewComment(@RequestHeader(value = headerUserValue, required = false) Long userId, @RequestBody Comment comment, @PathVariable long itemId) {
        log.info("Request for adding a new comment");
        return itemService.addNewComment(comment, userId, itemId);
    }


    @PatchMapping(pathIdItem)
    public ItemDto updateItem(@RequestHeader(value = headerUserValue, required = false) Long userId, @PathVariable long itemId, @RequestBody RequestBodyItemDto requestBodyItemDto) {
        log.info(String.format("%s %d", "Request for changing the item with id =", itemId));
        return itemService.updateItem(itemId, requestBodyItemDto, userId);
    }


    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader(value = headerUserValue, required = false) Long userId,
                                     @RequestParam(defaultValue = "0") Integer from,
                                     @RequestParam(defaultValue = "10") Integer size) {
        log.info("Request for getting all items");
        return itemService.getAllItems(userId, from, size);
    }

    @GetMapping(pathIdItem)
    public ItemDto getItemById(@RequestHeader(value = headerUserValue, required = false) Long userId, @PathVariable long itemId) {
        log.info(String.format("%s %d", "Request for getting the item with id =", itemId));
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemByNameOrDescription(@RequestHeader(value = headerUserValue, required = false) Long userId,
                                                    @RequestParam String text,
                                                    @RequestParam(defaultValue = "0") Integer from,
                                                    @RequestParam(defaultValue = "10") Integer size) {
        log.info("Request for getting the item by name or description");
        return itemService.getItemByNameOrDescription(text, userId, from, size);
    }
}
