package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundExeption;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<UserDto> getUsers() {
        log.info("Возвращаем всех юзеров");
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(UserMapper::userToDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(long userId) {
        log.info(String.format("Возвращаем юзера с id %s",userId));
        return userRepository.findUserById(userId)
                .map(UserMapper::userToDto)
                .orElseThrow(() -> new NotFoundExeption(
                        String.format("Пользователь с id = %d не найден", userId)));
    }

    public UserDto getUserByEmail(String email) {
        log.info(String.format("Возвращаем юзера с email %s",email));
        return userRepository.findUserByEmail(email)
                .map(UserMapper::userToDto)
                .orElse(null);
    }

    public UserDto createUser(UserDto userDto) {
        log.info("Создаем юзера");
        User user = UserMapper.dtoToUser(userDto);
        return UserMapper.userToDto(userRepository.createUser(user));
    }

    public UserDto updateUser(long userId, UserDto userDto) {
        log.info(String.format("Обновляем юзера с id %s",userId));
        getUserById(userId);
        User user = UserMapper.dtoToUser(userDto);
        return UserMapper.userToDto(userRepository.update(userId, user));
    }

    public void deleteUser(long userId) {
        log.info(String.format("Удаляем юзера с id %s",userId));
        getUserById(userId);
        userRepository.delete(userId);
    }
}
