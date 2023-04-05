package ru.practicum.shareit.item.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.item.dto.RequestBodyCommentDto;
import ru.practicum.shareit.item.dto.RequestBodyItemDto;
import ru.practicum.shareit.validation.Update;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@Validated
@RequestMapping(path = "/items")
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor

public class ItemController {
    final ItemClient itemClient;
    final String pathIdItem = "/{itemId}";
    final String headerUserValue = "X-Sharer-User-Id";

    @Validated(Create.class)
    @PostMapping
    public ResponseEntity<Object> addNewItem(@RequestHeader(value = headerUserValue, required = false) Long userId,
                                             @Valid @RequestBody RequestBodyItemDto requestBodyItemDto) {
        return itemClient.addNewItem(requestBodyItemDto, userId);
    }

    @Validated(Create.class)
    @PostMapping(pathIdItem + "/comment")
    public ResponseEntity<Object> addNewComment(@RequestHeader(value = headerUserValue, required = false) Long userId,
                                                @Valid @RequestBody RequestBodyCommentDto requestBodyCommentDto,
                                                @PathVariable long itemId) {
        return itemClient.addNewComment(requestBodyCommentDto, userId, itemId);
    }


    @Validated(Update.class)
    @PatchMapping(pathIdItem)
    public ResponseEntity<Object> updateItem(@RequestHeader(value = headerUserValue, required = false) Long userId,
                                             @PathVariable long itemId,
                                             @Valid @RequestBody RequestBodyItemDto requestBodyItemDto) {
        return itemClient.updateItem(itemId, requestBodyItemDto, userId);
    }


    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader(value = headerUserValue, required = false) Long userId,
                                              @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                              @Positive @RequestParam(defaultValue = "10") Integer size) {
        return itemClient.getAllItems(userId, from, size);
    }

    @GetMapping(pathIdItem)
    public ResponseEntity<Object> getItemById(@RequestHeader(value = headerUserValue, required = false) Long userId,
                                              @PathVariable long itemId) {
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemByNameOrDescription(@RequestHeader(value = headerUserValue, required = false) Long userId,
                                                             @RequestParam String text,
                                                             @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                             @Positive @RequestParam(defaultValue = "10") Integer size) {
        return itemClient.getItemByNameOrDescription(text, userId, from, size);
    }
}
