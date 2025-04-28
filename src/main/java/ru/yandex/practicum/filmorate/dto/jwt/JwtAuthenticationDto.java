package ru.yandex.practicum.filmorate.dto.jwt;

import lombok.Data;

@Data
public class JwtAuthenticationDto {
    private String token;
    private String refreshToken;
}
