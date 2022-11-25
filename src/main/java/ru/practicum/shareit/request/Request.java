package ru.practicum.shareit.request;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private long id;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "requestor_id", referencedColumnName = "user_id")
    private User requestor;

    @OneToMany(mappedBy = "request")
    private List<Item> items;
    private LocalDateTime created = LocalDateTime.now();
}
