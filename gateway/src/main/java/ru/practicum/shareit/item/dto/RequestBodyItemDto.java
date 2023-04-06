package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.validation.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class RequestBodyItemDto {
    @NotBlank(groups = Create.class, message = "Имя вещи должно быть указано")
    String name;
    @NotBlank(groups = Create.class, message = "Описание вещи должно быть указано")
    String description;
    @NotNull(groups = Create.class, message = "Доступность вещи должна быть указана")
    Boolean available;
    Long requestId;
}
