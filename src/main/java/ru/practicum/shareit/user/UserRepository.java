package ru.practicum.shareit.user;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> findAll();

    Optional<User> findUserById(long userId);

    Optional<User> findUserByEmail(String email);

    User createUser(User user);

    User update(long userId, User user);

    void delete(long userId);
}