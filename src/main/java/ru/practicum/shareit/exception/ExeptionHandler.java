package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExeptionHandler {
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
        return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
    }
}
