package ru.yandex.practicum.catsgram.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class PostDto {
    private Long id;
    private String description;
    private Instant postDate;
    private Long authorId;
}
