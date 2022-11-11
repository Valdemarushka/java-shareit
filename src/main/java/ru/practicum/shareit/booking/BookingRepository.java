package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findBookingsByBookerId(long userId, Sort sort);

    List<Booking> findBookingsByBookerIdAndStartIsBeforeAndEndIsAfter(long userId, LocalDateTime nowToStart,
                                                                      LocalDateTime nowToEnd,
                                                                      Sort sort);

    List<Booking> findBookingsByBookerIdAndStatus(long userId, BookingStatus status, Sort sort);

    List<Booking> findBookingsByBookerIdAndEndIsBefore(long userId, LocalDateTime now, Sort sort);

    List<Booking> findBookingsByBookerIdAndStartIsAfter(long userId, LocalDateTime now, Sort sort);

    List<Booking> findBookingsByItemIdInAndStartIsBeforeAndEndIsAfter(List<Long> itemsId, LocalDateTime nowToStart,
                                                                      LocalDateTime nowToEnd, Sort sort);

    List<Booking> findBookingsByItemIdInAndStatus(List<Long> itemsId, BookingStatus status, Sort sort);

    List<Booking> findBookingsByItemIdInAndEndIsBefore(List<Long> itemsId, LocalDateTime now, Sort sort);

    List<Booking> findBookingsByItemIdIn(List<Long> itemsId, Sort sort);

    List<Booking> findBookingsByItemIdInAndStartIsAfter(List<Long> itemsId, LocalDateTime now, Sort sort);

    List<Booking> findBookingsByItemIdAndStartIsBeforeAndStatus(long itemId, LocalDateTime now, BookingStatus status,
                                                                Sort sort);

    List<Booking> findBookingsByItemIdAndStartIsAfterAndStatus(long itemId, LocalDateTime now, BookingStatus status,
                                                               Sort sort);
}
