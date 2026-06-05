package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> findAll() {
        return users.values();
    }

    public User create(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }

        // Исправленная проверка уникальности email при создании
        boolean emailExists = users.values().stream()
                .anyMatch(u -> user.getEmail().equalsIgnoreCase(u.getEmail()));
        if (emailExists) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }

        user.setId(getNextId());
        user.setRegistrationDate(Instant.now());
        users.put(user.getId(), user);
        return user;
    }

    public User update(User newUser) {
        if (newUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        User oldUser = users.get(newUser.getId());
        if (oldUser == null) {
            throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
        }

        if (newUser.getEmail() != null) {
            if (newUser.getEmail().isBlank()) {
                throw new ConditionsNotMetException("Имейл не может быть пустым");
            }

            boolean emailBusy = users.values().stream()
                    .anyMatch(u -> !u.getId().equals(newUser.getId())
                            && newUser.getEmail().equalsIgnoreCase(u.getEmail()));

            if (emailBusy) {
                throw new DuplicatedDataException("Этот имейл уже используется другим пользователем");
            }

            oldUser.setEmail(newUser.getEmail());
        }

        if (newUser.getUsername() != null) {
            if (newUser.getUsername().isBlank()) {
                throw new ConditionsNotMetException("Имя пользователя не может быть пустым");
            }
            oldUser.setUsername(newUser.getUsername());
        }

        if (newUser.getPassword() != null) {
            if (newUser.getPassword().isBlank()) {
                throw new ConditionsNotMetException("Пароль не может быть пустым");
            }
            oldUser.setPassword(newUser.getPassword());
        }

        return oldUser;
    }

    public Optional<User> findUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
