package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Comment;

public class CommentValidation {
    public boolean commentValidation(Comment comment) {
        return nameValidation(comment);
    }

    private boolean nameValidation(Comment comment) {
        if (comment.getText() == null) {
            return false;
        } else {
            return !comment.getText().equals("") && !comment.getText().equals(" ");
        }
    }
}
