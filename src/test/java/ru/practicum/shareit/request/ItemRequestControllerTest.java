package ru.practicum.shareit.request;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.ResponseDto;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RequestServiceImpl itemRequestService;

    @Autowired
    private MockMvc mockMvc;

    private ResponseDto itemRequestDto;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @BeforeEach
    void setUp() {
        User requestor = new User();
        requestor.setId(1);

        ResponseDto.Item item = new ResponseDto.Item(1,
                "Item name",
                "Item description",
                true,
                1);

        itemRequestDto = new ResponseDto();
        itemRequestDto.setId(1);
        itemRequestDto.setDescription("Test description");
        itemRequestDto.setItems(List.of(item));
        itemRequestDto.setCreated(LocalDateTime.now());
    }

    @Test
    void getItemRequestByIdTest() throws Exception {
        Mockito
                .when(itemRequestService.getItemRequestById(anyLong(), anyLong()))
                .thenReturn(itemRequestDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.items[0].id", is(itemRequestDto.getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated().format(formatter))));
    }

    @Test
    void getItemRequestByUserTest() throws Exception {
        Mockito
                .when(itemRequestService.getItemRequestsByUser(anyLong()))
                .thenReturn(List.of(itemRequestDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/requests")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].items[0].id", is(itemRequestDto.getItems().get(0).getId()),
                        Long.class))
                .andExpect(jsonPath("$[0].created", is(itemRequestDto.getCreated().format(formatter))));
    }

    @Test
    void getItemRequestsTest() throws Exception {
        Mockito
                .when(itemRequestService.getItemRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequestDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].items[0].id", is(itemRequestDto.getItems().get(0).getId()),
                        Long.class))
                .andExpect(jsonPath("$[0].created", is(itemRequestDto.getCreated().format(formatter))));
    }

    @Test
    void createItemRequestTest() throws Exception {
        RequestDto reqRequestDto = new RequestDto();
        reqRequestDto.setDescription("Test description");

        Mockito
                .when(itemRequestService.createItemRequest(anyLong(), any(RequestDto.class)))
                .thenReturn(itemRequestDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(reqRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.items[0].id", is(itemRequestDto.getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated().format(formatter))));
    }
}
