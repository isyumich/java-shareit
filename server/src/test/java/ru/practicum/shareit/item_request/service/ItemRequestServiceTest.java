package ru.practicum.shareit.item_request.service;

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
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item_request.dto.ItemRequestDto;
import ru.practicum.shareit.item_request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.item_request.dto.RequestBodyItemRequestDto;
import ru.practicum.shareit.item_request.model.ItemRequest;
import ru.practicum.shareit.item_request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestServiceTest {
    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    @InjectMocks
    ItemRequestServiceImpl itemRequestServiceimpl;
    User author;
    User owner;
    Item item;
    ItemRequest itemRequest;
    RequestBodyItemRequestDto requestBodyItemRequestDto;
    ItemRequestDto itemRequestDto;
    final TestHelper testHelper = new TestHelper();

    @BeforeEach
    void beforeEach() {
        author = testHelper.getAuthor();
        owner = testHelper.getOwner();
        itemRequest = testHelper.getItemRequest();
        requestBodyItemRequestDto = RequestBodyItemRequestDto.builder().description(itemRequest.getDescription()).build();
        itemRequestDto = ItemRequestDtoMapper.mapRow(itemRequest);
        item = testHelper.getItem();
        when(itemRequestRepository.save(any())).thenAnswer(input -> input.getArguments()[0]);
        item.setItemRequest(itemRequest);
    }

    @Test
    void addItemRequestTest_whenItemRequestCorrect_thenSave() {
        itemRequestDto.setItems(new ArrayList<>());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(author));

        ItemRequestDto result = itemRequestServiceimpl.addNewItemRequest(2L, requestBodyItemRequestDto);

        verify(itemRequestRepository).save(any());
        assertNotNull(result);
        assertEquals(itemRequestDto.getDescription(), result.getDescription());
        assertEquals(itemRequestDto.getItems(), result.getItems());
    }

    @Test
    void getItemRequestByIdTest_whenItemRequestPresent_thenItemRequest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(author));
        when(itemRequestRepository.findById(any())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findItemsByRequests(anyLong())).thenReturn(List.of(item));

        ItemRequestDto result = itemRequestServiceimpl.getRequestById(2L, 1L);

        verify(itemRepository, times(2)).findItemsByRequests(anyLong());
        itemRequestDto.setItems(List.of(ItemDtoMapper.mapRow(item)));
        assertEquals(itemRequestDto, result);
    }

    @Test
    void getItemRequestByIdTest_whenItemRequestNotFound_thenThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(author));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        verify(itemRepository, never()).findItemsByRequests(anyLong());
        assertThrows(NotFoundException.class, () -> itemRequestServiceimpl.getRequestById(2L, 2L));
    }

    @Test
    void getItemRequestsTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        List<ItemRequestDto> result = itemRequestServiceimpl.getAllItemRequests(1, 1, owner.getId());

        verify(itemRequestRepository).findAllItemRequests(any(), any());
        assertEquals(new ArrayList<>(), result);
    }

    @Test
    void getOwnItemRequestsTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        List<ItemRequestDto> result = itemRequestServiceimpl.getOwnItemRequests(owner.getId());

        verify(itemRequestRepository).findItemRequestsByAuthor(any());
        assertEquals(new ArrayList<>(), result);
    }

}
