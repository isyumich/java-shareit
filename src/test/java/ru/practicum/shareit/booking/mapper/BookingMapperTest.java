package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class BookingMapperTest {
    @Test
    public void toBookingDtoTest() {
        Booking booking = Booking.builder().id(1L).item(null).booker(null)
                .start(LocalDateTime.now()).end(LocalDateTime.now().plus(1, ChronoUnit.DAYS))
                .status(BookingStatus.APPROVED).build();

        BookingDto bookingDto = BookingDtoMapper.mapRow(booking);

        assertEquals(1L, bookingDto.getId());
        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());
        assertNull(bookingDto.getItem());
        assertNull(bookingDto.getBooker());
    }
}
