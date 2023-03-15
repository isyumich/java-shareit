package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.RequestBodyBookingDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;

public class BookingValidation {
    LocalDateTime currentDate = LocalDateTime.now();

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
        if (requestBooking.getStart() == null || requestBooking.getEnd() == null) {
            throw new ValidationException("Дата начала и дата окончания бронироваия не должны быть пустыми");
        }
        if (requestBooking.getStart().isAfter(requestBooking.getEnd()) || requestBooking.getStart().equals(requestBooking.getEnd())) {
            throw new ValidationException("Дата начала должна быть меньше, чем дата окончания");
        }
        if (requestBooking.getStart().isBefore(currentDate)) {
            throw new ValidationException("Дата начала должна быть больше текущей даты");
        }
    }

    private void bookerIsNotOwnerValidation(Long userId, Item item) {
        if (item.getOwner().getId() == userId) {
            throw new NotFoundException("Владелец вещи не может её бронировать");
        }
    }

}
