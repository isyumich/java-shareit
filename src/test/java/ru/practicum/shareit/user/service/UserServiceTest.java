package ru.practicum.shareit.user.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.exception.IsAlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceTest {
    @Mock
    UserRepository userRepository;
    @InjectMocks
    UserServiceImpl userServiceImpl;

    @BeforeEach
    void beforeEach() {
        when(userRepository.save(any())).thenAnswer(input -> input.getArguments()[0]);
    }

    @Test
    void addUserTest_whenUserCorrect_thenSave() {
        long userId = 0L;
        User user = User.builder().id(userId).name("userName1").email("userEmail1@mail.ru").build();
        when(userRepository.save(user)).thenReturn(user);
        UserDto userDto = userServiceImpl.addUser(user);

        verify(userRepository).save(user);
        assertEquals(UserDtoMapper.mapRow(user), userDto);
    }

    @Test
    void addUserTest_whenDuplicateEmailUser_thenThrowException() {
        long userId = 0L;
        User user = User.builder().id(userId).name("userName1").email("userEmail1@mail.ru").build();
        when(userRepository.save(any()))
                .thenThrow(new IsAlreadyExistsException("A user with the same email already exists"));

        assertThrows(IsAlreadyExistsException.class,
                () -> userServiceImpl.addUser(user));
        verify(userRepository).save(user);
    }

    @Test
    void updateUserTest_whenRequestCorrect_thenReturnedUpdateUser() {
        long userId = 1L;
        User user = User.builder().id(userId).name("userName1").email("userEmail1@mail.ru").build();
        when(userRepository.findById(eq(userId))).thenReturn(Optional.of(user));
        User updatedUser = User.builder().id(userId).name("updatedUserName1").email("updatedUserEmail1@mail.ru").build();
        userServiceImpl.updateUser(updatedUser, userId);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User capturedUser = captor.getValue();
        assertEquals("updatedUserName1", capturedUser.getName());
        assertEquals("updatedUserEmail1@mail.ru", capturedUser.getEmail());
    }

    @Test
    void updateUserTest_whenUserNotFound_thenThrowException() {
        long userId = 999L;
        assertThrows(
                NotFoundException.class,
                () -> {
                    when(userRepository.findById(eq(userId))).thenReturn(Optional.empty());
                    userServiceImpl.getUserById(userId);
                }
        );
    }

    @Test
    void getAllUsersTest() {
        List<User> users = List.of(User.builder().name("userName1").email("userEmail1@mail.ru").build());
        when(userRepository.findAll()).thenReturn(users);
        List<UserDto> usersDto = userServiceImpl.getAllUsers();
        verify(userRepository).findAll();
        assertEquals(1, usersDto.size());
        assertEquals(UserDtoMapper.mapRow(users.get(0)), usersDto.get(0));
    }

    @Test
    void getUserByIdTest_whenUserPresent_thenUser() {
        long userId = 0L;
        User user = User.builder().name("userName1").email("userEmail1@mail.ru").build();
        UserDto userDto = UserDtoMapper.mapRow(user);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        UserDto userDtoFromDb = userServiceImpl.getUserById(userId);

        verify(userRepository, times(2)).findById(userId);
        assertEquals(userDto, userDtoFromDb);
    }

    @Test
    void getUserByIdTest_whenUserNotFound_thenThrowException() {
        long userId = 0L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userServiceImpl.getUserById(userId));
    }
}
