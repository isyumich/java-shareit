package ru.practicum.shareit.user.repository;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.TestHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;
    final TestHelper testHelper = new TestHelper();

    @BeforeEach
    void beforeEach() {
        userRepository.save(testHelper.getUser());
        userRepository.save(testHelper.getSecondUser());
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
