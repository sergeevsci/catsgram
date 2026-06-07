package ru.yandex.practicum.catsgram.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.model.SortOrder;

import java.time.Instant;
import java.util.*;

// Указываем, что класс PostService - является бином и его
// нужно добавить в контекст приложения
@Service
@RequiredArgsConstructor
public class PostService {
    private final Map<Long, Post> posts = new HashMap<>();
    private final UserService userService;

    public Collection<Post> findAllWithFilters(PostFilterRequest filter) {

        // Извлекаем значения через Optional-методы рекорда
        SortOrder sortOrder = filter.getSortOrder().orElse(SortOrder.ASCENDING);
        int offset = filter.getFromOptional().orElse(0);
        int countPosts = filter.getSizeOptional().orElse(posts.size());

        // Готовим компаратор по дате выхода поста
        Comparator<Post> dateComparator = Comparator.comparing(Post::getPostDate);

        // если по убыванию - реверс компаратора
        if (sortOrder == SortOrder.DESCENDING) {
            dateComparator = dateComparator.reversed();
        }

        // Фильтр
        return posts.values().stream()
                .sorted(dateComparator) // sortType Сортируем (asc или desc)
                .skip(offset)           // offset Пропускаем первые from постов
                .limit(countPosts)      // countPosts Ограничиваем количество (size)
                .toList();
    }

    public Collection<Post> findAllDefault() {

        int countPosts = 10;

        return posts.values().stream()
                .sorted(Comparator.comparing(Post::getPostDate).reversed()) // desc
                .limit(countPosts) // 10
                .toList();
    }

    public Optional<Post> findPostById(Long postId) {
        return Optional.ofNullable(posts.get(postId));
    }

    public Post create(Post post) {
        if (post.getDescription() == null || post.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }

        // Проверяем, существует ли автор при создании поста
        userService.findUserById(post.getAuthorId())
                .orElseThrow(() -> new ConditionsNotMetException("Автор с id = " + post.getAuthorId() + " не найден"));

        post.setId(getNextId());
        post.setPostDate(Instant.now());
        posts.put(post.getId(), post);
        return post;
    }

    public Post update(Post newPost) {
        if (newPost.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (posts.containsKey(newPost.getId())) {
            Post oldPost = posts.get(newPost.getId());
            if (newPost.getDescription() == null || newPost.getDescription().isBlank()) {
                throw new ConditionsNotMetException("Описание не может быть пустым");
            }
            oldPost.setDescription(newPost.getDescription());
            return oldPost;
        }
        throw new NotFoundException("Пост с id = " + newPost.getId() + " не найден");
    }

    private long getNextId() {
        long currentMaxId = posts.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
