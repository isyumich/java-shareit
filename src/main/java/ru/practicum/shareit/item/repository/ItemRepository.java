package ru.practicum.shareit.item.repository;


import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(value = "select i from Item i where i.owner = :user order by i.id asc")
    List<Item> findItemsForUserWithPage(User user, Pageable pageable);

    @Query(value = "select i from Item i where i.owner = :user order by i.id asc")
    List<Item> findItemsForUser(User user);

    @Query(value = "select i from Item i " +
            "where (lower(i.name) like lower(concat('%', :textName, '%')) or lower(i.description) like lower(concat('%', :textDesc, '%')))" +
            "and i.available is true")
    List<Item> findAvailableItemsByNameOrDescription(String textName, String textDesc, Pageable pageable);

    @Query(value = "select * from items where item_request_id = :requestId", nativeQuery = true)
    List<Item> findItemsByRequests(long requestId);
}
