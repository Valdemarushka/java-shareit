package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundExeption;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDto> getUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(UserMapper::userToDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(long userId) {
        return userRepository.findUserById(userId)
                .map(UserMapper::userToDto)
                .orElseThrow(() -> new NotFoundExeption(
                        String.format("Пользователь с id = %d не найден", userId)));
    }

    public UserDto getUserByEmail(String email) {
        return userRepository.findUserByEmail(email)
                .map(UserMapper::userToDto)
                .orElse(null);
    }

    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.dtoToUser(userDto);
        return UserMapper.userToDto(userRepository.createUser(user));
    }

    public UserDto updateUser(long userId, UserDto userDto) {
        getUserById(userId);
        User user = UserMapper.dtoToUser(userDto);
        return UserMapper.userToDto(userRepository.update(userId, user));
    }

    public void deleteUser(long userId) {
        getUserById(userId);
        userRepository.delete(userId);
    }
}
