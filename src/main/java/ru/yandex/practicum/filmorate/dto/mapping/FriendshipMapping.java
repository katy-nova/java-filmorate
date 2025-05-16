package ru.yandex.practicum.filmorate.dto.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.filmorate.dto.FriendshipDto;
import ru.yandex.practicum.filmorate.model.entity.Friendship;

@Mapper(componentModel = "spring")
public interface FriendshipMapping {

    @Mapping(target = "userLogin", expression = "java(friendship.getUser().getLogin())")
    @Mapping(target = "friendLogin", expression = "java(friendship.getFriend().getLogin())")
    FriendshipDto toFriendshipDto(Friendship friendship);
}
