package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ExeptionHandler {
    @ExceptionHandler
    public ResponseEntity<String> handleItemUnavailable(NotAvailableExeption e) {
        log.error("Ошибка: не доступно");
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleStatusAlreadySet(StatusIsConfirmedException e) {
        log.error("Ошибка: уже подтверждено");
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleWrongBookingState(ConversionFailedException e) {
        log.error("Ошибка: состояние");
        Map<String, String> map = new HashMap<>();
        String message = "Unknown state: " + e.getValue();
        map.put("error", message);
        return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleWrongDate(WrongDateException e) {
        log.error("Ошибка: в дате");
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleItemNotFound(EntryNotFoundException e) {
        log.error("Ошибка: запись не найдена");
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleNotValidEmail(NotValidEmail e) {
        log.error("Ошибка: в Email");
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }


    @ExceptionHandler
    public ResponseEntity<String> handleItemNotFound(NotFoundExeption e) {
        log.error("Ошибка: не найдено");
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleWrongOwner(WrongOwnerExeption e) {
        log.error("Ошибка: неверный владелец");
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }
}
