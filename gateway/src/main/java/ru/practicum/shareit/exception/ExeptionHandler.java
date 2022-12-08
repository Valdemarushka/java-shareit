package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
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
    public ResponseEntity<Map<String, String>> handleWrongBookingState(IllegalArgumentException e) {
        log.error("Ошибка: состояние");
        Map<String, String> map = new HashMap<>();
        String message = "Unknown state: UNSUPPORTED_STATUS";
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
    public ResponseEntity<String> handleWrongOwner(WrongOwnerExeption e) {
        log.error("Ошибка: неверный владелец");
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException e) {
        log.error("Ошибка: нарушение ограничения целостности базы данных");
        Map<String, String> map = new HashMap<>();
        e.getConstraintViolations().forEach(error -> {
            String message = error.getMessage();
            map.put("error", message);
        });
        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleConversionFailed(ConversionFailedException e) {
        log.error("Ошибка: несуществующий параметр");
        Map<String, String> map = new HashMap<>();
        String message = "Unknown state: " + e.getValue();
        map.put("error", message);
        return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        log.error("Ошибка: валидация");
        Map<String, String> map = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            String message = error.getDefaultMessage();
            map.put("error", message);
        });
        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }
}
