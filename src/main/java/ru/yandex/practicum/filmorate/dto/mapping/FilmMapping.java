package ru.yandex.practicum.filmorate.dto.mapping;

import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.dto.film.FilmCreateDto;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.FilmDtoHtml;
import ru.yandex.practicum.filmorate.dto.film.FilmSimpleDto;
import ru.yandex.practicum.filmorate.model.Film;

@Mapper(componentModel = "spring", uses = ReviewMapping.class)
public interface FilmMapping {

    Film fromDto(FilmDto filmDto);

    FilmDto toDto(Film film);

    FilmDtoHtml toDtoHtml(Film film);

    Film fromCreateDto(FilmCreateDto filmDto);

    FilmSimpleDto toSimpleDto(Film film);
}
