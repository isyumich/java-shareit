package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.LastAndNextBookingDto;
import ru.practicum.shareit.booking.dto.LastAndNextBookingDtoMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LastAndNextBookingMapperTest {
    @Test
    public void toLastAndNextBookingDtoTest() {
        User booker = User.builder().id(1L).name("bookerName").email("bookerEmail@mail.ru").build();
        Booking booking = Booking.builder().id(1L).item(null).booker(booker)
                .start(LocalDateTime.now()).end(LocalDateTime.now().plus(1, ChronoUnit.DAYS))
                .status(BookingStatus.APPROVED).build();

        LastAndNextBookingDto lastAndNextBookingDto = LastAndNextBookingDtoMapper.mapRow(booking);

        assertEquals(1L, lastAndNextBookingDto.getId());
        assertEquals(1L, lastAndNextBookingDto.getBookerId());
    }
}
