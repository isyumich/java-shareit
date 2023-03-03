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
            throw new ValidationException("Данная вещь недоступна для бронирования");
        }
    }

    private void startOrEndDateValidation(RequestBodyBookingDto requestBooking) {
        if (requestBooking.getStart().isAfter(requestBooking.getEnd())) {
            throw new ValidationException("Дата начала бронирования должна быть меньше даты окончания");
        }
        if (requestBooking.getStart().isBefore(currentDate)) {
            throw new ValidationException("Дата начала бронирования не должна быть меньше текущей даты");
        }
    }

    private void bookerIsNotOwnerValidation(Long userId, Item item) {
        if (item.getOwner().getId() == userId) {
            throw new NotFoundException("Владелец вещи не может её бронировать");
        }
    }

}
