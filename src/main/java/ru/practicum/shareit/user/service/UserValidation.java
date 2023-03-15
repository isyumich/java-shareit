package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

@Slf4j
public class UserValidation {
    public void userValidation(User user) {
        nameValidation(user);
        emailValidation(user);
    }

    private void nameValidation(User user) {
        String message = "Имя не может быть пустым";
        if (user.getName() == null || user.getName().equals("") || user.getName().equals(" ")) {
            log.info(message);
            throw new ValidationException(message);
        }
    }

    private void emailValidation(User user) {
        String emptyEmailMessage = "Email не может быть пустым";
        String atSignNotFoundMessage = "Поле Email должно содержать символ @";
        if (user.getEmail() == null || user.getEmail().equals("") || user.getEmail().equals(" ")) {
            log.info(emptyEmailMessage);
            throw new ValidationException(emptyEmailMessage);
        }
        if (!user.getEmail().contains("@")) {
            log.info(atSignNotFoundMessage);
            throw new ValidationException(atSignNotFoundMessage);
        }
    }
}
