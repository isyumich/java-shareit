package ru.practicum.shareit.item.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    @ManyToOne
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    Item item;
    @ManyToOne
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    User author;
    @Column(name = "create_date")
    LocalDateTime createDate;
    @Column(name = "text")
    String text;
}
