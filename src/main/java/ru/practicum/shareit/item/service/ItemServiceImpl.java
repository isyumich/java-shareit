package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.LastAndNextBookingDtoMapper;
import ru.practicum.shareit.booking.model.BookingStatus;
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
import ru.practicum.shareit.item_request.model.ItemRequest;
import ru.practicum.shareit.item_request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Qualifier("ItemServiceImpl")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemServiceImpl implements ItemService {
    final ItemRepository itemRepository;
    final UserRepository userRepository;
    final BookingRepository bookingRepository;
    final CommentRepository commentRepository;
    final ItemRequestRepository itemRequestRepository;
    final ItemValidation itemValidation = new ItemValidation();
    final CommentValidation commentValidation = new CommentValidation();

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository,
                           UserRepository userRepository,
                           BookingRepository bookingRepository,
                           CommentRepository commentRepository,
                           ItemRequestRepository itemRequestRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    @Override
    public ItemDto addNewItem(RequestBodyItemDto requestBodyItemDto, Long userId) {
        itemValidation.itemValidation(requestBodyItemDto, userId);
        Item item = RequestBodyItemDtoMapper.mapRow(requestBodyItemDto);
        User owner = getUserById(userId);
        Long itemRequestId = requestBodyItemDto.getRequestId();
        if (itemRequestId != null) {
            ItemRequest itemRequest = getRequestById(requestBodyItemDto.getRequestId());
            item.setItemRequest(itemRequest);
        }
        item.setOwner(owner);
        Item savedItem = itemRepository.save(item);
        return itemToItemDto(savedItem, userId);
    }

    @Override
    public CommentDto addNewComment(Comment comment, Long userId, long itemId) {
        commentValidation.commentValidation(comment);
        LocalDateTime currentDate = LocalDateTime.now();
        if (itemRepository.findById(itemId).isEmpty()) {
            String message = String.format("%s %d %s", "Вещь с id =", itemId, "не найдена");
            log.info(message);
            throw new NotFoundException(message);
        }
        Item item = itemRepository.findById(itemId).get();
        if (userRepository.findById(userId).isEmpty()) {
            String message = String.format("%s %d %s", "Пользователь с id =", userId, "не найден");
            log.info(message);
            throw new NotFoundException(message);
        }
        User user = userRepository.findById(userId).get();
        if (bookingRepository.findPastBookingsForUserAndItem(item, user, BookingStatus.APPROVED, currentDate).isEmpty()) {
            String message = String.format("%s %d %s %d", "У пользователя с id =", userId, "нет бронирования для вещи с id=", itemId);
            log.info(message);
            throw new ValidationException(message);
        }
        comment.setItem(item);
        comment.setCreateDate(currentDate);
        comment.setAuthor(user);
        return CommentDtoMapper.mapRow(commentRepository.save(comment));
    }

    @Override
    public ItemDto updateItem(long itemId, RequestBodyItemDto requestBodyItemDto, Long userId) {
        Item item = RequestBodyItemDtoMapper.mapRow(requestBodyItemDto);
        Item checkedItem = checkFieldsForUpdate(item, itemId, userId);
        if (requestBodyItemDto.getRequestId() != null) {
            ItemRequest itemRequest = getRequestById(requestBodyItemDto.getRequestId());
            checkedItem.setItemRequest(itemRequest);
        }
        User owner = getUserById(userId);
        checkedItem.setOwner(owner);
        checkedItem.setId(itemId);
        return itemToItemDto(itemRepository.save(checkedItem), userId);
    }

    @Override
    public List<ItemDto> getAllItems(Long userId, Integer from, Integer size) {
        checkFormAndSize(from, size);
        User user = getUserById(userId);
        return itemsToItemsDto(itemRepository.findItemsForUserWithPage(user, PageRequest.of(from / size, size)), userId);
    }

    @Override
    public ItemDto getItemById(long itemId, Long userId) {
        if (itemRepository.findById(itemId).isEmpty()) {
            String message = String.format("%s %d %s", "Вещь с id =", itemId, "не найдена");
            log.info(message);
            throw new NotFoundException(message);
        }
        return itemToItemDto(itemRepository.findById(itemId).get(), userId);
    }

    @Override
    public List<ItemDto> getItemByNameOrDescription(String text, Long userId, Integer from, Integer size) {
        checkFormAndSize(from, size);
        if (text.equals("")) {
            return new ArrayList<>();
        }
        return itemsToItemsDto(itemRepository.findAvailableItemsByNameOrDescription(text, text, PageRequest.of(from / size, size)), userId);
    }

    private List<ItemDto> itemsToItemsDto(List<Item> items, Long userId) {
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : items) {
            itemsDto.add(itemToItemDto(item, userId));
        }
        return itemsDto;
    }

    private ItemDto itemToItemDto(Item item, Long userId) {
        if (item == null) {
            return null;
        }
        ItemDto itemDto = ItemDtoMapper.mapRow(item);
        setNextAndLastBookings(item, userId, itemDto);
        setComments(item, itemDto);
        return itemDto;
    }

    private List<CommentDto> commentsToCommentsDto(List<Comment> comments) {
        List<CommentDto> commentsDto = new ArrayList<>();
        for (Comment comment : comments) {
            commentsDto.add(CommentDtoMapper.mapRow(comment));
        }
        return commentsDto;
    }


    private void setNextAndLastBookings(Item item, Long userId, ItemDto itemDto) {
        LocalDateTime currentDate = LocalDateTime.now();
        if (userId.equals(item.getOwner().getId())) {
            if (bookingRepository.findLastBooking(item.getId(), currentDate, BookingStatus.APPROVED.toString()) != null) {
                itemDto.setLastBooking(LastAndNextBookingDtoMapper
                        .mapRow(bookingRepository.findLastBooking(item.getId(), currentDate, BookingStatus.APPROVED.toString())));
            }
            if (bookingRepository.findNextBooking(item.getId(), currentDate, BookingStatus.APPROVED.toString()) != null) {
                itemDto.setNextBooking(LastAndNextBookingDtoMapper
                        .mapRow(bookingRepository.findNextBooking(item.getId(), currentDate, BookingStatus.APPROVED.toString())));
            }
        }
    }

    private void setComments(Item item, ItemDto itemDto) {
        if (!commentRepository.findCommentsByItem(item).isEmpty()) {
            itemDto.setComments(commentsToCommentsDto(commentRepository.findCommentsByItem(item)));
        } else {
            itemDto.setComments(new ArrayList<>());
        }
    }

    private Item checkFieldsForUpdate(Item item, long itemId, Long userId) {
        if (userId == null) {
            String message = "Не указан id пользователя";
            log.info(message);
            throw new InternalServerException(message);
        }
        if (itemRepository.findById(itemId).isEmpty()) {
            String message = String.format("%s %d %s", "Вещь с id =", itemId, "не найдена");
            log.info(message);
            throw new NotFoundException(message);
        }
        Item itemFromDb = itemRepository.findById(itemId).get();
        if (!userId.equals(itemFromDb.getOwner().getId())) {
            String message = "Только владелец может редактировать вещь";
            log.info(message);
            throw new ForbiddenException(message);
        }
        if (item.getName() == null) {
            item.setName(itemFromDb.getName());
        }
        if (item.getDescription() == null) {
            item.setDescription(itemFromDb.getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(itemFromDb.getAvailable());
        }
        return item;
    }

    private User getUserById(long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            String message = String.format("%s %d %s", "Пользователь с id =", userId, "не найден");
            log.info(message);
            throw new NotFoundException(message);
        }
        return userRepository.findById(userId).get();
    }

    private ItemRequest getRequestById(Long requestId) {
        if (itemRequestRepository.findById(requestId).isPresent()) {
            return itemRequestRepository.findById(requestId).get();
        }
        return null;
    }

    private void checkFormAndSize(Integer from, Integer size) {
        if (from < 0 || size < 1) {
            String message = "Номер страницы или количество элементов недопустимо";
            log.info(message);
            throw new ValidationException(message);
        }
    }
}
