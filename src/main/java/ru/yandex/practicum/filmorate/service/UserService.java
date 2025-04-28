package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.dto.user.UserDtoHtml;
import ru.yandex.practicum.filmorate.dto.user.UserSimpleDto;
import ru.yandex.practicum.filmorate.dto.user.UserUpdateDto;

import java.util.List;

public interface UserService {

    List<UserDto> getAllUsers();

    List<UserDtoHtml> getUsersForHTML();

    UserDto createUser(UserDto userDto);

    UserDto getUser(String email);

    UserDto getUser(Long id);

    void deleteUser(Long id);

    UserDto updateUser(Long id, UserUpdateDto user);

    UserDto addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    List<UserSimpleDto> getFriends(Long userId);

    List<UserSimpleDto> getCommonFriends(Long userId, Long otherUserId);

}
