package ru.yandex.practicum.filmorate.model.enums;

public enum Genre {
    COMEDY,
    ROMANCE,
    DRAMA,
    SCIENCE_FICTION,
    FANTASY,
    MUSICAL,
    HORROR,
    CARTOON,
    SERIES,
    DETECTIVE,
    THRILLER,
    ACTION,
    UNKNOWN;

    public String getDisplayName() {
        return switch (this) {
            case COMEDY -> "Комедия";
            case DRAMA -> "Драма";
            case ACTION -> "Боевик";
            case HORROR -> "Ужасы";
            case SERIES -> "Сериал";
            case CARTOON -> "Мультфильм";
            case DETECTIVE -> "Детектив";
            case THRILLER -> "Триллер";
            case ROMANCE -> "Мелодрама";
            case MUSICAL -> "Мюзикл";
            case FANTASY -> "Фэнтези";
            case SCIENCE_FICTION -> "Научная фантастика";
            case UNKNOWN -> "Не указан";
        };
    }
}
