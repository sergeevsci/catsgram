package ru.yandex.practicum.catsgram.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.catsgram.dal.mappers.PostRowMapper;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.model.SortOrder;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostRepository {
    private final JdbcTemplate jdbc;
    private final PostRowMapper mapper;

    public List<Post> findAll(SortOrder sortOrder, int offset, int size) {
        String direction = sortOrder == SortOrder.DESCENDING ? "DESC" : "ASC";

        String sql = """
                SELECT id, description, post_date, author_id
                FROM posts
                ORDER BY post_date %s
                LIMIT ? OFFSET ?
                """.formatted(direction);

        return jdbc.query(sql, mapper, size, offset);
    }

    public Optional<Post> findById(Long postId) {
        String sql = """
                SELECT id, description, post_date, author_id
                FROM posts
                WHERE id = ?
                """;

        List<Post> posts = jdbc.query(sql, mapper, postId);

        return posts.stream().findFirst();
    }

    public Post create(Post post) {
        String sql = """
                INSERT INTO posts (author_id, description, post_date)
                VALUES (?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, new String[] {"id"});
            statement.setLong(1, post.getAuthorId());
            statement.setString(2, post.getDescription());
            statement.setTimestamp(3, Timestamp.from(post.getPostDate()));
            return statement;
        }, keyHolder);

        post.setId(keyHolder.getKeyAs(Long.class));
        return post;
    }

    public Post update(Post post) {
        String sql = """
                UPDATE posts
                SET description = ?
                WHERE id = ?
                """;

        jdbc.update(sql, post.getDescription(), post.getId());
        return post;
    }
}
