package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDtoTest {
    @Autowired
    JacksonTester<BookingDto> jacksonTester;

    @Test
    void bookingDtoJsonTest() throws IOException {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plus(1, ChronoUnit.DAYS);
        long bookingId = 1L;
        BookingDto bookingDto = BookingDto.builder().id(bookingId).start(start).end(end).status(BookingStatus.APPROVED).build();

        JsonContent<BookingDto> result = jacksonTester.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo((int) bookingDto.getId());
        assertThat(result).extractingJsonPathStringValue("$.start").isNotBlank();
        assertThat(result).extractingJsonPathStringValue("$.end").isNotBlank();
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(bookingDto.getStatus().toString());
    }
}
