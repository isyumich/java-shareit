package ru.practicum.shareit.item.repository;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRepositoryTest {
    @Autowired
    ItemRepository itemRepository;

    @BeforeEach
    void beforeEach() {
        itemRepository.save(Item.builder().name("itemName1").description("itemDesc1").available(true).build());
        itemRepository.save(Item.builder().name("itemName2").description("itemDesc2").available(true).build());
    }

    @AfterEach
    void afterEach() {
        itemRepository.deleteAll();
    }

    @Test
    void saveItemsTest() {
        assertEquals(itemRepository.findAll().size(), 2);
    }
}
