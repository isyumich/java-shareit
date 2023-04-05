package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.validation.Create;

import javax.validation.constraints.NotBlank;

@Data
public class RequestBodyCommentDto {
    @NotBlank(groups = Create.class, message = "Текст комментария должен быть указан")
    String text;
}
