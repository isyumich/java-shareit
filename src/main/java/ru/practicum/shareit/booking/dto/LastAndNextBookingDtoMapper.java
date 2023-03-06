package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;

public class LastAndNextBookingDtoMapper {
    public static LastAndNextBookingDto mapRow(Booking booking) {
        return LastAndNextBookingDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }
}
