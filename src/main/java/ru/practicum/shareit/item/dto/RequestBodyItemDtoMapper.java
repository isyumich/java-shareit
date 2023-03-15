package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

public class RequestBodyItemDtoMapper {
    public static Item mapRow(RequestBodyItemDto requestBodyItemDto) {
        return Item.builder()
                .name(requestBodyItemDto.getName())
                .description(requestBodyItemDto.getDescription())
                .available(requestBodyItemDto.getAvailable())
                .build();
    }
}
