package ru.yandex.practicum.filmorate.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.dto.user.UserDtoHtml;
import ru.yandex.practicum.filmorate.dto.user.UserSimpleDto;
import ru.yandex.practicum.filmorate.dto.user.UserUpdateDto;
import ru.yandex.practicum.filmorate.dto.mapping.UserMapping;
import ru.yandex.practicum.filmorate.exception.AlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.ReviewRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class UserServiceDB implements UserService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final UserMapping userMapping;

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(userMapping::toDto).collect(Collectors.toList());
    }

    public List<UserDtoHtml> getUsersForHTML() {
        return userRepository.findAll().stream().map(userMapping::toDtoHtml).collect(Collectors.toList());
    }

    public UserDto createUser(UserDto userDto) {
        User user = userMapping.fromDto(userDto);
        checkEmail(user.getEmail());
        checkLogin(user.getLogin());
        if (user.getName() == null) {
            user.setName(user.getLogin());
            log.info("Для пользователя без имени установлено имя в соответствии с логином: {}", user.getLogin());
        }
        log.info("Пользователь сохранен");
        userRepository.save(user);
        return userMapping.toDto(user);
    }

    public UserDto getUser(String email) {
        return userRepository.findByEmail(email).map(userMapping::toDto)
                .orElseThrow(() -> {String errorMessage = String.format("Пользователь с email '%s' не найден", email);
            log.error(errorMessage);
            return new NotFoundException(errorMessage);
        });
    }

    public UserDto getUser(Long id) {
        return userMapping.toDto(findById(id));
    }

    public void deleteUser(Long id) {
        log.debug("Попытка удалить пользователя с ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {String errorMessage = String.format("Пользователь с id '%s' не найден", id);
            log.error(errorMessage);
            return new NotFoundException(errorMessage);
        });
        log.debug("Попытка удалить все отзывы пользователя с ID: {}", id);
        reviewRepository.deleteAllByUserId(id);
        log.info("Отзывы успешно удалены");
        userRepository.delete(user);
        log.info("Пользователь с ID: {} успешно удален", id);
    }

    public UserDto updateUser(Long id, UserUpdateDto user) {
        User oldUser = findById(id);
        if (!oldUser.getLogin().equals(user.getLogin()) && user.getLogin() != null) {
            log.debug("Попытка обновить логин");
            checkLogin(user.getLogin());
            oldUser.setLogin(user.getLogin());
            log.info("логин: {} успешно установлен", user.getLogin());
        }
        if (!oldUser.getEmail().equals(user.getEmail()) && user.getEmail() != null) {
            log.debug("Попытка обновить email");
            log.debug("Проверка повторного использования email");
            checkEmail(user.getEmail());
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
        return userMapping.toDto(oldUser);
    }

    private void checkEmail(String email) {
        Optional<User> maybeUser = userRepository.findByEmail(email);
        if (maybeUser.isPresent()) {
            String errorMessage = String.format("Пользователь с email '%s' уже существует", email);
            log.error(errorMessage);
            throw new AlreadyExistsException(errorMessage);
        }
    }

    private void checkLogin(String login) {
        Optional<User> maybeUser = userRepository.findByLogin(login);
        if (maybeUser.isPresent()) {
            String errorMessage = String.format("Пользователь с логином '%s' уже существует", login);
            log.error(errorMessage);
            throw new AlreadyExistsException(errorMessage);
        }
    }

    private User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {String errorMessage = String.format("Пользователь с id '%s' не найден", id);
                    log.error(errorMessage);
                    return new NotFoundException(errorMessage);
                });
    }

    @Transactional
    public UserDto addFriend(Long userId, Long friendId) {
        User user = findById(userId);
        User friend = findById(friendId);
        user.addFriend(friend);
        userRepository.save(user);
        userRepository.save(friend);
        return userMapping.toDto(user);
    }

    @Override
    @Transactional
    public void removeFriend(Long userId, Long friendId) {
        User user = findById(userId);
        User friend = findById(friendId);
        user.removeFriend(friend);
        userRepository.save(user);
        userRepository.save(friend);
    }

    @Override
    public List<UserSimpleDto> getFriends(Long userId) {
        User user = findById(userId);
        return user.getFriends().stream().map(userMapping::toDtoFriend).toList();
    }

    @Override
    public List<UserSimpleDto> getCommonFriends(Long userId, Long otherUserId) {
        Set<User> friends1 = findById(userId).getFriends();
        Set<User> friends2 = findById(otherUserId).getFriends();
        Set<User> commonFriends = new HashSet<>(friends1);
        commonFriends.retainAll(friends2);
        return commonFriends.stream().map(userMapping::toDtoFriend).toList();
    }
}
