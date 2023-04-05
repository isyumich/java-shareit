package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NonNull;

@Data
public class RequestBodyItemDto {
    @NonNull
    String name;
    @NonNull
    String description;
    @NonNull
    Boolean available;
    @NonNull
    Long requestId;
}
