package ru.yandex.practicum.catsgram.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.catsgram.model.Image;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class ImageRepository extends BaseRepository<Image> {
    private static final String FIND_BY_POST_ID_QUERY = """
            SELECT id, original_name, file_path, post_id
            FROM image_storage
            WHERE post_id = ?
            """;
    private static final String FIND_BY_ID_QUERY = """
            SELECT id, original_name, file_path, post_id
            FROM image_storage
            WHERE id = ?
            """;

    public ImageRepository(JdbcTemplate jdbc, RowMapper<Image> mapper) {
        super(jdbc, mapper);
    }

    public List<Image> findByPostId(long postId) {
        return findMany(FIND_BY_POST_ID_QUERY, postId);
    }

    public Optional<Image> findById(long imageId) {
        return findOne(FIND_BY_ID_QUERY, imageId);
    }

    public Image create(Image image) {
        String sql = """
                INSERT INTO image_storage (original_name, file_path, post_id)
                VALUES (?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, new String[] {"id"});
            statement.setString(1, image.getOriginalFileName());
            statement.setString(2, image.getFilePath());
            statement.setLong(3, image.getPostId());
            return statement;
        }, keyHolder);

        image.setId(keyHolder.getKeyAs(Long.class));
        return image;
    }
}
