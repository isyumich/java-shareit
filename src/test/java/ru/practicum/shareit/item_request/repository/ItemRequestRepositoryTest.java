package ru.practicum.shareit.item_request.repository;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item_request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestRepositoryTest {
    @Autowired
    ItemRequestRepository itemRequestRepository;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void beforeEach() {
        User author = userRepository.save(User.builder().name("userName1").email("userEmail1@mail.ru").build());
        itemRequestRepository.save(ItemRequest.builder().author(author).created(LocalDateTime.now()).description("itemRequestDesc1").build());
    }

    @AfterEach
    void afterEach() {
        itemRequestRepository.deleteAll();
    }

    @Test
    void saveItemRequestsTest() {
        assertEquals(itemRequestRepository.findAll().size(), 1);
    }
}
