package ru.practicum.shareit.exception;

public class IsAlreadyExistsException extends RuntimeException {
    public IsAlreadyExistsException(String s) {
        super(s);
    }
}
