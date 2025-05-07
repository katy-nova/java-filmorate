package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.user.UserCreateDto;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.dto.user.UserHtmlDto;
import ru.yandex.practicum.filmorate.dto.user.UserUpdateDto;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

// контроллер работающий с html файлами
@Controller
@RequestMapping(path = "/api/users")
@AllArgsConstructor
public class UserWebController {

    private final UserService userService;

    // не понимаю как мне тут сделать через DTO, ведь у меня там нет поля айди
    // для отображения 1 юзера я передаю айди в модель отдельно, но не понимаю, как это делать в списке(
    // поэтому тут немножко костыль в виде отдельного DTO
    @GetMapping
    public String getUsers(Model model) {
        List<UserHtmlDto> users = userService.getUsersForHTML();
        model.addAttribute("users", users);
        return "users/list";
    }

    @GetMapping(path = "/{id}")
    public String getUser(@PathVariable Long id, Model model) {
        UserDto user = userService.getUser(id);
        model.addAttribute("id", id);
        model.addAttribute("user", user);
        return "users/userInfo";
    }

    @GetMapping("/new")
    public String newUserForm(Model model) {
        model.addAttribute("user", new User());
        return "users/new";
    }

    @PostMapping("/new")
    public String createUser(@Valid @ModelAttribute("user") UserCreateDto user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            // Если есть ошибки валидации, возвращаемся на форму
            return "users/new";
        }
        try {
            userService.createUser(user);
            return "redirect:/api/users";
        } catch (Exception e) {
            // Обработка ошибок при создании пользователя
            model.addAttribute("errorMessage", e.getMessage()); // Передаем сообщение об ошибке в модель
            return "users/new";
        }
    }

    @GetMapping(path = "/update/{id}")
    public String updateUser(@PathVariable Long id, Model model) {
        model.addAttribute("id", id);
        model.addAttribute("user", userService.getUser(id));
        return "users/update";
    }

    @PostMapping(path = "/update/{id}")
    public String updateUser(@PathVariable Long id,
                             @Valid @ModelAttribute("user") UserUpdateDto user,
                             BindingResult result,
                             Model model) {
        if (result.hasErrors()) {
            return "users/update";
        }
        try {
            userService.updateUser(id, user);
            return "redirect:/api/users/" + id;
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "users/update";
        }
    }

    @PostMapping(path = "/delete")
    public String deleteUser(@RequestParam("id") Long id) {
        userService.deleteUser(id);
        return "redirect:/api/users";
    }
}
