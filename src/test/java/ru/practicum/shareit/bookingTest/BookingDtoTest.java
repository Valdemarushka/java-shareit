package ru.practicum.shareit.bookingTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingResponseDto> json;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Test
    void bookingDtoTest() throws IOException {
        BookingResponseDto.Item item = new BookingResponseDto.Item(1, "Test item name");
        BookingResponseDto.Booker booker = new BookingResponseDto.Booker(2, "Test booker name");

        BookingResponseDto bookingDto = BookingResponseDto.builder()
                .id(3)
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        JsonContent<BookingResponseDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo((int) bookingDto.getId());
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo((int) bookingDto.getItem().getId());
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo(bookingDto.getItem().getName());
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo((int) bookingDto.getBooker().getId());
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo(bookingDto.getBooker().getName());
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(bookingDto.getStart().format(formatter));
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(bookingDto.getEnd().format(formatter));
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(bookingDto.getStatus().name());
    }
}
