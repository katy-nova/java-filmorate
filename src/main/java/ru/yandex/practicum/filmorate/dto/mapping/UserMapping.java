package ru.yandex.practicum.filmorate.dto.mapping;

import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.dto.user.UserCreateDto;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.dto.user.UserHtmlDto;
import ru.yandex.practicum.filmorate.dto.user.UserSimpleDto;
import ru.yandex.practicum.filmorate.model.entity.User;

@Mapper(componentModel = "spring", uses = ReviewMapping.class)
public interface UserMapping {
    UserDto toDto(User user);

    User fromDto(UserDto userDto);

    UserHtmlDto toDtoHtml(User user);

    UserSimpleDto toDtoFriend(User user);

    User fromCreateDto(UserCreateDto userCreateDto);
}
