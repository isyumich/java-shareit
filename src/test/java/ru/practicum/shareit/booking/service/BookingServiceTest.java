package ru.practicum.shareit.booking.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.dto.RequestBodyBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item_request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingServiceTest {

    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    BookingRepository bookingRepository;
    @InjectMocks
    BookingServiceImpl bookingServiceImpl;
    User booker;
    User owner;
    Item item;
    Booking booking;
    RequestBodyBookingDto requestBodyBookingDto;
    BookingDto bookingDto;

    @BeforeEach
    void beforeEach() {
        booker = User.builder().id(1L).name("author").email("author@mail.ru").build();
        owner = User.builder().id(2L).name("owner").email("owner@mail.ru").build();
        item = Item.builder().id(1L).name("itemName1").description("itemDesc1").owner(owner).available(true).build();
        requestBodyBookingDto = RequestBodyBookingDto.builder().itemId(0L).start(LocalDateTime.now())
                .end(LocalDateTime.now().plus(1, ChronoUnit.DAYS)).build();
        booking = Booking.builder().id(1L).start(LocalDateTime.now()).booker(booker)
                .end(LocalDateTime.now().plus(1, ChronoUnit.DAYS)).status(BookingStatus.APPROVED).build();
        bookingDto = BookingDtoMapper.mapRow(booking);

        when(itemRequestRepository.save(any())).thenAnswer(input -> input.getArguments()[0]);
    }

    @Test
    void addBookingTest_whenCorrect_thenSave() {
        long userId = 1L;
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));

        bookingServiceImpl.addNewBooking(requestBodyBookingDto, userId);

        verify(bookingRepository).save(any());
    }

    @Test
    void getBookingByIdTest_whenBookingPresent_thenBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        BookingDto result = bookingServiceImpl.getBookingById(1L, 1L);

        verify(bookingRepository, times(2)).findById(anyLong());
        assertEquals(bookingDto, result);
    }

    @Test
    void getBookingByIdTest_whenBookingNotFound_thenThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        verify(bookingRepository, never()).findById(anyLong());
        assertThrows(NotFoundException.class, () -> bookingServiceImpl.getBookingById(2L, 1L));
    }
}
