package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerId(long userId, Sort sort);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfter(long userId, LocalDateTime nowToStart,
                                                              LocalDateTime nowToEnd,
                                                              Sort sort);

    List<Booking> findByBookerIdAndStatus(long userId, BookingStatus status, Sort sort);

    List<Booking> findByBookerIdAndEndIsBefore(long userId, LocalDateTime now, Sort sort);

    List<Booking> findByBookerIdAndStartIsAfter(long userId, LocalDateTime now, Sort sort);

    List<Booking> findByItemIdInAndStartIsBeforeAndEndIsAfter(List<Long> itemsId, LocalDateTime nowToStart,
                                                              LocalDateTime nowToEnd, Sort sort);

    List<Booking> findByItemIdInAndStatus(List<Long> itemsId, BookingStatus status, Sort sort);

    List<Booking> findByItemIdInAndEndIsBefore(List<Long> itemsId, LocalDateTime now, Sort sort);

    List<Booking> findByItemIdIn(List<Long> itemsId, Sort sort);

    List<Booking> findByItemIdInAndStartIsAfter(List<Long> itemsId, LocalDateTime now, Sort sort);

    List<Booking> findByItemIdAndStartIsBeforeAndStatus(long itemId, LocalDateTime now, BookingStatus status,
                                                        Sort sort);

    List<Booking> findByItemIdAndStartIsAfterAndStatus(long itemId, LocalDateTime now, BookingStatus status,
                                                       Sort sort);
}
