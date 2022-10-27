package ru.practicum.shareit.booking;

import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

@NoArgsConstructor
public class BookingMapper {
    public static Booking dtoToBooking(BookingDto bookingDto) {
        Booking booking = new Booking();
        booking.setId(bookingDto.getId());
        booking.setItem(ItemMapper.dtoToItem(bookingDto.getItem()));
        booking.setStart(bookingDto.getStart());
        booking.setEnd(booking.getEnd());
        booking.setBooker(UserMapper.dtoToUser(bookingDto.getBooker()));
        booking.setStatus(bookingDto.getStatus());
        return booking;
    }

    public static BookingDto bookingToDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setItem(ItemMapper.itemToDto(booking.getItem()));
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setBooker(UserMapper.userToDto(booking.getBooker()));
        bookingDto.setStatus(booking.getStatus());
        return bookingDto;
    }
}
