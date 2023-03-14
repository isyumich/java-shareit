package ru.practicum.shareit.item.service;

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
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.dto.RequestBodyItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item_request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemServiceTest {
    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentRepository commentRepository;

    @InjectMocks
    ItemServiceImpl itemServiceimpl;

    User owner;
    RequestBodyItemDto requestBodyItemDto;
    Item item;
    ItemDto itemDto;

    @BeforeEach
    void beforeEach() {
        owner = User.builder().id(0L).name("owner").email("owner@mail.ru").build();
        item = Item.builder().id(0L).name("itemName1").description("itemDesc1").available(true).build();
        itemDto = ItemDtoMapper.mapRow(item);
        requestBodyItemDto = RequestBodyItemDto.builder().name("itemName1").description("itemDesc1").available(true).build();
        when(itemRequestRepository.save(any())).thenAnswer(input -> input.getArguments()[0]);
    }

    @Test
    void addItemTest_whenItemCorrect_thenSave() {
        long userId = 0L;
        long itemId = 0L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        itemServiceimpl.addNewItem(requestBodyItemDto, userId);

        ArgumentCaptor<Item> captor = ArgumentCaptor.forClass(Item.class);
        verify(itemRepository).save(captor.capture());

        Item savedItem = captor.getValue();
        assertEquals(itemId, savedItem.getId());
        assertEquals(requestBodyItemDto.getName(), savedItem.getName());
        assertEquals(requestBodyItemDto.getDescription(), savedItem.getDescription());
        assertEquals(requestBodyItemDto.getAvailable(), savedItem.getAvailable());
        assertEquals(owner, savedItem.getOwner());
    }

    @Test
    void getItemByIdTest_whenItemPresent_thenItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        item.setOwner(owner);
        itemDto.setComments(new ArrayList<>());

        ItemDto result = itemServiceimpl.getItemById(0L, 0L);

        verify(itemRepository, times(2)).findById(anyLong());
        assertEquals(itemDto, result);
    }

    @Test
    void getItemByIdTest_whenItemNotFound_thenThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        verify(itemRepository, never()).findById(anyLong());
        assertThrows(NotFoundException.class, () -> itemServiceimpl.getItemById(2L, 1L));
    }

}
