package ru.yandex.practicum.catsgram.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Post;

import java.time.Instant;
import java.util.*;

// Указываем, что класс PostService - является бином и его
// нужно добавить в контекст приложения
@Service
@RequiredArgsConstructor
public class PostService {
    private final Map<Long, Post> posts = new HashMap<>();
    private final UserService userService;

    public Collection<Post> findAllWithFilters(Optional<String> sort, Optional<Integer> from, Optional<Integer> size) {

        String sortType = sort.orElse("asc"); // по умолчанию берем по возрастанию

        int offset = from.orElse(0); // сколько постов пропустить. 0

        int countPosts = size.orElse(posts.size()); // сколько постов вернуть. берем оставшееся количество в коллекции

        // компаратор для сортировки по дате создания
        Comparator<Post> dateComparator = Comparator.comparing(Post::getPostDate);

        // Если попросили desc, реверс компаратора
        if ("desc".equalsIgnoreCase(sortType)) {
            dateComparator = dateComparator.reversed();
        }

        return posts.values().stream()
                .sorted(dateComparator) // Сортируем sortType
                .skip(offset)           // отбрасываем первые from постов
                .limit(countPosts)      // отбираем последовательно size постов
                .toList();              // возвращаем результат
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
