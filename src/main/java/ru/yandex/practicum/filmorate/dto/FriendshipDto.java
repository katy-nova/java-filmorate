package ru.yandex.practicum.filmorate.dto;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.enums.FriendshipStatus;

import java.time.Instant;

@Data
public class FriendshipDto {
    private String userLogin;
    private String friendLogin;
    private FriendshipStatus status;
    private Instant createdAt;
    private Instant updatedAt;
}
