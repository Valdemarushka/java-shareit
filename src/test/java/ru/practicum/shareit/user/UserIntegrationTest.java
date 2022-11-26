package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.service.UserServiceImpl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserIntegrationTest {
    @Autowired
    private UserServiceImpl userService;

    @Test
    void updateUserTest() {
        UserDto userDto = new UserDto();
        userDto.setName("Test name");
        userDto.setEmail("Test email");

        UserDto userToUpdateDto = new UserDto();
        userToUpdateDto.setName("Updated name");
        userToUpdateDto.setEmail("Updated email");

        userService.createUser(userDto);
        UserDto updatedUserDto = userService.updateUser(1, userToUpdateDto);

        assertThat(updatedUserDto.getName(), equalTo(userToUpdateDto.getName()));
        assertThat(updatedUserDto.getEmail(), equalTo(userToUpdateDto.getEmail()));
    }
}
