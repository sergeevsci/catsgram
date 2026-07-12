package ru.yandex.practicum.catsgram.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.dal.PostRepository;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.model.SortOrder;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;

// Указываем, что класс PostService - является бином и его
// нужно добавить в контекст приложения
@Service
@RequiredArgsConstructor
public class PostService {
    private final UserService userService;

    private final PostRepository postRepository;

    public Collection<Post> findAllWithFilters(PostFilterRequest filter) {
        SortOrder sortOrder = filter.getSortOrder().orElse(SortOrder.ASCENDING);
        int offset = filter.getFromOptional().orElse(0);
        int countPosts = filter.getSizeOptional().orElse(10);

        return postRepository.findAll(sortOrder, offset, countPosts);
    }

    public Collection<Post> findAllDefault() {
        return postRepository.findAll(SortOrder.DESCENDING, 0, 10);
    }

    public Optional<Post> findPostById(Long postId) {
        return postRepository.findById(postId);
    }

    public Post create(Post post) {
        if (post.getDescription() == null || post.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }

        // Проверяем, существует ли автор при создании поста
        userService.findUserById(post.getAuthorId())
                .orElseThrow(() -> new ConditionsNotMetException("Автор с id = " + post.getAuthorId() + " не найден"));

        post.setPostDate(Instant.now());
        return postRepository.create(post);
    }

    public Post update(Post newPost) {
        if (newPost.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        Post oldPost = postRepository.findById(newPost.getId())
                .orElseThrow(() -> new NotFoundException("Пост с id = " + newPost.getId() + " не найден"));

        if (newPost.getDescription() == null || newPost.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }
        oldPost.setDescription(newPost.getDescription());

        return postRepository.update(oldPost);
    }
}
