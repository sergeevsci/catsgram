package ru.yandex.practicum.catsgram.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.model.SortOrder;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class PostRepository extends BaseRepository<Post> {
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM posts WHERE id = ?";
    private static final String INSERT_QUERY = """
            INSERT INTO posts(author_id, description, post_date)
            VALUES (?, ?, ?)
            RETURNING id
            """;
    private static final String UPDATE_QUERY = """
            UPDATE posts
            SET description = ?
            WHERE id = ?
            """;
    private static final String FIND_BY_ID_QUERY = """
            SELECT id, description, post_date, author_id
            FROM posts
            WHERE id = ?
            """;

    public PostRepository(JdbcTemplate jdbc, RowMapper<Post> mapper) {
        super(jdbc, mapper);
    }

    public List<Post> findAll(SortOrder sortOrder, int offset, int size) {
        String direction = sortOrder == SortOrder.DESCENDING ? "DESC" : "ASC";

        String sql = """
                SELECT id, description, post_date, author_id
                FROM posts
                ORDER BY post_date %s
                LIMIT ? OFFSET ?
                """.formatted(direction);

        return findMany(sql, size, offset);
    }

    public Optional<Post> findById(Long postId) {
        return findOne(FIND_BY_ID_QUERY, postId);
    }

    public Post save(Post post) {
        long id = insert(
                INSERT_QUERY,
                post.getAuthorId(),
                post.getDescription(),
                Timestamp.from(post.getPostDate())
        );
        post.setId(id);
        return post;
    }

    public Post update(Post post) {
        update(UPDATE_QUERY, post.getDescription(), post.getId());
        return post;
    }

    public boolean deleteById(long postId) {
        return delete(DELETE_BY_ID_QUERY, postId);
    }
}
