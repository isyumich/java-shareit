package ru.practicum.shareit.user.dto;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class RequestBodyUserDto {
    @NotBlank(groups = Create.class, message = "Имя не может быть пустым")
    String name;
    @Email(groups = Create.class, message = "Email должно содержать символ @")
    @NotBlank(groups = Create.class, message = "Email не должен быть пустым")
    String email;
}
