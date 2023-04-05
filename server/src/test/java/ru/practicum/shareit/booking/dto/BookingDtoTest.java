package ru.practicum.shareit.booking.dto;

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
public class BookingDtoTest {
    @Autowired
    JacksonTester<BookingDto> jacksonTester;
    final TestHelper testHelper = new TestHelper();

    @Test
    void bookingDtoJsonTest() throws IOException {
        BookingDto bookingDto = BookingDtoMapper.mapRow(testHelper.getBooking());

        JsonContent<BookingDto> result = jacksonTester.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo((int) bookingDto.getId());
        assertThat(result).extractingJsonPathStringValue("$.start").isNotBlank();
        assertThat(result).extractingJsonPathStringValue("$.end").isNotBlank();
        assertThat(result).extractingJsonPathStringValue("$.item").isBlank();
        assertThat(result).extractingJsonPathStringValue("$.booker").isBlank();
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(bookingDto.getStatus().toString());
    }
}
