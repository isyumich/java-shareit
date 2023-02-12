package ru.practicum.shareit.user.repository;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.IsAlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
@Qualifier("InMemoryUserRepository")
public class InMemoryUserRepository implements UserRepository {
    long nextId = 1;
    final Map<Long, User> users = new HashMap<>();

    @Override
    public UserDto addUser(User user) {
        if (checkDuplicateEmail(user.getEmail())) {
            user.setId(nextId++);
            users.put(user.getId(), user);
            log.info(String.format("%s %d %s", "Пользователь с id =", user.getId(), "добавлен"));
            return UserDtoMapper.mapRow(user);
        } else {
            log.info("Уже существует пользователь с таким email");
            throw new IsAlreadyExistsException("Уже существует пользователь с таким email");
        }
    }

    @Override
    public UserDto updateUser(User user, long userId) {
        if (!users.containsKey(userId)) {
            log.debug(String.format("%s %d %s", "Пользователь с id =", userId, "не найден"));
            throw new NotFoundException(String.format("%s %d %s", "Пользователь с id = ", userId, "не найден"));
        }
        user.setId(userId);
        User checkedUser = checkFieldsForUpdate(user);
        users.put(userId, checkedUser);
        log.info(String.format("%s %d %s", "Пользователь с id =", userId, "обновлен"));
        return UserDtoMapper.mapRow(checkedUser);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<UserDto> usersDto = new ArrayList<>();
        for (User user : users.values()) {
            usersDto.add(UserDtoMapper.mapRow(user));
        }
        log.info("Список пользователей успешно выведен");
        return usersDto;
    }

    @Override
    public UserDto getUserById(long userId) {
        if (!users.containsKey(userId)) {
            log.debug(String.format("%s %d %s", "Пользователь с id =", userId, "не найден"));
            throw new NotFoundException(String.format("%s %d %s", "Пользователь с id = ", userId, "не найден"));
        } else {
            log.info(String.format("%s %d %s", "Пользователь с id =", userId, "выведен"));
            return UserDtoMapper.mapRow(users.get(userId));
        }
    }

    @Override
    public void deleteUser(long userId) {
        if (!users.containsKey(userId)) {
            log.debug(String.format("%s %d %s", "Пользователь с id =", userId, "не найден"));
            throw new NotFoundException(String.format("%s %d %s", "Пользователь с id = ", userId, "не найден"));
        } else {
            users.remove(userId);
            log.info(String.format("%s %d %s", "Пользователь с id =", userId, "удалён"));
        }
    }

    private boolean checkDuplicateEmail(String email) {
        int counter = 0;
        for (User user : users.values()) {
            if (user.getEmail().equals(email)) {
                counter++;
                break;
            }
        }
        return counter <= 0;
    }

    private User checkFieldsForUpdate(User user) {
        if (user.getName() == null) {
            user.setName(users.get(user.getId()).getName());
        }
        if (user.getEmail() == null) {
            user.setEmail(users.get(user.getId()).getEmail());
        } else {
            for (User oldUser : users.values()) {
                if (oldUser.getEmail().equals(user.getEmail()) && oldUser.getId() != user.getId()) {
                    throw new IsAlreadyExistsException("Такой email используется другим пользователем");
                }
            }
        }
        return user;
    }
}
