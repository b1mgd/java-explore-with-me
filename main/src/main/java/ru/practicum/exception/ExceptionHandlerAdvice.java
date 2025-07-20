package ru.practicum.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class ExceptionHandlerAdvice {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ApiError {
        private String message;
        private String reason;
        private String status;
        private LocalDateTime timestamp;
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequestException(BadRequestException e) {
        log.warn("Запрос составлен некорректно: {}", e.getMessage());
        return ApiError.builder()
                .message(e.getMessage())
                .reason("Запрос составлен некорректно")
                .status(HttpStatus.BAD_REQUEST.toString())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("Ошибка валидации переданных в теле запроса параметров: {}", e.getMessage());
        return ApiError.builder()
                .message(e.getMessage())
                .reason("Ошибка валидации переданных в теле запроса параметров")
                .status(HttpStatus.BAD_REQUEST.toString())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleConstraintViolationException(ConstraintViolationException e) {
        log.warn("Ошибка валидации переменных запроса: {}", e.getMessage());
        return ApiError.builder()
                .message(e.getMessage())
                .reason("Ошибка валидации переменных запроса")
                .status(HttpStatus.BAD_REQUEST.toString())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMissingServletRequestParameter(MissingServletRequestParameterException e) {
        log.warn("Не передан обязательный параметр запроса: {}", e.getParameterName());
        return ApiError.builder()
                .message("Параметр '" + e.getParameterName() + "' обязателен")
                .reason("Не передан обязательный параметр")
                .status(HttpStatus.BAD_REQUEST.toString())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(NotFoundException e) {
        log.warn("Результаты по запросу не были найдены: {}", e.getMessage());
        return ApiError.builder()
                .message(e.getMessage())
                .reason("Результаты по запросу не были найдены")
                .status(HttpStatus.NOT_FOUND.toString())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(ConflictException e) {
        log.warn("Нарушение целостности данных: {}", e.getMessage());
        return ApiError.builder()
                .message(e.getMessage())
                .reason("Нарушение целостности данных")
                .status(HttpStatus.CONFLICT.toString())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleException(Exception e) {
        log.warn("Внутренняя ошибка сервера: {}", e.getMessage());
        return ApiError.builder()
                .message(e.getMessage())
                .reason("Внутренняя ошибка главного (main) сервера")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
