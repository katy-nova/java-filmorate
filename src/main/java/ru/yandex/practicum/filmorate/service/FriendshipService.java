package ru.yandex.practicum.filmorate.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FriendshipDto;
import ru.yandex.practicum.filmorate.dto.mapping.FriendshipMapping;
import ru.yandex.practicum.filmorate.dto.mapping.UserMapping;
import ru.yandex.practicum.filmorate.dto.user.UserSimpleDto;
import ru.yandex.practicum.filmorate.exception.AlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.entity.Friendship;
import ru.yandex.practicum.filmorate.model.entity.User;
import ru.yandex.practicum.filmorate.model.enums.FriendshipStatus;
import ru.yandex.practicum.filmorate.repository.FriendshipRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendshipService {
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final UserMapping userMapping;
    private final FriendshipMapping friendshipMapping;

    public List<UserSimpleDto> getFriendsByUserId(Long userId) {
        User user = findById(userId);
        Set<UserSimpleDto> friends = user.getFriendships().stream()
                .filter(friendship -> friendship.getStatus().equals(FriendshipStatus.ACCEPTED))
                .map(Friendship::getFriend)
                .map(userMapping::toDtoFriend)
                .collect(Collectors.toSet());
        Set<UserSimpleDto> friendsOf = user.getFriendsOf().stream()
                .filter(friendship -> friendship.getStatus().equals(FriendshipStatus.ACCEPTED))
                .map(Friendship::getUser)
                .map(userMapping::toDtoFriend)
                .collect(Collectors.toSet());
        friends.addAll(friendsOf);
        return new ArrayList<>(friends);
    }

    public List<UserSimpleDto> getFollowersByUserId(Long userId) {
        User user = findById(userId);
        return user.getFriendsOf().stream()
                .filter(friendship -> friendship.getStatus().equals(FriendshipStatus.REQUESTED))
                .map(Friendship::getUser)
                .map(userMapping::toDtoFriend)
                .collect(Collectors.toList());
    }

    public List<UserSimpleDto> getFollowingByUserId(Long userId) {
        User user = findById(userId);
        return user.getFriendships().stream()
                .filter(friendship -> friendship.getStatus().equals(FriendshipStatus.REQUESTED))
                .map(Friendship::getFriend)
                .map(userMapping::toDtoFriend)
                .collect(Collectors.toList());
    }

    public List<UserSimpleDto> getBlackList(Long userId) {
        User user = findById(userId);
        return user.getFriendships().stream()
                .filter(friendship -> friendship.getStatus().equals(FriendshipStatus.BLOCKED))
                .map(Friendship::getUser)
                .map(userMapping::toDtoFriend)
                .collect(Collectors.toList());
    }

    public List<UserSimpleDto> getCommonFriends(Long userId, Long otherUserId) {
        User user = findById(userId); // проверяем существование этих пользователей
        User user2 = findById(otherUserId);
        Set<UserSimpleDto> friends1 = new HashSet<>(getFriendsByUserId(userId));
        Set<UserSimpleDto> friends2 = new HashSet<>(getFriendsByUserId(otherUserId));
        friends1.addAll(friends2);
        return new ArrayList<>(friends1);
    }

    @Transactional
    public FriendshipDto sendRequest(Long userId, Long friendId) {
        Optional<Friendship> maybeFriendship = friendshipRepository.findByUserIdAndFriendId(userId, friendId);
        if (maybeFriendship.isPresent()) {
            Friendship friendship = maybeFriendship.get();
            String message;
            switch (friendship.getStatus()) {
                case ACCEPTED:
                    message = "Пользователь уже добавлен в друзья";
                    break;
                case REQUESTED:
                    if (friendship.getUser().getId().equals(userId)) {
                        message = "Заявка уже отправлена";
                    } else if (friendship.getUser().getId().equals(friendId)) {
                        return acceptRequest(userId, friendId);
                    } else {
                        message = "Непредвиденная ошибка";
                    }
                    break;
                case BLOCKED:
                    message = "Невозможно отправить заявку пользователю, который вас заблокировал";
                    break;
                default:
                    message = "Непредвиденная ошибка";
            }
            log.error(message);
            throw new AlreadyExistsException(message);
        }
        User user = findById(userId);
        User friend = findById(friendId);
        Friendship friendship = new Friendship(user, friend, FriendshipStatus.REQUESTED);
        friendshipRepository.save(friendship);
        return friendshipMapping.toFriendshipDto(friendship);
    }

    @Transactional
    public FriendshipDto acceptRequest(Long userId, Long friendId, Long requestId ) {
        Friendship friendship = findFriendshipById(requestId);
            if (friendship.getUser().getId().equals(friendId) && friendship.getFriend().getId().equals(userId)
                    && friendship.getStatus().equals(FriendshipStatus.REQUESTED)) {
                friendship.setStatus(FriendshipStatus.ACCEPTED);
                friendship.setUpdatedAt(Instant.now());
                friendshipRepository.save(friendship);
                return friendshipMapping.toFriendshipDto(friendship);
            } else {
                String message = "Отправлен некорректный запрос";
                log.error(message);
                throw new RuntimeException(message);
            }
    }

    @Transactional
    public FriendshipDto acceptRequest(Long userId, Long friendId) {
        Friendship friendship = findFriendship(userId, friendId);
        if (friendship.getUser().getId().equals(friendId) && friendship.getFriend().getId().equals(userId)
                && friendship.getStatus().equals(FriendshipStatus.REQUESTED)) {
            friendship.setStatus(FriendshipStatus.ACCEPTED);
            friendship.setUpdatedAt(Instant.now());
            return friendshipMapping.toFriendshipDto(friendship);
        } else {
            String message = "Отправлен некорректный запрос";
            log.error(message);
            throw new RuntimeException(message);
        }
    }

    @Transactional
    protected FriendshipDto changStatus(Long userId, Long friendId, FriendshipStatus status) {
        Friendship friendship = findFriendship(userId, friendId);
        if (friendship.getUser().getId().equals(userId)) {
            System.out.println("true");
            friendship.setUser(findById(friendId)); //меняем направленность, чтобы понимать кто на кого подписан
            friendship.setFriend(findById(userId));
        }
        friendship.setStatus(status);
        friendship.setUpdatedAt(Instant.now());
        friendshipRepository.save(friendship);
        return friendshipMapping.toFriendshipDto(friendship);
    }

    @Transactional
    public FriendshipDto addToBlackList(Long userId, Long friendId) {
        return changStatus(userId, friendId, FriendshipStatus.BLOCKED);
    }

    @Transactional
    public FriendshipDto removeFromFriends(Long userId, Long friendId) {
        return changStatus(userId, friendId, FriendshipStatus.REQUESTED);
    }

    // при удалении человека из черного списка стирается запись о дружбе
    @Transactional
    public void removeFromBlackList(Long userId, Long friendId) {
        Friendship friendship = findFriendship(userId, friendId);
        friendshipRepository.delete(friendship);
    }

    private Friendship findFriendship(Long userId, Long friendId) {
        return friendshipRepository.findByUserIdAndFriendId(userId, friendId).orElseThrow(() ->
                {String message = String.format("Связь между пользователями с id: %s, %s не найдена", userId, friendId);
                    log.error(message);
                    return new NotFoundException(message);
                }
        );
    }

    private Friendship findFriendshipById(Long friendshipId) {
        return friendshipRepository.findById(friendshipId).orElseThrow(() ->
                {String message = String.format("Заявка с id: %s не найдена", friendshipId);
                    log.error(message);
                    return new NotFoundException(message);
                }
        );
    }

    private User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    String errorMessage = String.format("Пользователь с id '%s' не найден", id);
                    log.error(errorMessage);
                    return new NotFoundException(errorMessage);
                });
    }
}
