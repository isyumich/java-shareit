package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Comment;

@Slf4j
public class CommentValidation {
    public void commentValidation(Comment comment) {
        textValidation(comment);

    }

    private void textValidation(Comment comment) {
        if (comment.getText() == null || comment.getText().equals("") || comment.getText().equals(" ")) {
            String message = "Текст комментария должен быть указан";
            log.info(message);
            throw new ValidationException(message);
        }
    }
}
