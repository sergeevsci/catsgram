package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.User;

import ru.yandex.practicum.catsgram.dal.UserRepository;
import ru.yandex.practicum.catsgram.dto.UserDto;
import ru.yandex.practicum.catsgram.mapper.UserMapper;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDto> getUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public User create(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }

        // Исправленная проверка уникальности email при создании
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }

        user.setRegistrationDate(Instant.now());
        return userRepository.save(user);
    }

    public User update(User newUser) {
        if (newUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        User oldUser = userRepository.findById(newUser.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден"));

        if (newUser.getEmail() != null) {
            if (newUser.getEmail().isBlank()) {
                throw new ConditionsNotMetException("Имейл не может быть пустым");
            }

            if (userRepository.existsByEmailAndIdNot(newUser.getEmail(), newUser.getId())) {
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

        return userRepository.update(oldUser);
    }

    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    public void delete(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        userRepository.deleteById(userId);
    }
}
