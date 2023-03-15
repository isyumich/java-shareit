package ru.practicum.shareit.item_request.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item_request.dto.ItemRequestDto;
import ru.practicum.shareit.item_request.dto.RequestBodyItemRequestDto;
import ru.practicum.shareit.item_request.service.ItemRequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestController {
    final ItemRequestService itemRequestService;
    final String headerUserValue = "X-Sharer-User-Id";

    @Autowired
    public ItemRequestController(@Qualifier("ItemRequestServiceImpl") ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDto addNewItemRequest(@RequestHeader(value = headerUserValue, required = false) Long userId,
                                            @RequestBody RequestBodyItemRequestDto requestBodyRequestDto) {
        log.info("Запрос на создание новой заявки");
        return itemRequestService.addNewItemRequest(userId, requestBodyRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getOwnItemRequests(@RequestHeader(value = headerUserValue, required = false) Long userId) {
        log.info("Запрос на получение своих заявок");
        return itemRequestService.getOwnItemRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests(@RequestHeader(value = headerUserValue, required = false) Long userId,
                                                   @RequestParam(defaultValue = "0") Integer from,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        log.info("Запрос на получение всех заявок");
        return itemRequestService.getAllItemRequests(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader(value = headerUserValue, required = false) Long userId,
                                         @PathVariable long requestId) {
        log.info(String.format("%s %d", "Запрос на получение заявки с id =", requestId));
        return itemRequestService.getRequestById(userId, requestId);
    }
}
