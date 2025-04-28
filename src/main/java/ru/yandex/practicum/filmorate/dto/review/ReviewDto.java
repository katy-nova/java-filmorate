package ru.yandex.practicum.filmorate.dto.review;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReviewDto {

    private String text;
    private BigDecimal rating;
    private String userName;
    private String filmName;
}
