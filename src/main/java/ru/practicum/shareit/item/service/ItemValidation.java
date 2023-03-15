package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.RequestBodyItemDto;

@Slf4j
public class ItemValidation {
    public void itemValidation(RequestBodyItemDto requestBodyItemDto, Long userId) {
        nameValidation(requestBodyItemDto);
        descriptionValidation(requestBodyItemDto);
        availableValidation(requestBodyItemDto);
        ownerIdValidation(userId);
    }

    private void nameValidation(RequestBodyItemDto requestBodyItemDto) {
        if (requestBodyItemDto.getName() == null || requestBodyItemDto.getName().equals("") ||
                requestBodyItemDto.getName().equals(" ")) {
            String message = "��� �� ����� ���� ������";
            log.info(message);
            throw new ValidationException(message);
        }
    }

    private void descriptionValidation(RequestBodyItemDto requestBodyItemDto) {
        if (requestBodyItemDto.getDescription() == null || requestBodyItemDto.getDescription().equals("") ||
                requestBodyItemDto.getDescription().equals(" ")) {
            String message = "�������� �� ����� ���� ������";
            log.info(message);
            throw new ValidationException(message);
        }
    }

    private void availableValidation(RequestBodyItemDto requestBodyItemDto) {
        if (requestBodyItemDto.getAvailable() == null) {
            String message = "����������� ������ ������ ���� �������";
            log.info(message);
            throw new ValidationException(message);
        }
    }

    private void ownerIdValidation(Long userId) {
        if (userId == null) {
            String message = "�� ������ id ���������";
            log.info(message);
            throw new ValidationException(message);
        }
    }
}
