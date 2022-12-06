package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.groups.ForCreate;
import ru.practicum.shareit.groups.ForUpdate;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserClient userClient;

    @Autowired
    public UserController(UserClient userClient) {
        this.userClient = userClient;
    }

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        return userClient.getUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable long userId) {
        return userClient.getUser(userId);
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Validated(ForCreate.class) UserDto userDto) {
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable long userId,
                                             @RequestBody @Validated(ForUpdate.class) UserDto userDto) {
        return userClient.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        userClient.deleteUser(userId);
    }
}