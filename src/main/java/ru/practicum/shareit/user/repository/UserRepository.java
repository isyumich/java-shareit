package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    UserDto addUser(User user);

    UserDto updateUser(User user, long userId);

    List<UserDto> getAllUsers();

    UserDto getUserById(long userId);

    void deleteUser(long userId);
}
