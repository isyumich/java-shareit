package ru.practicum.shareit.user.repository;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void beforeEach() {
        userRepository.save(User.builder().name("userName1").email("userEmail1@mail.ru").build());
        userRepository.save(User.builder().name("userName2").email("userEmail2@mail.ru").build());
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
    }

    @Test
    void saveUsersTest() {
        assertEquals(userRepository.findAll().size(), 2);
    }

}
