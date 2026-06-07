package ru.yandex.practicum.catsgram.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.service.PostService;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping("/{postId}")
    public Post findById(@PathVariable long postId) {
        return postService.findPostById(postId)
                .orElseThrow(() -> new NotFoundException("Пост с id = " + postId + " не найден"));
    }

    @GetMapping
    public Collection<Post> findAll(
            @RequestParam(name = "sort") Optional<String> sort, // сортировка asc/desc, необяз параметр. null если ничего
            @RequestParam(name = "from") Optional<Integer> from, // сколько постов пропускаем (от какого выводим)
            @RequestParam(name = "size") Optional<Integer> size // сколько постов надо вывести
    ) {
        // если не задан ни один параметр вообще (тогда только size 10)
        if (sort.isEmpty() && from.isEmpty() && size.isEmpty()) {
            return postService.findAllDefault(); // все дефолтное
        }
        // если хотя бы один задан, используем только то, что пришло.
        return postService.findAllWithFilters(sort, from, size);
    }

    @PostMapping
    public Post create(@RequestBody Post post) {
        return postService.create(post);
    }

    @PutMapping
    public Post update(@RequestBody Post newPost) {
        return postService.update(newPost);
    }
}