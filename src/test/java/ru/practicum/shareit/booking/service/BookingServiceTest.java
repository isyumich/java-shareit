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
import ru.practicum.shareit.exception.IllegalArgumentException;
import ru.practicum.shareit.exception.IsAlreadyDoneException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item_request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
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
        item = Item.builder().id(1L).name("itemName1").description("itemDesc1").owner(owner).available(true).itemRequest(null).build();
        requestBodyBookingDto = RequestBodyBookingDto.builder().itemId(0L).start(LocalDateTime.now())
                .end(LocalDateTime.now().plus(1, ChronoUnit.DAYS)).build();
        booking = Booking.builder().id(1L).start(LocalDateTime.now()).item(null).booker(booker)
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
    void addBookingTest_whenItemIsNotAvailable_thenThrowException() {
        long userId = 1L;
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        item.setAvailable(false);

        assertThrows(ValidationException.class,
                () -> bookingServiceImpl.addNewBooking(requestBodyBookingDto, userId));
    }

    @Test
    void addBookingTest_whenStartIsNull_thenThrowException() {
        long userId = 1L;
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        requestBodyBookingDto.setStart(null);

        assertThrows(ValidationException.class,
                () -> bookingServiceImpl.addNewBooking(requestBodyBookingDto, userId));
    }

    @Test
    void addBookingTest_whenEndIsNull_thenThrowException() {
        long userId = 1L;
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        requestBodyBookingDto.setEnd(null);

        assertThrows(ValidationException.class,
                () -> bookingServiceImpl.addNewBooking(requestBodyBookingDto, userId));
    }

    @Test
    void addBookingTest_whenStartMoreEnd_thenThrowException() {
        long userId = 1L;
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        requestBodyBookingDto.setStart(LocalDateTime.now().plus(100, ChronoUnit.DAYS));

        assertThrows(ValidationException.class,
                () -> bookingServiceImpl.addNewBooking(requestBodyBookingDto, userId));
    }

    @Test
    void addBookingTest_whenStartEqualsEnd_thenThrowException() {
        long userId = 1L;
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        requestBodyBookingDto.setStart(requestBodyBookingDto.getEnd());

        assertThrows(ValidationException.class,
                () -> bookingServiceImpl.addNewBooking(requestBodyBookingDto, userId));
    }

    @Test
    void addBookingTest_whenStartLessNow_thenThrowException() {
        long userId = 1L;
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        requestBodyBookingDto.setStart(LocalDateTime.now().minus(100, ChronoUnit.DAYS));

        assertThrows(ValidationException.class,
                () -> bookingServiceImpl.addNewBooking(requestBodyBookingDto, userId));
    }

    @Test
    void addBookingTest_whenBookerIsOwner_thenThrowException() {
        long userId = 2L;
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));


        assertThrows(NotFoundException.class,
                () -> bookingServiceImpl.addNewBooking(requestBodyBookingDto, userId));
    }

    @Test
    void addBookingTest_whenItemNotFound_thenThrowException() {
        long userId = 1L;
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));

        assertThrows(NotFoundException.class,
                () -> bookingServiceImpl.addNewBooking(requestBodyBookingDto, userId));
    }

    @Test
    void addBookingTest_whenBookerNotFound_thenThrowException() {
        long userId = 1L;
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingServiceImpl.addNewBooking(requestBodyBookingDto, userId));
    }

    @Test
    void approvedBookingTest_whenIsAlreadyApproved_thenThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        booking.setItem(item);

        assertThrows(IsAlreadyDoneException.class,
                () -> bookingServiceImpl.approveOrRejectBooking(2L, 1L, true));
    }

    @Test
    void approvedBookingTest_whenWaiting_thenApprove() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        bookingServiceImpl.approveOrRejectBooking(2L, 1L, true);

        verify(bookingRepository).save(any());
    }

    @Test
    void rejectedBookingTest_whenWaiting_thenReject() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        booking.setStatus(BookingStatus.WAITING);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        bookingServiceImpl.approveOrRejectBooking(2L, 1L, false);
        verify(bookingRepository).save(any());
    }

    @Test
    void approvedBookingTest_whenIsAlreadyRejected_thenThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        booking.setStatus(BookingStatus.REJECTED);
        booking.setItem(item);

        assertThrows(IsAlreadyDoneException.class,
                () -> bookingServiceImpl.approveOrRejectBooking(2L, 1L, false));
    }

    @Test
    void approvedBookingTest_whenBooingIsNotFound_thenThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        assertThrows(NotFoundException.class,
                () -> bookingServiceImpl.approveOrRejectBooking(2L, 1L, false));
    }

    @Test
    void approvedBookingTest_whenNotOwner_thenThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        booking.setStatus(BookingStatus.REJECTED);
        booking.setItem(item);

        assertThrows(NotFoundException.class,
                () -> bookingServiceImpl.approveOrRejectBooking(4L, 1L, false));
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
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        verify(bookingRepository, never()).findById(anyLong());
        assertThrows(NotFoundException.class, () -> bookingServiceImpl.getBookingById(2L, 1L));
    }

    @Test
    void getBookingByIdTest_whenUserNotBookerOrNotOwner_thenThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        booking.setItem(item);

        verify(bookingRepository, never()).findById(anyLong());
        assertThrows(NotFoundException.class, () -> bookingServiceImpl.getBookingById(10L, 1L));
    }

    @Test
    void getBookingCurrentUserTest_whenStateIllegal_thenThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        assertThrows(IllegalArgumentException.class,
                () -> bookingServiceImpl.getBookingCurrentUser(2L, "State", 1, 1));
    }

    @Test
    void getBookingCurrentUserTest_whenSizeIllegal_thenThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        assertThrows(ValidationException.class,
                () -> bookingServiceImpl.getBookingCurrentUser(2L, "ALL", 1, -1));
    }

    @Test
    void getBookingCurrentUserTest_whenStateAll_thenBookings() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        List<BookingDto> result = bookingServiceImpl.getBookingCurrentUser(owner.getId(), BookingState.ALL.toString(), 1, 1);

        verify(bookingRepository).findAllBookingsForUser(any(), any());
        assertEquals(new ArrayList<>(), result);
    }

    @Test
    void getBookingCurrentUserTest_whenStateCurrent_thenBookings() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        List<BookingDto> result = bookingServiceImpl.getBookingCurrentUser(owner.getId(), BookingState.CURRENT.toString(), 1, 1);

        verify(bookingRepository).findCurrentBookingsForUser(any(), any(), any(), any());
        assertEquals(new ArrayList<>(), result);
    }

    @Test
    void getBookingCurrentUserTest_whenStatePast_thenBookings() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        List<BookingDto> result = bookingServiceImpl.getBookingCurrentUser(owner.getId(), BookingState.PAST.toString(), 1, 1);

        verify(bookingRepository).findPastBookingsForUser(any(), any(), any());
        assertEquals(new ArrayList<>(), result);
    }

    @Test
    void getBookingCurrentUserTest_whenStateFuture_thenBookings() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        List<BookingDto> result = bookingServiceImpl.getBookingCurrentUser(owner.getId(), BookingState.FUTURE.toString(), 1, 1);

        verify(bookingRepository).findFutureBookingsForUser(any(), any(), any());
        assertEquals(new ArrayList<>(), result);
    }

    @Test
    void getBookingCurrentUserTest_whenStateWaiting_thenBookings() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        List<BookingDto> result = bookingServiceImpl.getBookingCurrentUser(owner.getId(), BookingState.WAITING.toString(), 1, 1);

        verify(bookingRepository).findWaitingOrRejectedBookingsForUser(any(), any(), any());
        assertEquals(new ArrayList<>(), result);
    }

    @Test
    void getBookingCurrentUserTest_whenStateRejected_thenBookings() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        List<BookingDto> result = bookingServiceImpl.getBookingCurrentUser(owner.getId(), BookingState.REJECTED.toString(), 1, 1);

        verify(bookingRepository).findWaitingOrRejectedBookingsForUser(any(), any(), any());
        assertEquals(new ArrayList<>(), result);
    }

    @Test
    void getBookingItemsCurrentUserTest_whenStateIllegal_thenThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        assertThrows(IllegalArgumentException.class,
                () -> bookingServiceImpl.getBookingForItemsCurrentUser(2L, "State", 1, 1));
    }

    @Test
    void getBookingItemsCurrentUserTest_whenStateAll_thenBookings() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        List<BookingDto> result = bookingServiceImpl.getBookingForItemsCurrentUser(owner.getId(), BookingState.ALL.toString(), 1, 1);

        verify(bookingRepository).findAllBookingsForItems(any(), any());
        assertEquals(new ArrayList<>(), result);
    }

    @Test
    void getBookingItemsCurrentUserTest_whenStateCurrent_thenBookings() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        List<BookingDto> result = bookingServiceImpl.getBookingForItemsCurrentUser(owner.getId(), BookingState.CURRENT.toString(), 1, 1);

        verify(bookingRepository).findCurrentBookingsForItems(any(), any(), any(), any());
        assertEquals(new ArrayList<>(), result);
    }

    @Test
    void getBookingItemsCurrentUserTest_whenStatePast_thenBookings() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        List<BookingDto> result = bookingServiceImpl.getBookingForItemsCurrentUser(owner.getId(), BookingState.PAST.toString(), 1, 1);

        verify(bookingRepository).findPastBookingsForItems(any(), any(), any());
        assertEquals(new ArrayList<>(), result);
    }

    @Test
    void getBookingItemsCurrentUserTest_whenStateFuture_thenBookings() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        List<BookingDto> result = bookingServiceImpl.getBookingForItemsCurrentUser(owner.getId(), BookingState.FUTURE.toString(), 1, 1);

        verify(bookingRepository).findFutureBookingsForItems(any(), any(), any());
        assertEquals(new ArrayList<>(), result);
    }

    @Test
    void getBookingItemsCurrentUserTest_whenStateWaiting_thenBookings() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        List<BookingDto> result = bookingServiceImpl.getBookingForItemsCurrentUser(owner.getId(), BookingState.WAITING.toString(), 1, 1);

        verify(bookingRepository).findWaitingOrRejectedBookingsForItems(any(), any(), any());
        assertEquals(new ArrayList<>(), result);
    }

    @Test
    void getBookingItemsCurrentUserTest_whenStateRejected_thenBookings() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        List<BookingDto> result = bookingServiceImpl.getBookingForItemsCurrentUser(owner.getId(), BookingState.REJECTED.toString(), 1, 1);

        verify(bookingRepository).findWaitingOrRejectedBookingsForItems(any(), any(), any());
        assertEquals(new ArrayList<>(), result);
    }
}
