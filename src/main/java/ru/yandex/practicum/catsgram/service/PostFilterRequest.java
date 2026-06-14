package ru.yandex.practicum.catsgram.service;

import ru.yandex.practicum.catsgram.exception.ParameterNotValidException;
import ru.yandex.practicum.catsgram.model.SortOrder;

import java.util.Optional;

public record PostFilterRequest(String sort, Integer from, Integer size) {

    // конструктор-валидатор, чтоб без импорта jakarta
    public PostFilterRequest {
        // Проверяем size, если он передан
        if (size != null && size <= 0) {
            throw new ParameterNotValidException("size", "Размер должен быть больше нуля");
        }

        if (from != null && from < 0) {
            throw new ParameterNotValidException("from", "Начало выборки должно быть положительным числом");
        }

        // Проверяем sort через enum, если он передан
        if (sort != null && SortOrder.from(sort) == null) {
            throw new ParameterNotValidException("sort", "Получено: " + sort + " должно быть: ask или desc");
        }
    }

    // метод проверки sort через enum
    public Optional<SortOrder> getSortOrder() {
        return Optional.ofNullable(sort).map(SortOrder::from);
    }

    // если клиент вообще ничего не передал в URL
    public boolean isEmpty() {
        return sort == null && from == null && size == null;
    }

    // поля в Optional, чтобы сервис не работал с null
    public Optional<String> getSortOptional() {
        return Optional.ofNullable(sort);
    }

    public Optional<Integer> getFromOptional() {
        return Optional.ofNullable(from);
    }

    public Optional<Integer> getSizeOptional() {
        return Optional.ofNullable(size);
    }
}