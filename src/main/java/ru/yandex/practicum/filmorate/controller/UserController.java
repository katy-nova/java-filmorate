package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

//обычный контроллер как в тз
@RestController
@RequestMapping(path = "api/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    @GetMapping(path = "/{id}")
    public User getUserById(@PathVariable("id") Long id) {
        return userService.getUser(id);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping // должен ли метод пут возвращать юзера или он будет войд?
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    @PutMapping(path = "/{id}")
    public User updateUser
            (@PathVariable("id") Long id,
             @Email @RequestParam(required = false) String email,
             @RequestParam(required = false) String name,
             @NotEmpty @Pattern(regexp = "^\\S*$", message = "Поле не должно содержать пробелы")
             @RequestParam(required = false) String login,
             @Past @RequestParam(required = false) LocalDate birthday) {
        return userService.updateUser(id, email, name, login, birthday);
    }
}
