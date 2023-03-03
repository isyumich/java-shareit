package ru.practicum.shareit.user.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Qualifier("UserServiceImpl")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {
    final UserRepository userRepository;
    final UserValidation userValidation = new UserValidation();

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto addUser(User user) {
        if (!userValidation.userValidation(user)) {
            log.info("Поля заполнены неверно");
            throw new ValidationException("Поля заполнены неверно");
        }
        return UserDtoMapper.mapRow(userRepository.save(user));
    }

    @Override
    public UserDto updateUser(User user, long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            log.info(String.format("%s %d %s", "Пользователь с id =", userId, "не найден"));
            throw new NotFoundException(String.format("%s %d %s", "Пользователь с id =", userId, "не найден"));
        }
        user.setId(userId);
        User checkedUser = checkFieldsForUpdate(user);
        return UserDtoMapper.mapRow(userRepository.save(checkedUser));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return usersToUsersDto(userRepository.findAll());
    }

    @Override
    public UserDto getUserById(long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            log.debug(String.format("%s %d %s", "Пользователь с id =", userId, "не найден"));
            throw new NotFoundException(String.format("%s %d %s", "Пользователь с id =", userId, "не найден"));
        }
        return UserDtoMapper.mapRow(userRepository.findById(userId).get());
    }

    @Override
    public void deleteUser(long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            log.info(String.format("%s %d %s", "Пользователь с id =", userId, "не найден"));
            throw new NotFoundException(String.format("%s %d %s", "Пользователь с id =", userId, "не найден"));
        }
        userRepository.deleteById(userId);
        log.info(String.format("%s %d %s", "Пользователь с id =", userId, "удалён"));
    }

    private User checkFieldsForUpdate(User user) {
        User oldUser = userRepository.findById(user.getId()).get();
        if (user.getName() == null) {
            user.setName(oldUser.getName());
        }
        if (user.getEmail() == null) {
            user.setEmail(oldUser.getEmail());
        }
        return user;
    }

    private List<UserDto> usersToUsersDto(List<User> users) {
        List<UserDto> usersDto = new ArrayList<>();
        for (User user : users) {
            usersDto.add(UserDtoMapper.mapRow(user));
        }
        return usersDto;
    }
}
