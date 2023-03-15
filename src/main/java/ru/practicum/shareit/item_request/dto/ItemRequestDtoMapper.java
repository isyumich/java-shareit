package ru.practicum.shareit.item_request.dto;

import ru.practicum.shareit.item_request.model.ItemRequest;

public class ItemRequestDtoMapper {
    public static ItemRequestDto mapRow(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }
}
