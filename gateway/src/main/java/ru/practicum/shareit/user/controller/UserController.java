package ru.practicum.shareit.user.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.RequestBodyUserDto;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import javax.validation.Valid;

@Controller
@Validated
@RequestMapping(path = "/users")
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class UserController {
    final UserClient userClient;

    final String pathUserId = "/{userId}";

    @Validated(Create.class)
    @PostMapping
    public ResponseEntity<Object> addNewUser(@Valid @RequestBody RequestBodyUserDto requestBodyUserDto) {
        return userClient.addNewUser(requestBodyUserDto);
    }

    @Validated(Update.class)
    @PatchMapping(pathUserId)
    public ResponseEntity<Object> updateUser(@PathVariable long userId,
                                             @Valid @RequestBody RequestBodyUserDto requestBodyUserDto) {
        return userClient.updateUser(requestBodyUserDto, userId);
    }


    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        return userClient.getAllUsers();
    }

    @GetMapping(pathUserId)
    public ResponseEntity<Object> getUserById(@PathVariable long userId) {
        return userClient.getUserById(userId);
    }


    @DeleteMapping(pathUserId)
    public ResponseEntity<Object> deleteUser(@PathVariable long userId) {
        return userClient.deleteUser(userId);
    }
}
