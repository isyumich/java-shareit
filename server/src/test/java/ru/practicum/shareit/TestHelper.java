package ru.practicum.shareit;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item_request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TestHelper {
    User user = User.builder().name("userName1").email("userEmail1@mail.ru").build();
    User secondUser = User.builder().name("userName2").email("userEmail2@mail.ru").build();
    User author = User.builder().id(1L).name("author").email("author@mail.ru").build();
    User owner = User.builder().id(2L).name("owner").email("owner@mail.ru").build();
    User booker = User.builder().id(2L).name("booker").email("booker@mail.ru").build();
    ItemRequest itemRequest = ItemRequest.builder().id(1L).created(LocalDateTime.now()).description("itemRequestDesc").build();
    Item item = Item.builder().id(1L).available(true).name("itemName").description("itemDesc").build();
    Comment comment = Comment.builder().id(1L).text("commentText").createDate(LocalDateTime.now()).build();
    Booking booking = Booking.builder().id(1L).status(BookingStatus.APPROVED)
            .start(LocalDateTime.now().minus(10, ChronoUnit.DAYS)).end(LocalDateTime.now().minus(3, ChronoUnit.DAYS)).build();
}
