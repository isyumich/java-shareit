package ru.practicum.shareit.booking.repository;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.TestHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingRepositoryTest {
    @Autowired
    BookingRepository bookingRepository;
    final TestHelper testHelper = new TestHelper();

    @BeforeEach
    void beforeEach() {
        bookingRepository.save(testHelper.getBooking());
    }

    @AfterEach
    void afterEach() {
        bookingRepository.deleteAll();
    }

    @Test
    void saveBookingsTest() {
        assertEquals(bookingRepository.findAll().size(), 1);
    }
}
