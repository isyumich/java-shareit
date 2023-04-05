package ru.practicum.shareit.item.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item_request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;


@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "items")
public class Item {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    @Column(name = "name", nullable = false)
    String name;
    @Column(name = "description", nullable = false)
    String description;
    @Column(name = "available", nullable = false)
    Boolean available;
    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    User owner;
    @ManyToOne
    @JoinColumn(name = "item_request_id", referencedColumnName = "id")
    ItemRequest itemRequest;
}
