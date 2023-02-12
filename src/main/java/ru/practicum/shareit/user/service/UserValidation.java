package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

public class UserValidation {
    public boolean userValidation(User user) {
        return nameValidation(user) && mailValidation(user);
    }

    private boolean nameValidation(User user) {
        if (user.getName() == null) {
            return false;
        } else {
            return !user.getName().equals("") && !user.getName().equals(" ");
        }
    }

    private boolean mailValidation(User user) {
        if (user.getEmail() == null) {
            return false;
        } else {
            return !user.getEmail().equals("") && !user.getEmail().equals(" ") && user.getEmail().contains("@");
        }
    }
}
