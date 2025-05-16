package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.user.*;

import java.util.List;

public interface UserService {

    List<UserDto> getAllUsers();

    List<UserHtmlDto> getUsersForHTML();

    UserDto createUser(UserCreateDto userDto);

    UserDto getUser(String email);

    UserDto getUser(Long id);

    void deleteUser(Long id);

    UserDto updateUser(Long id, UserUpdateDto user);

//    UserDto addFriend(Long userId, Long friendId);

//    void removeFriend(Long userId, Long friendId);
//
//    List<UserSimpleDto> getFriends(Long userId);
//
//    List<UserSimpleDto> getCommonFriends(Long userId, Long otherUserId);

}
