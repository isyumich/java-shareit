package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.RequestBodyBookingDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;

public class BookingValidation {
    public void bookingValidation(RequestBodyBookingDto requestBooking, Item item, Long userId) {
        itemAvailableValidation(item);
        startOrEndDateValidation(requestBooking);
        bookerIsNotOwnerValidation(userId, item);
    }

    private void itemAvailableValidation(Item item) {
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недопустима для бронирования");
        }
    }

    private void startOrEndDateValidation(RequestBodyBookingDto requestBooking) {
        if (requestBooking.getStart().isAfter(requestBooking.getEnd()) || requestBooking.getStart().equals(requestBooking.getEnd())) {
            throw new ValidationException("Дата начала должна быть меньше, чем дата окончания");
        }
    }

    private void bookerIsNotOwnerValidation(Long userId, Item item) {
        if (item.getOwner().getId() == userId) {
            throw new NotFoundException("Владелец вещи не может её бронировать");
        }
    }

}
