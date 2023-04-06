package ru.practicum.shareit.exception;

public class IsAlreadyDoneException extends RuntimeException {
    public IsAlreadyDoneException(String s) {
        super(s);
    }
}
