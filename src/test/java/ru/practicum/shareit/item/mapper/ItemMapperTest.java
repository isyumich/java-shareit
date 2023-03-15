package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ItemMapperTest {
    @Test
    public void toItemDtoTest() {
        Item item = Item.builder().id(1L).name("itemName").description("itemDesc").available(true).build();

        ItemDto itemDto = ItemDtoMapper.mapRow(item);

        assertEquals(1L, itemDto.getId());
        assertEquals("itemName", itemDto.getName());
        assertEquals("itemDesc", itemDto.getDescription());
        assertTrue(itemDto.getAvailable());
    }
}
