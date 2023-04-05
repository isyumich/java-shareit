package ru.practicum.shareit.item_request.dto;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.TestHelper;

import java.io.IOException;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestDtoTest {
    final TestHelper testHelper = new TestHelper();
    @Autowired
    JacksonTester<ItemRequestDto> jacksonTester;

    @Test
    void itemRequestDtoTest() throws IOException {
        ItemRequestDto itemRequestDto = ItemRequestDtoMapper.mapRow(testHelper.getItemRequest());
        itemRequestDto.setItems(new ArrayList<>());
        JsonContent<ItemRequestDto> result = jacksonTester.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo((int) itemRequestDto.getId());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemRequestDto.getDescription());
        assertThat(result).extractingJsonPathStringValue("$.created").isNotBlank();
        assertThat(result).extractingJsonPathValue("$.items").isEqualTo(itemRequestDto.getItems());
    }
}
