package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query(value = "select b from Booking b where b.booker = :user order by b.start desc")
    List<Booking> findAllBookingsForUser(User user, Pageable pageable);

    @Query(value = "select b from Booking b where b.booker = :user and b.start < :start and b.end > :end order by b.start desc")
    List<Booking> findCurrentBookingsForUser(User user, LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query(value = "select b from Booking b where b.booker = :user and b.end < :end order by b.start desc")
    List<Booking> findPastBookingsForUser(User user, LocalDateTime end, Pageable pageable);

    @Query(value = "select b from Booking b where b.booker = :user and b.start > :start order by b.start desc")
    List<Booking> findFutureBookingsForUser(User user, LocalDateTime start, Pageable pageable);

    @Query(value = "select b from Booking b where b.booker = :user and b.status = :status order by b.start desc")
    List<Booking> findWaitingOrRejectedBookingsForUser(User user, BookingStatus status, Pageable pageable);

    @Query(value = "select b from Booking b where b.item in :items order by b.start desc")
    List<Booking> findAllBookingsForItems(List<Item> items, Pageable pageable);

    @Query(value = "select b from Booking b where b.item in :items and b.start < :start and b.end > :end order by b.start desc")
    List<Booking> findCurrentBookingsForItems(List<Item> items, LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query(value = "select b from Booking b where b.item in :items and b.end < :end order by b.start desc")
    List<Booking> findPastBookingsForItems(List<Item> items, LocalDateTime end, Pageable pageable);

    @Query(value = "select b from Booking b where b.item in :items and b.start > :start order by b.start desc")
    List<Booking> findFutureBookingsForItems(List<Item> items, LocalDateTime start, Pageable pageable);

    @Query(value = "select b from Booking b where b.item in :items and b.status = :status")
    List<Booking> findWaitingOrRejectedBookingsForItems(List<Item> items, BookingStatus status, Pageable pageable);

    @Query(value = "select * from bookings b " +
            "left join items i on b.item_id = i.id " +
            "left join users u on b.booker_id = u.id " +
            "where b.item_id = :item_id " +
            "and b.start_date < :start " +
            "and b.booking_status = :status " +
            "order by b.start_date desc " +
            "limit 1", nativeQuery = true)
    Booking findLastBooking(long item_id, LocalDateTime start, String status);

    @Query(value = "select * from bookings b " +
            "left join items i on b.item_id = i.id " +
            "left join users u on b.booker_id = u.id " +
            "where b.item_id = :item_id " +
            "and b.start_date > :start " +
            "and b.booking_status = :status " +
            "order by b.start_date asc " +
            "limit 1", nativeQuery = true)
    Booking findNextBooking(long item_id, LocalDateTime start, String status);

    @Query(value = "select b from Booking b where b.item = :item and b.booker = :user and b.status = :status and b.end < :end")
    List<Booking> FindPastBookingsForUserAndItem(Item item, User user, BookingStatus status, LocalDateTime end);
}