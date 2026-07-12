package ru.yandex.practicum.catsgram.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

//import ru.yandex.practicum.catsgram.dto.NewUserRequest;
//import ru.yandex.practicum.catsgram.dto.UpdateUserRequest;

import ru.yandex.practicum.catsgram.dto.UserDto;
import ru.yandex.practicum.catsgram.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserMapper {

    public static UserDto mapToUserDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRegistrationDate(user.getRegistrationDate());
        return dto;
    }
}