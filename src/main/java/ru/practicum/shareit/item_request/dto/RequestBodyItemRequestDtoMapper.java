package ru.practicum.shareit.item_request.dto;

import ru.practicum.shareit.item_request.model.ItemRequest;

public class RequestBodyItemRequestDtoMapper {
    public static ItemRequest mapRow(RequestBodyItemRequestDto requestBodyRequestDto) {
        return ItemRequest.builder()
                .description(requestBodyRequestDto.getDescription())
                .build();
    }
}
