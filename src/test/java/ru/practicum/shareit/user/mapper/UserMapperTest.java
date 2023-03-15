package ru.practicum.shareit.user.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserMapperTest {
    final TestHelper testHelper = new TestHelper();
    @Test
    public void toUserDtoTest() {
        User user = testHelper.getUser();
        user.setId(1L);

        UserDto userDto = UserDtoMapper.mapRow(user);

        assertEquals(1L, userDto.getId());
        assertEquals("userName1", userDto.getName());
        assertEquals("userEmail1@mail.ru", userDto.getEmail());
    }
}
