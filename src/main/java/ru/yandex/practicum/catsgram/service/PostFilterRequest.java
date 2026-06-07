package ru.yandex.practicum.catsgram.service;

import ru.yandex.practicum.catsgram.model.SortOrder;

import java.util.Optional;

public record PostFilterRequest(String sort, Integer from, Integer size) {

    // конструктор-валидатор, чтоб без импорта jakarta
    public PostFilterRequest {
        // Проверяем size, если он передан
        if (size != null && size <= 0) {
            throw new IllegalArgumentException("Размер выборки (size) должен быть больше 0");
        }

        // Проверяем sort через enum, если он передан
        if (sort != null && SortOrder.from(sort) == null) {
            throw new IllegalArgumentException("Допустимые значения для sort: asc, desc, ascending, descending");
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