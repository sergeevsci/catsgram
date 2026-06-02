package ru.yandex.practicum.catsgram.controller;

import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;

import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        // проверяем выполнение необходимых условий
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }
        if (users.containsValue(user)) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
        // формируем дополнительные данные
        user.setId(getNextId());
        user.setRegistrationDate(Instant.now());
        // сохраняем новую публикацию в памяти приложения
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
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

            // Проверяем уникальность email: ищем его у других пользователей
            boolean emailBusy = users.values().stream()
                    .anyMatch(user -> !user.getId().equals(newUser.getId())
                            && newUser.getEmail().equalsIgnoreCase(user.getEmail()));

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

    // вспомогательный метод для генерации идентификатора нового поста
    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
