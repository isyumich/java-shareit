package ru.practicum.shareit.booking.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.RequestBodyBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/bookings")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingController {
    final BookingService bookingService;
    final String headerUserValue = "X-Sharer-User-Id";
    final String pathBookingId = "/{bookingId}";

    @Autowired
    public BookingController(@Qualifier("BookingServiceImpl") BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto addNewBooking(@RequestHeader(value = headerUserValue, required = false) Long userId,
                                    @RequestBody RequestBodyBookingDto requestBooking) {
        log.info("Запрос на новое бронирование");
        return bookingService.addNewBooking(requestBooking, userId);
    }

    @PatchMapping(pathBookingId)
    public BookingDto approveOrRejectBooking(@RequestHeader(value = headerUserValue, required = false) Long userId,
                                             @PathVariable long bookingId, @RequestParam boolean approved) {
        log.info("Запрос на подтверждение/отклонение бронирования");
        return bookingService.approveOrRejectBooking(userId, bookingId, approved);
    }

    @GetMapping(pathBookingId)
    public BookingDto getBookingById(@RequestHeader(value = headerUserValue, required = false) Long userId,
                                     @PathVariable long bookingId) {
        log.info(String.format("%s %d", "Запрос на вывод бронирования с id =", bookingId));
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getBookingCurrentUser(@RequestHeader(value = headerUserValue, required = false) Long userId,
                                                  @RequestParam(defaultValue = "ALL") String state) {
        log.info(String.format("%s %d", "Запрос на вывод всех бронирований с id пользователя = ", userId));
        return bookingService.getBookingCurrentUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingForItemsCurrentUser(@RequestHeader(value = headerUserValue, required = false) Long userId,
                                                          @RequestParam(defaultValue = "ALL") String state) {
        log.info(String.format("%s %d", "Запрос на вывод всех бронирований для вещей пользователя id = ", userId));
        return bookingService.getBookingForItemsCurrentUser(userId, state);
    }
}
