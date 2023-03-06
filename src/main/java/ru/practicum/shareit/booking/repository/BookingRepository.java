package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findBookingsByBookerIsOrderByStartDesc(User user);

    List<Booking> findBookingsByBookerIsAndStartBeforeAndEndAfterOrderByStartDesc(User user, LocalDateTime start, LocalDateTime end);

    List<Booking> findBookingsByBookerIsAndEndBeforeOrderByStartDesc(User user, LocalDateTime start);

    List<Booking> findBookingsByBookerIsAndStartAfterOrderByStartDesc(User user, LocalDateTime end);

    List<Booking> findBookingsByBookerIsAndStatusOrderByStartDesc(User user, BookingStatus state);

    List<Booking> findBookingsByItemInOrderByStartDesc(List<Item> items);

    List<Booking> findBookingsByItemInAndStartBeforeAndEndAfterOrderByStartDesc(List<Item> items, LocalDateTime start, LocalDateTime end);

    List<Booking> findBookingsByItemInAndEndBeforeOrderByStartDesc(List<Item> items, LocalDateTime start);

    List<Booking> findBookingsByItemInAndStartAfterOrderByStartDesc(List<Item> items, LocalDateTime end);

    List<Booking> findBookingsByItemInAndStatusOrderByStartDesc(List<Item> items, BookingStatus state);

    List<Booking> findBookingsByItemIsAndEndBeforeAndStatusOrderByEndDesc(Item item, LocalDateTime end, BookingStatus status);

    List<Booking> findBookingsByItemIsAndStartAfterAndStatusOrderByEndAsc(Item item, LocalDateTime end, BookingStatus status);

    List<Booking> findBookingsByItemIsAndBookerIsAndStatus(Item item, User booker, BookingStatus status);

    List<Booking> findBookingsByItemIsAndBookerIsAndStatusAndEndBefore(Item item, User booker, BookingStatus status, LocalDateTime end);
}