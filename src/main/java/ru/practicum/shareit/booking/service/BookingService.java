package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.RequestBodyBookingDto;

import java.util.List;

public interface BookingService {
    BookingDto addNewBooking(RequestBodyBookingDto requestBooking, Long userId);

    BookingDto approveOrRejectBooking(Long userId, long bookingId, boolean approved);

    BookingDto getBookingById(Long userId, long bookingId);

    List<BookingDto> getBookingCurrentUser(Long userId, String state, Integer from, Integer size);

    List<BookingDto> getBookingForItemsCurrentUser(Long userId, String state, Integer from, Integer size);
}
