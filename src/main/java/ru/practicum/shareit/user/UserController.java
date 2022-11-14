package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.groups.ForCreate;
import ru.practicum.shareit.groups.ForUpdate;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserServiceImpl userService;

    @Autowired
    public UserController(@Qualifier("userServiceImpl") UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> getUsers() {
        return userService.getUsers().stream()
                .map(UserMapper::userToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable long userId) {
        return UserMapper.userToDto(userService.getUserById(userId));
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody @Validated(ForCreate.class) UserDto userDto) {
        User user = UserMapper.dtoToUser(userDto);
        UserDto dto = UserMapper.userToDto(userService.createUser(user));
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable long userId,
                              @RequestBody @Validated(ForUpdate.class) UserDto userDto) {
        User user = UserMapper.dtoToUser(userDto);
        return UserMapper.userToDto(userService.updateUser(userId, user));
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        userService.deleteUser(userId);
    }
}