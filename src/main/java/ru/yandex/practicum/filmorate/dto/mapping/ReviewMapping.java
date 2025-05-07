package ru.yandex.practicum.filmorate.dto.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.dto.review.ReviewCreateDto;
import ru.yandex.practicum.filmorate.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

@Mapper(componentModel = "spring")
public abstract class ReviewMapping {
    // пришлось сделать абстрактным классом чтобы заинжектить репозитории

    @Autowired
    UserRepository userRepository;

    @Autowired
    FilmRepository filmRepository;


    public abstract ReviewCreateDto toDto(Review review);

    @Mapping(target = "user", expression = "java(userRepository.findById(dto.getUserId()).orElse(null))")
    @Mapping(target = "film", expression = "java(filmRepository.findById(dto.getFilmId()).orElse(null))")
    public abstract Review fromDto(ReviewCreateDto dto);

    @Mapping(target = "userName", expression = "java(review.getUser().getName())")
    @Mapping(target = "filmName", expression = "java(review.getFilm().getName())")
    public abstract ReviewDto toReviewDto(Review review);
}
