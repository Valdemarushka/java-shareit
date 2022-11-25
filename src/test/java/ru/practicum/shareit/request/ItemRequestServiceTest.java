package ru.practicum.shareit.request;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.EntryNotFoundException;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.ResponseDto;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {
    private RequestServiceImpl itemRequestService;

    @Mock
    private RequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        itemRequestService = new RequestServiceImpl(itemRequestRepository, userRepository);
    }

    @Test
    void getItemRequestByIdTest() {
        Mockito
                .when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(new Request()));
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User()));

        itemRequestService.getItemRequestById(1, 1);

        Mockito.verifyNoMoreInteractions(itemRequestRepository, userRepository);
    }

    @Test
    void getItemRequestByIdNotFoundTest() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User()));

        Exception exception = assertThrows(EntryNotFoundException.class,
                () -> itemRequestService.getItemRequestById(1, 1));
        assertThat(exception.getMessage(), equalTo("Вещь не найдена"));

        Mockito.verify(itemRequestRepository, Mockito.times(1)).findById(1L);
        Mockito.verifyNoMoreInteractions(itemRequestRepository, userRepository);
    }

    @Test
    void getItemRequestsByUserIdTest() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User()));
        Mockito
                .when(itemRequestRepository.findByRequestorId(anyLong(), any(Sort.class)))
                .thenReturn(List.of(new Request(), new Request()));

        itemRequestService.getItemRequestsByUser(1);

        Mockito
                .verify(itemRequestRepository, Mockito.times(1))
                .findByRequestorId(anyLong(), any(Sort.class));
        Mockito.verifyNoMoreInteractions(itemRequestRepository, userRepository);
    }

    @Test
    void getItemRequestsByUserNotFoundTest() {
        Exception exception = assertThrows(EntryNotFoundException.class,
                () -> itemRequestService.getItemRequests(1, 1, 1));
        assertThat(exception.getMessage(), equalTo("Юзер не найден"));

        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verifyNoMoreInteractions(itemRequestRepository, userRepository);
    }

    @Test
    void getItemRequestsTest() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User()));
        Mockito
                .when(itemRequestRepository.findByRequestorIdIsNot(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(new Request(), new Request()));

        itemRequestService.getItemRequests(1, 0, 10);

        Mockito
                .verify(itemRequestRepository, Mockito.times(1))
                .findByRequestorIdIsNot(anyLong(), any(Pageable.class));
        Mockito.verifyNoMoreInteractions(itemRequestRepository, userRepository);
    }

    @Test
    void getItemRequestsNotFoundTest() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User()));

        List<ResponseDto> itemRequestsDto = itemRequestService.getItemRequests(1, 1, 1);

        assertThat(itemRequestsDto, hasSize(0));
        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .findByRequestorIdIsNot(anyLong(), any(Pageable.class));
        Mockito.verifyNoMoreInteractions(itemRequestRepository, userRepository);
    }

    @Test
    void createItemTest() {
        User requestor = new User();
        requestor.setId(1);

        Request itemRequest = new Request();
        itemRequest.setId(1);
        itemRequest.setDescription("Test description");
        itemRequest.setRequestor(requestor);

        RequestDto itemRequestDto = new RequestDto();
        itemRequestDto.setDescription("Test description");

        ArgumentCaptor<Request> itemRequestArgumentCaptor = ArgumentCaptor.forClass(Request.class);

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(requestor));
        Mockito
                .when(itemRequestRepository.save(any(Request.class)))
                .thenReturn(itemRequest);

        itemRequestService.createItemRequest(1, itemRequestDto);

        Mockito
                .verify(itemRequestRepository, Mockito.times(1))
                .save(itemRequestArgumentCaptor.capture());

        Request capturedItemRequest = itemRequestArgumentCaptor.getValue();

        assertThat(capturedItemRequest.getRequestor(), equalTo(requestor));
        assertThat(capturedItemRequest.getDescription(), equalTo(itemRequest.getDescription()));
        Mockito.verifyNoMoreInteractions(itemRequestRepository, userRepository);
    }
}
