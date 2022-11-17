package ru.practicum.shareit.user.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserService {
    List<User> getUsers();

    User getUserById(long userId);

    @Transactional
    User createUser(User user);

    @Transactional
    User updateUser(long userId, User user);

    @Transactional
    void deleteUser(long userId);
}
