package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.RequestBodyItemDto;

public class ItemValidation {
    public boolean itemValidation(RequestBodyItemDto requestBodyItemDto, Long userId) {
        return nameValidation(requestBodyItemDto) && descriptionValidation(requestBodyItemDto) && availableValidation(requestBodyItemDto) && ownerIdValidation(userId);
    }

    private boolean nameValidation(RequestBodyItemDto requestBodyItemDto) {
        if (requestBodyItemDto.getName() == null) {
            return false;
        } else {
            return !requestBodyItemDto.getName().equals("") && !requestBodyItemDto.getName().equals(" ");
        }
    }

    private boolean descriptionValidation(RequestBodyItemDto requestBodyItemDto) {
        if (requestBodyItemDto.getDescription() == null) {
            return false;
        } else {
            return !requestBodyItemDto.getDescription().equals("") && !requestBodyItemDto.getDescription().equals(" ");
        }
    }

    private boolean availableValidation(RequestBodyItemDto requestBodyItemDto) {
        return requestBodyItemDto.getAvailable() != null;
    }

    private boolean ownerIdValidation(Long userId) {
        return userId != null;
    }
}
