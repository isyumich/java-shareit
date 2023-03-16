package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.TestHelper;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDtoTest {
    @Autowired
    JacksonTester<ItemDto> jacksonTester;
    final TestHelper testHelper = new TestHelper();

    @Test
    void userDtoJsonTest() throws IOException {
        ItemDto itemDto = ItemDtoMapper.mapRow(testHelper.getItem());
        itemDto.setRequestId(1L);

        JsonContent<ItemDto> result = jacksonTester.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo((int) itemDto.getId());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemDto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(itemDto.getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo((int) itemDto.getRequestId());
    }
}
