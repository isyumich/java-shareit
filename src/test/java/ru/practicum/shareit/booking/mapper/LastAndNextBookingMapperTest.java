package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.booking.dto.LastAndNextBookingDto;
import ru.practicum.shareit.booking.dto.LastAndNextBookingDtoMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LastAndNextBookingMapperTest {
    TestHelper testHelper = new TestHelper();
    @Test
    public void toLastAndNextBookingDtoTest() {
        User booker = testHelper.getBooker();
        Booking booking = testHelper.getBooking();
        booking.setBooker(booker);

        LastAndNextBookingDto lastAndNextBookingDto = LastAndNextBookingDtoMapper.mapRow(booking);

        assertEquals(1L, lastAndNextBookingDto.getId());
        assertEquals(2L, lastAndNextBookingDto.getBookerId());
    }
}
