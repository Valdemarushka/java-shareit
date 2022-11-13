package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotValidEmail;

import java.util.*;

@Component
public class UserRepositoryImpl implements UserRepository {
    private static Map<Long, User> users = new HashMap<>();
    private static Set<String> emailList = new HashSet<>();
    private static long id = 1;

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> findUserById(long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public User createUser(User user) {
        if (emailList.contains(user.getEmail())) {
            throw new NotValidEmail("Пользователь с таким email уже существует");
        }
        user.setId(id++);
        users.put(user.getId(), user);
        emailList.add(user.getEmail());
        return user;
    }

    @Override
    public User update(long userId, User user) {
        User userToUpdate = users.get(userId);

        if (user.getEmail() != null) {
            if (emailList.contains(user.getEmail()) && !userToUpdate.getEmail().equals(user.getEmail())) {
                throw new NotValidEmail("Пользователь с таким email уже существует");
            }
            emailList.remove(userToUpdate.getEmail());
            emailList.add(user.getEmail());
            userToUpdate.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            userToUpdate.setName(user.getName());
        }
        return userToUpdate;
    }

    @Override
    public void delete(long userId) {
        emailList.remove(users.get(userId).getEmail());
        users.remove(userId);
    }
}
