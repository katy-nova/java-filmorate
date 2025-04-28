package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.jwt.JwtAuthenticationDto;
import ru.yandex.practicum.filmorate.dto.jwt.RefreshTokenDto;
import ru.yandex.practicum.filmorate.dto.jwt.UserCredentialDto;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.naming.AuthenticationException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;

    @PostMapping("/sign-in")
    public ResponseEntity<JwtAuthenticationDto> singIn(@RequestBody UserCredentialDto userCredential) throws AuthenticationException {
        try {
            JwtAuthenticationDto jwtAuthenticationDto = userService.signIn(userCredential);
            return ResponseEntity.ok(jwtAuthenticationDto);
        } catch (AuthenticationException e) {
            throw new AuthenticationException(e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public JwtAuthenticationDto refresh(@RequestBody RefreshTokenDto refreshTokenDto) throws AuthenticationException {
        return userService.refreshToken(refreshTokenDto);
    }
}
