package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.user.UserCreateDto;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.dto.user.UserSimpleDto;
import ru.yandex.practicum.filmorate.dto.user.UserUpdateDto;
import ru.yandex.practicum.filmorate.service.AuthenticationService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

//обычный контроллер как в тз
@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    @GetMapping
    public List<UserDto> getUsers() {
        return userService.getAllUsers();
    }

    @GetMapping(path = "/{id}")
    public UserDto getUserById(@PathVariable("id") Long id) {
        return userService.getUser(id);
    }

    @PostMapping(path = "/registration")
    public UserDto createUser(@Valid @RequestBody UserCreateDto user) {
        return userService.createUser(user);
    }

    @PutMapping(path = "/{id}")
    @PreAuthorize("@authenticationService.isCurrentUser(#id) or @authenticationService.isAdmin()")
    public UserDto updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDto user) {
        return userService.updateUser(id, user);
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize("@authenticationService.isCurrentUser(#id) or @authenticationService.isAdmin()")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @PutMapping(path = "/{userId}/friends/{friendId}")
    @PreAuthorize("@authenticationService.isCurrentUser(#userId) or @authenticationService.isAdmin()")
    public UserDto addFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        return userService.addFriend(userId, friendId);
    }

    @DeleteMapping(path = "/{userId}/friends/{friendId}")
    @PreAuthorize("@authenticationService.isCurrentUser(#userId) or @authenticationService.isAdmin()")
    public void deleteFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        userService.removeFriend(userId, friendId);
    }

    @GetMapping(path = "/{id}/friends")
    public List<UserSimpleDto> getFriends(@PathVariable Long id) {
        return userService.getFriends(id);
    }

    @GetMapping(path = "/{id}/friends/common/{otherId}")
    public List<UserSimpleDto> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    @PutMapping(path = "/admin/make/{id}")
    public ResponseEntity<String> makeAdmin(@PathVariable Long id) {
        authenticationService.makeAdmin(id);
        String message = String.format("Для пользователя с id: %s установлены права администратора", id);
        log.info(message);
        return ResponseEntity.ok(message);
    }

    @PutMapping(path = "/admin/remove/{id}")
    public ResponseEntity<String> removeAdmin(@PathVariable Long id) {
        authenticationService.makeUser(id);
        String message = String.format("Для пользователя с id: %s аннулированы права администратора", id);
        log.info(message);
        return ResponseEntity.ok(message);
    }
}
