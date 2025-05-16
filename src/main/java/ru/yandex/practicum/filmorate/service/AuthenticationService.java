package ru.yandex.practicum.filmorate.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.jwt.JwtAuthenticationDto;
import ru.yandex.practicum.filmorate.dto.jwt.RefreshTokenDto;
import ru.yandex.practicum.filmorate.dto.jwt.UserCredentialDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.enums.Role;
import ru.yandex.practicum.filmorate.model.entity.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;
import ru.yandex.practicum.filmorate.security.CustomUserDetails;
import ru.yandex.practicum.filmorate.security.CustomUserServiceImpl;
import ru.yandex.practicum.filmorate.security.jwt.JwtService;

import javax.naming.AuthenticationException;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserServiceImpl customUserService;

    public JwtAuthenticationDto signIn(UserCredentialDto userCredentialDto) throws AuthenticationException {
        CustomUserDetails customUserDetails = findCustomUserDetailsByCredentials(userCredentialDto);
        return jwtService.generateAuthenticationToken(customUserDetails);

    }

    public JwtAuthenticationDto refreshToken(RefreshTokenDto refreshTokenDto) throws AuthenticationException {
        String refreshToken = refreshTokenDto.getRefreshToken();
        if (refreshToken != null && jwtService.validateJwtToken(refreshToken)) {
            String login = jwtService.getLoginFromToken(refreshToken);
            CustomUserDetails customUserDetails = customUserService.loadUserByUsername(login);
            return jwtService.refreshBaseToken(customUserDetails, refreshToken);

        }
        throw new AuthenticationException("Invalid refresh token");
    }

    private CustomUserDetails findCustomUserDetailsByCredentials(UserCredentialDto userCredentialDto)
            throws AuthenticationException {
        Optional<User> maybeUser = userRepository.findByLogin(userCredentialDto.getLogin());
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            if (passwordEncoder.matches(userCredentialDto.getPassword(), user.getPassword())) {
                return customUserService.loadUserByUsername(user.getLogin());
            }
        }
        throw new AuthenticationException("Неверные логин или пароль");
    }

    public boolean isCurrentUser(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        Long authenticatedUserId = ((CustomUserDetails) authentication.getPrincipal()).getId();
        return authenticatedUserId.equals(userId);
    }

    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals(Role.ADMIN.name()));
    }

    public void makeAdmin(Long userId) {
        User user = findUser(userId);
        user.makeAdmin();
    }

    public void makeUser(Long userId) {
        User user = findUser(userId);
        user.makeUser();
    }

    public void makeDisabled(Long userId) {
        User user = findUser(userId);
        user.setEnabled(false);
    }

    public void makeEnabled(Long userId) {
        User user = findUser(userId);
        user.setEnabled(true);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

}
