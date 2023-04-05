package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NonNull;

@Data
public class RequestBodyCommentDto {
    @NonNull
    String text;
}
