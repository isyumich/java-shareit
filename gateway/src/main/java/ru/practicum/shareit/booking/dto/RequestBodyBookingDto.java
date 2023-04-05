package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.validation.Create;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class RequestBodyBookingDto {
    @NotNull(groups = Create.class, message = "Идентификатор вещи должен быть указан")
    long itemId;
    @NotNull(groups = Create.class, message = "Дата начала должна быть указана")
    @FutureOrPresent(message = "Дата начала не может быть меньше текущей")
    LocalDateTime start;
    @NotNull(groups = Create.class, message = "Дата окончания должна быть указана")
    @Future(message = "Дата окочания не может быть равна или меньше текущей")
    LocalDateTime end;
}
