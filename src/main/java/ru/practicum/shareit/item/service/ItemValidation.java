package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

public class ItemValidation {
    public boolean itemValidation(Item item, Long userId) {
        return nameValidation(item) && descriptionValidation(item) && availableValidation(item) && ownerIdValidation(userId);
    }

    private boolean nameValidation(Item item) {
        if (item.getName() == null) {
            return false;
        } else {
            return !item.getName().equals("") && !item.getName().equals(" ");
        }
    }

    private boolean descriptionValidation(Item item) {
        if (item.getDescription() == null) {
            return false;
        } else {
            return !item.getDescription().equals("") && !item.getDescription().equals(" ");
        }
    }

    private boolean availableValidation(Item item) {
        return item.getAvailable() != null;
    }

    private boolean ownerIdValidation(Long userId) {
        return userId != null;
    }
}
