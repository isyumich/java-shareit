package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.LastAndNextBookingDto;

import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {
    long id;
    String name;
    String description;
    Boolean available;
    LastAndNextBookingDto lastBooking;
    LastAndNextBookingDto nextBooking;
    List<CommentDto> comments;
}
