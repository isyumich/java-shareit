package ru.practicum.shareit.item_request.service;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item_request.dto.RequestBodyItemRequestDto;

@Slf4j
public class ItemRequestValidation {

    public void itemRequestValidation(RequestBodyItemRequestDto requestBodyRequestDto) {
        descriptionValidation(requestBodyRequestDto);
    }

    private void descriptionValidation(RequestBodyItemRequestDto requestBodyRequestDto) {
        if (requestBodyRequestDto.getDescription() == null || requestBodyRequestDto.getDescription().equals("") ||
                requestBodyRequestDto.getDescription().equals(" ")) {
            String message = "Описание должно быть указано";
            log.info(message);
            throw new ValidationException(message);
        }
    }
}
