package ru.practicum.shareit.item_request.dto;

import lombok.Data;
import lombok.NonNull;

@Data
public class RequestBodyItemRequestDto {
    @NonNull
    String description;
}
