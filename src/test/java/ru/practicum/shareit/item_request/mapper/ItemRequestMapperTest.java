package ru.practicum.shareit.item_request.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item_request.dto.ItemRequestDto;
import ru.practicum.shareit.item_request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.item_request.model.ItemRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ItemRequestMapperTest {
    @Test
    public void toItemRequestDtoTest() {
        ItemRequest itemRequest = ItemRequest.builder().id(1L).author(null).description("itemRequestDesc").build();

        ItemRequestDto itemRequestDto = ItemRequestDtoMapper.mapRow(itemRequest);

        assertEquals(1L, itemRequestDto.getId());
        assertEquals("itemRequestDesc", itemRequestDto.getDescription());
        assertNull(itemRequestDto.getItems());
    }
}
