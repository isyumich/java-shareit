package ru.practicum.shareit.user.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.exception.IsAlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
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
    User user;
    final TestHelper testHelper = new TestHelper();

    @BeforeEach
    void beforeEach() {
        when(userRepository.save(any())).thenAnswer(input -> input.getArguments()[0]);
        user = testHelper.getUser();
        user.setId(1L);
    }

    @Test
    void addUserTest_whenUserCorrect_thenSave() {
        when(userRepository.save(user)).thenReturn(user);
        UserDto userDto = userServiceImpl.addUser(user);

        verify(userRepository).save(user);
        assertEquals(UserDtoMapper.mapRow(user), userDto);
    }

    @Test
    void addUserTest_whenDuplicateEmailUser_thenThrowException() {
        when(userRepository.save(any()))
                .thenThrow(new IsAlreadyExistsException("Пользователь с таким email уже существует"));

        assertThrows(IsAlreadyExistsException.class,
                () -> userServiceImpl.addUser(user));
        verify(userRepository).save(user);
    }

    @Test
    void addUserTest_whenUserNameEmpty_thenThrowException() {
        when(userRepository.save(any()))
                .thenThrow(new ValidationException("Имя не может быть пустым"));
        user.setName("");

        assertThrows(ValidationException.class,
                () -> userServiceImpl.addUser(user));
        verify(userRepository, never()).save(user);
    }

    @Test
    void addUserTest_whenUserNameIsNull_thenThrowException() {
        when(userRepository.save(any()))
                .thenThrow(new ValidationException("Имя не может быть пустым"));
        user.setName(null);

        assertThrows(ValidationException.class,
                () -> userServiceImpl.addUser(user));
        verify(userRepository, never()).save(user);
    }

    @Test
    void addUserTest_whenUserNameSpace_thenThrowException() {
        when(userRepository.save(any()))
                .thenThrow(new ValidationException("Имя не может быть пустым"));
        user.setName(" ");

        assertThrows(ValidationException.class,
                () -> userServiceImpl.addUser(user));
        verify(userRepository, never()).save(user);
    }

    @Test
    void addUserTest_whenUserEmailEmpty_thenThrowException() {
        when(userRepository.save(any()))
                .thenThrow(new ValidationException("Email не может быть пустым"));
        user.setEmail("");

        assertThrows(ValidationException.class,
                () -> userServiceImpl.addUser(user));
        verify(userRepository, never()).save(user);
    }

    @Test
    void addUserTest_whenUserEmailIsNull_thenThrowException() {
        when(userRepository.save(any()))
                .thenThrow(new ValidationException("Email не может быть пустым"));
        user.setEmail(null);

        assertThrows(ValidationException.class,
                () -> userServiceImpl.addUser(user));
        verify(userRepository, never()).save(user);
    }

    @Test
    void addUserTest_whenUserEmailSpace_thenThrowException() {
        when(userRepository.save(any()))
                .thenThrow(new ValidationException("Email не может быть пустым"));
        user.setEmail(" ");

        assertThrows(ValidationException.class,
                () -> userServiceImpl.addUser(user));
        verify(userRepository, never()).save(user);
    }

    @Test
    void addUserTest_whenUserEmailWithoutSymbol_thenThrowException() {
        when(userRepository.save(any()))
                .thenThrow(new ValidationException("Поле Email должно содержать символ @"));
        user.setEmail("mail.ru");

        assertThrows(ValidationException.class,
                () -> userServiceImpl.addUser(user));
        verify(userRepository, never()).save(user);
    }


    @Test
    void updateItemTest_whenCorrect_thenUpdate() {
        long userId = 1L;
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        userServiceImpl.updateUser(user, userId);
        verify(userRepository).save(any());
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
        List<User> users = List.of(user);
        when(userRepository.findAll()).thenReturn(users);
        List<UserDto> usersDto = userServiceImpl.getAllUsers();
        verify(userRepository).findAll();
        assertEquals(1, usersDto.size());
        assertEquals(UserDtoMapper.mapRow(users.get(0)), usersDto.get(0));
    }

    @Test
    void getUserByIdTest_whenUserPresent_thenUser() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        UserDto userDto = UserDtoMapper.mapRow(user);
        UserDto userDtoFromDb = userServiceImpl.getUserById(userId);

        verify(userRepository, times(2)).findById(userId);
        assertEquals(userDto, userDtoFromDb);
    }

    @Test
    void getUserByIdTest_whenUserNotFound_thenThrowException() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userServiceImpl.getUserById(userId));
    }

    @Test
    void deleteUserById_deletes() {
        long userId = 1L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        userServiceImpl.deleteUser(userId);
        verify(userRepository).deleteById(userId);
    }
}
