package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.dal.UserRepository;
import ru.yandex.practicum.catsgram.dto.NewUserRequest;
import ru.yandex.practicum.catsgram.dto.UpdateUserRequest;
import ru.yandex.practicum.catsgram.dto.UserDto;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.mapper.UserMapper;
import ru.yandex.practicum.catsgram.model.User;

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

    public UserDto getUserById(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с ID: " + userId));

        return UserMapper.mapToUserDto(user);
    }

    public UserDto createUser(NewUserRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }

        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new ConditionsNotMetException("Имя пользователя должно быть указано");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ConditionsNotMetException("Пароль должен быть указан");
        }

        // Исправленная проверка уникальности email при создании
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }

        User user = UserMapper.mapToUser(request);
        return UserMapper.mapToUserDto(userRepository.save(user));
    }

    public UserDto updateUser(long userId, UpdateUserRequest request) {
        if (request.hasEmail()) {
            if (userRepository.existsByEmailAndIdNot(request.getEmail(), userId)) {
                throw new DuplicatedDataException("Этот имейл уже используется другим пользователем");
            }
        }

        User updatedUser = userRepository.findById(userId)
                .map(user -> UserMapper.updateUserFields(user, request))
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        return UserMapper.mapToUserDto(userRepository.update(updatedUser));
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
