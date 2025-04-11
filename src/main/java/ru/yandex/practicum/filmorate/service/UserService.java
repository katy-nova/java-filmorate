package ru.yandex.practicum.filmorate.service;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.ReviewRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(User user) {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            String errorMessage = String.format("Пользователь с email '%s' уже существует", user.getEmail());
            log.error(errorMessage);
            throw new IllegalStateException(errorMessage);
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
            log.info("Для пользователя без имени установлено имя в соответствии с логином: {}", user.getLogin());
        }
        log.info("Пользователь сохранен");
        return userRepository.save(user);
    }

    public User getUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> {
            String errorMessage = String.format("Пользователь с email '%s' не найден", email);
            log.error(errorMessage);
            return new IllegalStateException(errorMessage);
        });
    }

    public User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> {
            String errorMessage = String.format("Пользователь с id '%s' не найден", id);
            log.error(errorMessage);
            return new IllegalStateException(errorMessage);
        });
    }

    public void deleteUser(Long id) {
        log.debug("Попытка удалить пользователя с ID: {}", id);
        User user = getUser(id); // если здесь не вылетело исключение - значит юзер есть в базе
        log.debug("Попытка удалить все отзывы пользователя с ID: {}", id);
        reviewRepository.deleteAllByUserId(id);
        log.info("Отзывы успешно удалены");
        userRepository.delete(user);
        log.info("Пользователь с ID: {} успешно удален", id);
    }

    // почему в примерах пост методы что-то возвращают?
    // какой из методов более верный, принимающий параметры или готового пользователя?
    public User updateUser(Long id, @Email String email, String login, String name, @Past LocalDate birthday) {
        log.debug("Попытка обновить данные пользователя с ID: {}", id);
        User user = getUser(id);
        if (!user.getEmail().equals(email) && email != null) {
            log.debug("Попытка обновить email");
            log.debug("Проверка повторного использования email");
            Optional<User> maybeUser = userRepository.findByEmail(email);
            if (maybeUser.isPresent()) {
                String errorMessage = String.format(String.format("Пользователь с email '%s' уже существует", email));
                log.error(errorMessage);
                throw new IllegalArgumentException(errorMessage);
            }
            user.setEmail(email);
            log.info("email: {} успешно установлен", email);
        }
        if (!user.getLogin().equals(login) && login != null) {
            log.debug("Попытка обновить логин");
            user.setLogin(login); // логин должен быть уникальным?
            log.info("логин: {} успешно установлен", login);
        }
        if (!user.getBirthday().equals(birthday) && birthday != null) {
            log.debug("Попытка обновить дату рождения");
            user.setBirthday(birthday);
            log.info("дата рождения: {} успешно установлена", birthday);
        }
        if (!user.getName().equals(name) && name != null) {
            log.debug("Попытка обновить имя пользователя");
            user.setName(name);
            log.info("имя пользователя: {} успешно установлено", name);
        }
        userRepository.save(user);
        log.info("Пользователь успешно сохранен");
        return user;
    }

    public User updateUser(User user) { // я правильно понимаю, что поля этого юзера уже прошли валидацию при создании?
        User oldUser = getUser(user.getId());
        if (!oldUser.getLogin().equals(user.getLogin()) && user.getLogin() != null) {
            log.debug("Попытка обновить логин");
            oldUser.setLogin(user.getLogin());
            log.info("логин: {} успешно установлен", user.getLogin());
        }
        if (!oldUser.getEmail().equals(user.getEmail()) && user.getEmail() != null) {
            log.debug("Попытка обновить email");
            log.debug("Проверка повторного использования email");
            Optional<User> maybeUser = userRepository.findByEmail(user.getEmail());
            if (maybeUser.isPresent()) {
                String errorMessage = String.format("Пользователь с email '%s' уже существует", user.getEmail());
                log.error(errorMessage);
                throw new IllegalArgumentException(errorMessage);
            }
            oldUser.setEmail(user.getEmail());
            log.info("email: {} успешно установлен", user.getEmail());
        }
        if (!oldUser.getBirthday().equals(user.getBirthday()) && user.getBirthday() != null) {
            log.debug("Попытка обновить дату рождения");
            oldUser.setBirthday(user.getBirthday());
            log.info("дата рождения: {} успешно установлена", user.getBirthday());
        }
        if (!oldUser.getName().equals(user.getName()) && user.getName() != null) {
            log.debug("Попытка обновить имя пользователя");
            oldUser.setName(user.getName());
            log.info("имя пользователя: {} успешно установлено", user.getName());
        }
        userRepository.save(oldUser);
        log.info("Пользователь успешно сохранен");
        return oldUser;
    }
}
