package ru.yandex.practicum.catsgram.mapper;

import ru.yandex.practicum.catsgram.dto.PostDto;
import ru.yandex.practicum.catsgram.model.Post;

public class PostMapper {
    public static PostDto mapToPostDto(Post post) {
        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setDescription(post.getDescription());
        dto.setPostDate(post.getPostDate());
        dto.setAuthorId(post.getAuthorId());
        return dto;
    }
}
