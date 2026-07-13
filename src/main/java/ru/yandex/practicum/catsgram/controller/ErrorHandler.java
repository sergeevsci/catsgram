package ru.yandex.practicum.catsgram.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.ImageFileException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.exception.ParameterNotValidException;
import ru.yandex.practicum.catsgram.model.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(final NotFoundException e) {
        return new ErrorResponse(
                e.getMessage(), // Сообщение из исключения попадает в поле error
                "Ресурс не найден" // любое общее описание/null
        );
    }

    @ExceptionHandler(DuplicatedDataException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicatedData(final DuplicatedDataException e) {
        return new ErrorResponse(
                e.getMessage(), // Сообщение из исключения уходит в поле error
                "Произошел конфликт данных" // Описание для поля description
        );
    }

    @ExceptionHandler(ConditionsNotMetException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorResponse handleConditionsNotMet(final ConditionsNotMetException e) {
        return new ErrorResponse(
                e.getMessage(), // Сообщение из исключения уходит в поле error
                "Не выполнены предварительные условия" // Описание для поля description
        );
    }

    @ExceptionHandler(ParameterNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // Возвращает HTTP-статус 400
    public ErrorResponse handleParameterNotValid(final ParameterNotValidException e) {
        return new ErrorResponse(
                String.format("Некорректное значение параметра %s: %s", e.getParameter(), e.getReason()),
                "Ошибка валидации параметров запроса"
        );
    }

    @ExceptionHandler(ImageFileException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleImageFile(final ImageFileException e) {
        log.error("Ошибка при работе с файлом изображения", e);
        return new ErrorResponse(
                e.getMessage(),
                "Ошибка при работе с файлом изображения"
        );
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        log.error("Непредвиденная ошибка", e);
        return new ErrorResponse(
                "Произошла непредвиденная ошибка.", // Сообщение уходит в поле error
                e.getMessage() // Описание ошибки уходит в description
        );
    }
}
