package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;

public class BookingDtoMapper {
    public static BookingDto mapRow(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .status(booking.getStatus())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
    }
}
