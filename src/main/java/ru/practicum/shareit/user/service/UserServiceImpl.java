package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntryNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::userToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(long userId) {
        return UserMapper.userToDto(getUserFromId(userId));
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.dtoToUser(userDto);
        return UserMapper.userToDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDto updateUser(long userId, UserDto userDto) {
        User userToUpdate = getUserFromId(userId);
        if (userDto.getName() != null) {
            userToUpdate.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            userToUpdate.setEmail(userDto.getEmail());
        }
        return UserMapper.userToDto(userToUpdate);
    }

    @Override
    @Transactional
    public void deleteUser(long userId) {
        User user = getUserFromId(userId);
        userRepository.delete(user);
    }

    private User getUserFromId(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(EntryNotFoundException.entryNotFoundException("Юзер не найден"));
    }
}
