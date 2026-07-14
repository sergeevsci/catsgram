package ru.yandex.practicum.catsgram.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.service.PostFilterRequest;
import ru.yandex.practicum.catsgram.service.PostService;

import java.util.Collection;

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
    public Collection<Post> findAll(PostFilterRequest filter) { // прикол что по итогу @RequestParam не понадобилась
        // так как record-класс делает удобнее обработку хитровыдуманной логики

        // Если не задан ни один параметр вообще — возвращаем дефолт size 10
        if (filter.isEmpty()) {
            return postService.findAllDefault();
        }
        // Если хотя бы один задан — передаем объект record в сервис
        return postService.findAllWithFilters(filter);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Post create(@RequestBody Post post) {
        return postService.create(post);
    }

    @PutMapping
    public Post update(@RequestBody Post newPost) {
        return postService.update(newPost);
    }

    @DeleteMapping("/{postId}") // не удалится из-за каскада. если есть Image у Post
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long postId) {
        postService.delete(postId);
    }
}
