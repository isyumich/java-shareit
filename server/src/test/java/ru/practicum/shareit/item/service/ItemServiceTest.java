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
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.InternalServerException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item_request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
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
    final TestHelper testHelper = new TestHelper();

    User owner;
    User author;
    User booker;
    RequestBodyItemDto requestBodyItemDto;
    Item item;
    Booking booking;
    Comment comment;
    ItemDto itemDto;
    final long userId = 1L;
    final long itemId = 1L;

    @BeforeEach
    void beforeEach() {
        owner = testHelper.getOwner();
        author = testHelper.getAuthor();
        item = testHelper.getItem();
        item.setOwner(owner);
        comment = testHelper.getComment();
        comment.setItem(item);
        comment.setAuthor(author);
        itemDto = ItemDtoMapper.mapRow(item);
        booking = testHelper.getBooking();
        booking.setItem(item);
        booking.setBooker(booker);
        requestBodyItemDto = RequestBodyItemDto.builder().name(item.getName())
                .description(item.getDescription()).available(item.getAvailable()).build();
        when(itemRequestRepository.save(any())).thenAnswer(input -> input.getArguments()[0]);
    }

    @Test
    void addItemTest_whenItemCorrect_thenSave() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        itemServiceimpl.addNewItem(requestBodyItemDto, owner.getId());
        requestBodyItemDto.setRequestId(null);

        ArgumentCaptor<Item> captor = ArgumentCaptor.forClass(Item.class);
        verify(itemRepository).save(captor.capture());

        Item savedItem = captor.getValue();
        assertEquals(requestBodyItemDto.getName(), savedItem.getName());
        assertEquals(requestBodyItemDto.getDescription(), savedItem.getDescription());
        assertEquals(requestBodyItemDto.getAvailable(), savedItem.getAvailable());
        assertEquals(owner, savedItem.getOwner());
    }

    @Test
    void addCommentTest_whenBookingNotFound_thenThrowException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(author));

        assertThrows(
                ValidationException.class,
                () -> itemServiceimpl.addNewComment(comment, userId, itemId));

        verify(commentRepository, never()).save(any());
    }

    @Test
    void addCommentTest_whenTextIsEmpty_thenThrowException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(author));
        comment.setText("");

        assertThrows(
                ValidationException.class,
                () -> itemServiceimpl.addNewComment(comment, userId, itemId));

        verify(commentRepository, never()).save(any());
    }

    @Test
    void addCommentTest_whenTextIsNull_thenThrowException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(author));
        comment.setText(null);

        assertThrows(
                ValidationException.class,
                () -> itemServiceimpl.addNewComment(comment, userId, itemId));

        verify(commentRepository, never()).save(any());
    }

    @Test
    void addCommentTest_whenTextIsSpace_thenThrowException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(author));
        comment.setText(" ");

        assertThrows(
                ValidationException.class,
                () -> itemServiceimpl.addNewComment(comment, userId, itemId));

        verify(commentRepository, never()).save(any());
    }

    @Test
    void addCommentTest_whenItemNotFound_thenThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(author));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));


        assertThrows(
                NotFoundException.class,
                () -> itemServiceimpl.addNewComment(comment, userId, itemId));

        verify(commentRepository, never()).save(any());
    }

    @Test
    void addCommentTest_whenUserNotFound_thenThrowException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));


        assertThrows(
                NotFoundException.class,
                () -> itemServiceimpl.addNewComment(comment, userId, itemId));

        verify(commentRepository, never()).save(any());
    }

    @Test
    void addCommentTest_whenCommentCorrect_thenSave() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(author));
        when(bookingRepository.findPastBookingsForUserAndItem(any(), any(), any(), any())).thenReturn(List.of(booking));
        when(commentRepository.save(comment)).thenReturn(comment);
        CommentDto commentDto = itemServiceimpl.addNewComment(comment, userId, itemId);

        verify(commentRepository).save(comment);
        assertEquals(CommentDtoMapper.mapRow(comment), commentDto);
    }

    @Test
    void updateItemTest_whenCorrect_thenUpdate() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        item.setOwner(owner);
        requestBodyItemDto.setDescription(null);
        requestBodyItemDto.setName(null);
        requestBodyItemDto.setAvailable(null);
        requestBodyItemDto.setRequestId(null);

        itemServiceimpl.updateItem(itemId, requestBodyItemDto, owner.getId());

        verify(itemRepository).save(any());
    }

    @Test
    void updateItemTest_whenUserMissing_thenThrowException() {
        long itemId = 1L;
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        assertThrows(
                InternalServerException.class,
                () -> itemServiceimpl.updateItem(itemId, requestBodyItemDto, null));

        verify(itemRepository, never()).save(any());
    }

    @Test
    void updateItemTest_whenItemNotFound_thenThrowException() {
        assertThrows(
                NotFoundException.class,
                () -> itemServiceimpl.updateItem(itemId, requestBodyItemDto, userId));

        verify(itemRepository, never()).save(any());
    }

    @Test
    void updateItemTest_whenUserIsNotOwner_thenThrowException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        item.setOwner(owner);
        assertThrows(
                ForbiddenException.class,
                () -> itemServiceimpl.updateItem(itemId, requestBodyItemDto, userId));

        verify(itemRepository, never()).save(any());
    }

    @Test
    void getItemByIdTest_whenItemPresent_thenItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        item.setOwner(owner);
        itemDto.setComments(new ArrayList<>());

        ItemDto result = itemServiceimpl.getItemById(itemId, userId);

        verify(itemRepository, times(2)).findById(anyLong());
        assertEquals(itemDto, result);
    }

    @Test
    void getItemByIdTest_whenItemNotFound_thenThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        verify(itemRepository, never()).findById(anyLong());
        assertThrows(NotFoundException.class, () -> itemServiceimpl.getItemById(1L, 1L));
    }

    @Test
    void getItemsTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        List<ItemDto> result = itemServiceimpl.getAllItems(owner.getId(), 1, 1);

        verify(itemRepository).findItemsForUserWithPage(any(), any());
        assertEquals(new ArrayList<>(), result);
    }

    @Test
    void getItemByIdTest_whenSizeIllegal_thenThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        verify(itemRepository, never()).findItemsForUserWithPage(any(), any());
        assertThrows(ValidationException.class, () -> itemServiceimpl.getAllItems(2L, -1, 1));
    }

    @Test
    void getItemByIdTest_whenUserNotFound_thenThrowException() {

        verify(itemRepository, never()).findItemsForUserWithPage(any(), any());
        assertThrows(NotFoundException.class, () -> itemServiceimpl.getAllItems(1L, 1, 1));
    }

    @Test
    void getItemsByTextTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        List<ItemDto> result = itemServiceimpl.getItemByNameOrDescription("itemName1", owner.getId(), 1, 1);

        verify(itemRepository).findAvailableItemsByNameOrDescription(anyString(), any(), any());
        assertEquals(new ArrayList<>(), result);
    }

}
