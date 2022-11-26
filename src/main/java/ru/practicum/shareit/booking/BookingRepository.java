package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerId(long userId, Pageable pageable);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfter(long userId, LocalDateTime nowToStart,
                                                              LocalDateTime nowToEnd,
                                                              Pageable pageable);

    List<Booking> findByBookerIdAndStatus(long userId, BookingStatus status, Pageable pageable);

    List<Booking> findByBookerIdAndEndIsBefore(long userId, LocalDateTime now, Pageable pageable);

    List<Booking> findByBookerIdAndStartIsAfter(long userId, LocalDateTime now, Pageable pageable);

    List<Booking> findByItemIdInAndStartIsBeforeAndEndIsAfter(List<Long> itemsId, LocalDateTime nowToStart,
                                                              LocalDateTime nowToEnd, Pageable pageable);

    List<Booking> findByItemIdInAndStatus(List<Long> itemsId, BookingStatus status, Pageable pageable);

    List<Booking> findByItemIdInAndEndIsBefore(List<Long> itemsId, LocalDateTime now, Pageable pageable);

    List<Booking> findByItemIdIn(List<Long> itemsId, Pageable pageable);

    List<Booking> findByItemIdInAndStartIsAfter(List<Long> itemsId, LocalDateTime now, Pageable pageable);

    List<Booking> findByItemIdAndStartIsBeforeAndStatus(long itemId, LocalDateTime now, BookingStatus status,
                                                        Sort sort);

    List<Booking> findByItemIdAndStartIsAfterAndStatus(long itemId, LocalDateTime now, BookingStatus status,
                                                       Sort sort);
}
