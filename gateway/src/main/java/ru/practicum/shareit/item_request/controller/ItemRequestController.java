package ru.practicum.shareit.item_request.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item_request.client.ItemRequestClient;
import ru.practicum.shareit.item_request.dto.RequestBodyItemRequestDto;
import ru.practicum.shareit.validation.Create;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@Controller
@Validated
@RequestMapping(path = "/requests")
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ItemRequestController {
    final ItemRequestClient itemRequestClient;
    final String headerUserValue = "X-Sharer-User-Id";

    @Validated(Create.class)
    @PostMapping
    public ResponseEntity<Object> addNewItemRequest(@RequestHeader(value = headerUserValue, required = false) Long userId,
                                                    @Valid @RequestBody RequestBodyItemRequestDto requestBodyRequestDto) {
        return itemRequestClient.addNewItemRequest(userId, requestBodyRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnItemRequests(@RequestHeader(value = headerUserValue, required = false) Long userId) {
        return itemRequestClient.getOwnItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(@RequestHeader(value = headerUserValue, required = false) Long userId,
                                                     @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                     @Positive @RequestParam(defaultValue = "10") Integer size) {
        return itemRequestClient.getAllItemRequests(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(value = headerUserValue, required = false) Long userId,
                                                 @PathVariable long requestId) {
        return itemRequestClient.getRequestById(userId, requestId);
    }
}
