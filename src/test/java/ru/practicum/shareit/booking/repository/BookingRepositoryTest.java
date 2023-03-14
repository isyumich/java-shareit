package ru.practicum.shareit.booking.repository;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingRepositoryTest {
    @Autowired
    BookingRepository bookingRepository;

    @BeforeEach
    void beforeEach() {
        bookingRepository.save(Booking.builder().start(LocalDateTime.now()).end(LocalDateTime.now()
                .plus(1, ChronoUnit.DAYS)).status(BookingStatus.APPROVED).build());
        bookingRepository.save(Booking.builder().start(LocalDateTime.now().plus(3, ChronoUnit.DAYS)).end(LocalDateTime.now()
                .plus(1, ChronoUnit.DAYS)).status(BookingStatus.APPROVED).build());
    }

    @AfterEach
    void afterEach() {
        bookingRepository.deleteAll();
    }

    @Test
    void saveBookingsTest() {
        assertEquals(bookingRepository.findAll().size(), 2);
    }
}
