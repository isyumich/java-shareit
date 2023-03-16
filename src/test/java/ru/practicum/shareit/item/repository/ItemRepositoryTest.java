package ru.practicum.shareit.item.repository;

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
public class ItemRepositoryTest {
    @Autowired
    ItemRepository itemRepository;
    final TestHelper testHelper = new TestHelper();

    @BeforeEach
    void beforeEach() {
        itemRepository.save(testHelper.getItem());
    }

    @AfterEach
    void afterEach() {
        itemRepository.deleteAll();
    }

    @Test
    void saveItemsTest() {
        assertEquals(itemRepository.findAll().size(), 1);
    }
}
