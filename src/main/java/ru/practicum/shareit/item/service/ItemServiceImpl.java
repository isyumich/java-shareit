package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.LastAndNextBookingDtoMapper;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.InternalServerException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
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
    final ItemValidation itemValidation = new ItemValidation();
    final CommentValidation commentValidation = new CommentValidation();

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository,
                           UserRepository userRepository,
                           BookingRepository bookingRepository,
                           CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public ItemDto addNewItem(Item item, Long userId) {
        if (!itemValidation.itemValidation(item, userId)) {
            log.info("Поля заполнены неверно или не указан id пользователя");
            throw new ValidationException("Поля заполнены неверно или не указан id пользователя");
        }
        User owner = getUserById(userId);
        item.setOwner(owner);
        return itemToItemDto(itemRepository.save(item), userId);
    }

    @Override
    public CommentDto addNewComment(Comment comment, Long userId, long itemId) {
        if (!commentValidation.commentValidation(comment)) {
            log.info("Не указан текст отзыва");
            throw new ValidationException("Не указан текст отзыва");
        }
        LocalDateTime currentDate = LocalDateTime.now();
        if (itemRepository.findById(itemId).isEmpty()) {
            log.info(String.format("%s %d %s", "Вещь с id =", itemId, "не найдена"));
            throw new NotFoundException(String.format("%s %d %s", "Вещь с id = ", itemId, "не найдена"));
        }
        Item item = itemRepository.findById(itemId).get();
        if (userRepository.findById(userId).isEmpty()) {
            log.debug(String.format("%s %d %s", "Пользователь с id =", userId, "не найден"));
            throw new NotFoundException(String.format("%s %d %s", "Пользователь с id =", userId, "не найден"));
        }
        User user = userRepository.findById(userId).get();
        if (bookingRepository.findBookingsByItemIsAndBookerIsAndStatus(item, user, BookingStatus.APPROVED).isEmpty()) {
            log.debug(String.format("%s %d %s %d", "Пользователь с id =", userId, "не бронировал вещь с id=", itemId));
            throw new ValidationException(String.format("%s %d %s %d", "Пользователь с id =", userId, "не бронировал вещь с id=", itemId));
        }
        if (bookingRepository.findBookingsByItemIsAndBookerIsAndStatusAndEndBefore(item, user, BookingStatus.APPROVED, currentDate).isEmpty()) {
            log.debug(String.format("%s %d %s %d %s", "Бронь пользователя с id =", userId, "для брони вещи с id=", itemId, "ещё не закончилась"));
            throw new ValidationException(String.format("%s %d %s %d %s", "Бронь пользователя с id =", userId, "для брони вещи с id=", itemId, "ещё не закончилась"));
        }
        comment.setItem(item);
        comment.setCreateDate(currentDate);
        comment.setAuthor(user);
        return CommentDtoMapper.mapRow(commentRepository.save(comment));
    }

    @Override
    public ItemDto updateItem(long itemId, Item item, Long userId) {
        Item checkedItem = checkFieldsForUpdate(item, itemId, userId);
        User owner = getUserById(userId);
        checkedItem.setOwner(owner);
        checkedItem.setId(itemId);
        return itemToItemDto(itemRepository.save(checkedItem), userId);
    }

    @Override
    public List<ItemDto> getAllItems(Long userId) {
        User user = getUserById(userId);
        return itemsToItemsDto(itemRepository.findByOwnerEqualsOrderByIdAsc(user), userId);
    }

    @Override
    public ItemDto getItemById(long itemId, Long userId) {
        if (itemRepository.findById(itemId).isEmpty()) {
            log.info(String.format("%s %d %s", "Вещь с id =", itemId, "не найдена"));
            throw new NotFoundException(String.format("%s %d %s", "Вещь с id = ", itemId, "не найдена"));
        }
        return itemToItemDto(itemRepository.findById(itemId).get(), userId);
    }

    @Override
    public List<ItemDto> getItemByNameOrDescription(String text, Long userId) {
        if (text.equals("")) {
            return new ArrayList<>();
        }
        System.out.println(itemRepository.findByNameContainsIgnoreCaseOrDescriptionContainsIgnoreCaseAndAvailableIsTrue(text, text));
        return itemsToItemsDto(itemRepository.findByNameContainsIgnoreCaseOrDescriptionContainsIgnoreCaseAndAvailableIsTrue(text, text), userId);
    }

    private List<ItemDto> itemsToItemsDto(List<Item> items, Long userId) {
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : items) {
            itemsDto.add(itemToItemDto(item, userId));
        }
        return itemsDto;
    }

    private ItemDto itemToItemDto(Item item, Long userId) {
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
            if (!bookingRepository.findBookingsByItemIsAndEndBeforeAndStatusOrderByEndDesc(item, currentDate, BookingStatus.APPROVED).isEmpty()) {
                itemDto.setLastBooking(LastAndNextBookingDtoMapper
                        .mapRow(bookingRepository.findBookingsByItemIsAndEndBeforeAndStatusOrderByEndDesc(item, currentDate, BookingStatus.APPROVED).get(0)));
            }
            if (!bookingRepository.findBookingsByItemIsAndStartAfterAndStatusOrderByEndAsc(item, currentDate, BookingStatus.APPROVED).isEmpty()) {
                itemDto.setNextBooking(LastAndNextBookingDtoMapper
                        .mapRow(bookingRepository.findBookingsByItemIsAndStartAfterAndStatusOrderByEndAsc(item, currentDate, BookingStatus.APPROVED).get(0)));
            }
        }
    }

    private void setComments(Item item, ItemDto itemDto) {
        if (!commentRepository.findCommentsByItemIs(item).isEmpty()) {
            itemDto.setComments(commentsToCommentsDto(commentRepository.findCommentsByItemIs(item)));
        } else {
            itemDto.setComments(new ArrayList<>());
        }
    }

    private Item checkFieldsForUpdate(Item item, long itemId, Long userId) {
        if (userId == null) {
            log.info("Не указан id владельца");
            throw new InternalServerException("Не указан id владельца");
        }
        if (itemRepository.findById(itemId).isEmpty()) {
            log.debug(String.format("%s %d %s", "Товар с id =", itemId, "не найден"));
            throw new NotFoundException(String.format("%s %d %s", "Товар с id = ", itemId, "не найден"));
        }
        Item itemFromDb = itemRepository.findById(itemId).get();
        if (!userId.equals(itemFromDb.getOwner().getId())) {
            log.info("Попытка редактирования товара другого пользователя");
            throw new ForbiddenException("Вам нельзя редактировать товар другого пользователя");
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
            log.debug(String.format("%s %d %s", "Пользователь с id =", userId, "не найден"));
            throw new NotFoundException(String.format("%s %d %s", "Пользователь с id =", userId, "не найден"));
        }
        return userRepository.findById(userId).get();
    }
}
