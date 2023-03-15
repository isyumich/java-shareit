package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class CommentMapperTest {

    @Test
    public void toCommentDtoTest() {
        User author = User.builder().id(1L).name("authorName").email("authorEmail@mail.ru").build();
        Comment comment = Comment.builder().id(1L).item(null).author(author)
                .createDate(LocalDateTime.now()).text("commentText").build();

        CommentDto commentDto = CommentDtoMapper.mapRow(comment);

        assertEquals(1L, commentDto.getId());
        assertNull(commentDto.getItem());
        assertEquals("authorName", commentDto.getAuthorName());
        assertNotNull(commentDto.getCreated());
        assertEquals("commentText", commentDto.getText());
    }
}
