package ru.yandex.practicum.catsgram.model;

public enum SortOrder {
    ASCENDING,
    DESCENDING;

    /**
     * Преобразует строку в элемент перечисления.
     * Поддерживает значения: "asc", "ascending", "desc", "descending" в любом регистре.
     * Если передана некорректная строка, вернет null.
     */

    public static SortOrder from(String order) {
        if (order == null) {
            return null;
        }

        switch (order.toLowerCase()) {
            case "ascending":
            case "asc":
                return ASCENDING;
            case "descending":
            case "desc":
                return DESCENDING;
            default:
                return null;
        }
    }
}
