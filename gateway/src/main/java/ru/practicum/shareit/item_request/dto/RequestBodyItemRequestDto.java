package ru.practicum.shareit.item_request.dto;

import lombok.Data;
import ru.practicum.shareit.validation.Create;

import javax.validation.constraints.NotBlank;

@Data
public class RequestBodyItemRequestDto {
    @NotBlank(groups = Create.class, message = "Поле описание должно быть заполнено")
    String description;
}
