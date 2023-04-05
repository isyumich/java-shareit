package ru.practicum.shareit.booking.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.RequestBodyBookingDto;
import ru.practicum.shareit.validation.Create;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@Validated
@RequestMapping(path = "/bookings")
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class BookingController {
    final BookingClient bookingClient;
    final static String HEADER_USER_VALUE = "X-Sharer-User-Id";
    final String pathBookingId = "/{bookingId}";

    @Validated(Create.class)
    @PostMapping
    public ResponseEntity<Object> addNewBooking(@RequestHeader(value = HEADER_USER_VALUE, required = false) Long userId,
                                                @Valid @RequestBody RequestBodyBookingDto requestBooking) {
        return bookingClient.addNewBooking(requestBooking, userId);
    }

    @PatchMapping(pathBookingId)
    public ResponseEntity<Object> approveOrRejectBooking(@RequestHeader(value = HEADER_USER_VALUE, required = false) Long userId,
                                                         @PathVariable long bookingId,
                                                         @RequestParam boolean approved) {
        return bookingClient.approveOrRejectBooking(userId, bookingId, approved);
    }

    @GetMapping(pathBookingId)
    public ResponseEntity<Object> getBookingById(@RequestHeader(value = HEADER_USER_VALUE, required = false) Long userId,
                                                 @PathVariable long bookingId) {
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingCurrentUser(@RequestHeader(value = HEADER_USER_VALUE, required = false) Long userId,
                                                        @RequestParam(defaultValue = "ALL") String state,
                                                        @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                        @Positive @RequestParam(defaultValue = "10") Integer size) {
        return bookingClient.getBookingCurrentUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingForItemsCurrentUser(@RequestHeader(value = HEADER_USER_VALUE, required = false) Long userId,
                                                                @RequestParam(defaultValue = "ALL") String state,
                                                                @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                                @Positive @RequestParam(defaultValue = "10") Integer size) {
        return bookingClient.getBookingForItemsCurrentUser(userId, state, from, size);
    }
}
