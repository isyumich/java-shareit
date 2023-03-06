package ru.practicum.shareit.booking.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.dto.RequestBodyBookingDto;
import ru.practicum.shareit.booking.dto.RequestBodyBookingDtoMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.IllegalArgumentException;
import ru.practicum.shareit.exception.IsAlreadyDoneException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Qualifier("BookingServiceImpl")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingServiceImpl implements BookingService {
    final BookingRepository bookingRepository;
    final ItemRepository itemRepository;
    final UserRepository userRepository;
    final BookingValidation bookingValidation = new BookingValidation();

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository,
                              UserRepository userRepository,
                              ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public BookingDto addNewBooking(RequestBodyBookingDto requestBooking, Long userId) {
        Item item = getItemById(requestBooking.getItemId());
        bookingValidation.bookingValidation(requestBooking, item, userId);
        User user = getUserById(userId);
        Booking booking = RequestBodyBookingDtoMapper.mapRow(requestBooking);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        return BookingDtoMapper.mapRow(bookingRepository.save(booking));
    }

    @Override
    public BookingDto approveOrRejectBooking(Long userId, long bookingId, boolean approved) {
        if (bookingRepository.findById(bookingId).isEmpty()) {
            String message = String.format("%s %d %s", "Бронирование с id =", bookingId, "не найдено");
            log.info(message);
            throw new NotFoundException(message);
        }
        Booking booking = bookingRepository.findById(bookingId).get();
        if (!userId.equals(booking.getItem().getOwner().getId())) {
            String message = "Подтверждать или отменять бронь может только владелец вещи";
            log.info(message);
            throw new NotFoundException(message);
        }
        if (approved) {
            if (booking.getStatus().equals(BookingStatus.APPROVED)) {
                String message = "Попытка подтверждения уже подтвержденного бронирования";
                log.info(message);
                throw new IsAlreadyDoneException(message);
            }
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            if (booking.getStatus().equals(BookingStatus.REJECTED)) {
                String message = "Попытка отмены уже отмененного бронирования";
                log.info(message);
                throw new IsAlreadyDoneException(message);
            }
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingDtoMapper.mapRow(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBookingById(Long userId, long bookingId) {
        if (bookingRepository.findById(bookingId).isEmpty()) {
            String message = String.format("%s %d %s", "Бронь с id =", bookingId, "не найдена");
            log.info(message);
            throw new NotFoundException(message);
        }
        Booking booking = bookingRepository.findById(bookingId).get();
        if (!userId.equals(booking.getBooker().getId()) && !userId.equals(booking.getItem().getOwner().getId())) {
            String message = "Просматривать бронь может либо автор брони либо владелец вещи";
            log.info(message);
            throw new NotFoundException(message);
        }
        return BookingDtoMapper.mapRow(booking);
    }

    @Override
    public List<BookingDto> getBookingCurrentUser(Long userId, String state) {
        LocalDateTime currentDate = LocalDateTime.now();
        User user = getUserById(userId);
        switch (getBookingStateValue(state)) {
            case ALL:
                return bookingsToBookingsDto(bookingRepository.findBookingsByBookerIsOrderByStartDesc(user));
            case CURRENT:
                return bookingsToBookingsDto(bookingRepository.findBookingsByBookerIsAndStartBeforeAndEndAfterOrderByStartDesc(user, currentDate, currentDate));
            case PAST:
                return bookingsToBookingsDto(bookingRepository.findBookingsByBookerIsAndEndBeforeOrderByStartDesc(user, currentDate));
            case FUTURE:
                return bookingsToBookingsDto(bookingRepository.findBookingsByBookerIsAndStartAfterOrderByStartDesc(user, currentDate));
            case WAITING:
                return bookingsToBookingsDto(bookingRepository.findBookingsByBookerIsAndStatusOrderByStartDesc(user, BookingStatus.WAITING));
            case REJECTED:
                return bookingsToBookingsDto(bookingRepository.findBookingsByBookerIsAndStatusOrderByStartDesc(user, BookingStatus.REJECTED));
            default:
                String message = "Указан неверный статус";
                log.info(message);
                throw new UnsupportedOperationException(message);
        }
    }

    @Override
    public List<BookingDto> getBookingForItemsCurrentUser(Long userId, String state) {
        LocalDateTime currentDate = LocalDateTime.now();
        User user = getUserById(userId);
        List<Item> items = itemRepository.findByOwnerEqualsOrderByIdAsc(user);

        switch (getBookingStateValue(state)) {
            case ALL:
                return bookingsToBookingsDto(bookingRepository.findBookingsByItemInOrderByStartDesc(items));
            case CURRENT:
                return bookingsToBookingsDto(bookingRepository.findBookingsByItemInAndStartBeforeAndEndAfterOrderByStartDesc(items, currentDate, currentDate));
            case PAST:
                return bookingsToBookingsDto(bookingRepository.findBookingsByItemInAndEndBeforeOrderByStartDesc(items, currentDate));
            case FUTURE:
                return bookingsToBookingsDto(bookingRepository.findBookingsByItemInAndStartAfterOrderByStartDesc(items, currentDate));
            case WAITING:
                return bookingsToBookingsDto(bookingRepository.findBookingsByItemInAndStatusOrderByStartDesc(items, BookingStatus.WAITING));
            case REJECTED:
                return bookingsToBookingsDto(bookingRepository.findBookingsByItemInAndStatusOrderByStartDesc(items, BookingStatus.REJECTED));
            default:
                String message = "Указан неверный статус";
                log.info(message);
                throw new UnsupportedOperationException(message);
        }
    }

    private BookingState getBookingStateValue(String state) {
        List<BookingState> states = List.of(BookingState.values());
        for (BookingState bookingState : states) {
            if (state.equals(bookingState.toString())) {
                return bookingState;
            }
        }
        throw new IllegalArgumentException("В поле state указано недопустимое значение");
    }

    private Item getItemById(long itemId) {
        if (itemRepository.findById(itemId).isEmpty()) {
            String message = String.format("%s %d %s", "Вещь с id =", itemId, "не найдена");
            log.info(message);
            throw new NotFoundException(message);
        }
        return itemRepository.findById(itemId).get();
    }

    private User getUserById(long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            String message = String.format("%s %d %s", "Пользователь с id =", userId, "не найден");
            log.info(message);
            throw new NotFoundException(message);
        }
        return userRepository.findById(userId).get();
    }

    private List<BookingDto> bookingsToBookingsDto(List<Booking> bookings) {
        List<BookingDto> bookingsDto = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingsDto.add(BookingDtoMapper.mapRow(booking));
        }
        return bookingsDto;
    }
}
