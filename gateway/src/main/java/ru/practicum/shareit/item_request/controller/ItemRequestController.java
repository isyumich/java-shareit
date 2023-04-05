package ru.practicum.shareit.item_request.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item_request.client.ItemRequestClient;
import ru.practicum.shareit.item_request.dto.RequestBodyItemRequestDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@Controller
@RequestMapping(path = "/requests")
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ItemRequestController {
    final ItemRequestClient itemRequestClient;
    final String HEADER_USER_VALUE = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> addNewItemRequest(@RequestHeader(value = HEADER_USER_VALUE, required = false) Long userId,
                                                    @RequestBody RequestBodyItemRequestDto requestBodyRequestDto) {
        return itemRequestClient.addNewItemRequest(userId, requestBodyRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnItemRequests(@RequestHeader(value = HEADER_USER_VALUE, required = false) Long userId) {
        return itemRequestClient.getOwnItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(@RequestHeader(value = HEADER_USER_VALUE, required = false) Long userId,
                                                     @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                     @Positive @RequestParam(defaultValue = "10") Integer size) {
        return itemRequestClient.getAllItemRequests(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(value = HEADER_USER_VALUE, required = false) Long userId,
                                                 @PathVariable long requestId) {
        return itemRequestClient.getRequestById(userId, requestId);
    }
}
