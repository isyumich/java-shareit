package ru.practicum.shareit.user.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserController {
    final UserService userService;

    final String pathUserId = "/{userId}";

    @Autowired
    public UserController(@Qualifier("UserServiceImpl") UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto addNewUser(@RequestBody User user) {
        log.info("The request for adding a new user");
        return userService.addUser(user);
    }


    @PatchMapping(pathUserId)
    public UserDto updateUser(@PathVariable long userId, @RequestBody User user) {
        log.info(String.format("%s %d", "The request for changing the user with id =", userId));
        return userService.updateUser(user, userId);
    }


    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("The request for getting all users");
        return userService.getAllUsers();
    }

    @GetMapping(pathUserId)
    public UserDto getUserById(@PathVariable long userId) {
        log.info(String.format("%s %d", "The request for getting the user with id =", userId));
        return userService.getUserById(userId);
    }


    @DeleteMapping(pathUserId)
    public void deleteUser(@PathVariable long userId) {
        log.info(String.format("%s %d", "The request for deletion the user with id =", userId));
        userService.deleteUser(userId);
    }
}
