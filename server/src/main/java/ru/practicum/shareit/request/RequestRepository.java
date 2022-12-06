package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findByRequestorIdIsNot(long userId, Pageable pageable);

    List<Request> findByRequestorId(long userId, Sort sort);
}
