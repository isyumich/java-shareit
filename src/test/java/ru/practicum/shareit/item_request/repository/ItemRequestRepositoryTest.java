package ru.practicum.shareit.item_request.repository;

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
public class ItemRequestRepositoryTest {
    @Autowired
    ItemRequestRepository itemRequestRepository;
    final TestHelper testHelper = new TestHelper();

    @BeforeEach
    void beforeEach() {
        itemRequestRepository.save(testHelper.getItemRequest());
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
