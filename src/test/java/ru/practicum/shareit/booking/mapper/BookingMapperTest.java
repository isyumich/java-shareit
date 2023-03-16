package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class BookingMapperTest {
    TestHelper testHelper = new TestHelper();

    @Test
    public void toBookingDtoTest() {
        Booking booking = testHelper.getBooking();

        BookingDto bookingDto = BookingDtoMapper.mapRow(booking);

        assertEquals(1L, bookingDto.getId());
        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());
        assertNull(bookingDto.getItem());
        assertNull(bookingDto.getBooker());
    }
}
