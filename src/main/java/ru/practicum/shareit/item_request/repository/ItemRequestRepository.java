package ru.practicum.shareit.item_request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item_request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    @Query(value = "select ir from ItemRequest ir where ir.author = :author order by ir.created desc")
    List<ItemRequest> findItemRequestsByAuthor(User author);

    @Query(value = "select ir from ItemRequest ir where ir.author <> :user order by ir.created desc")
    List<ItemRequest> findAllItemRequests(User user, Pageable pageable);
}
