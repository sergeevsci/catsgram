package ru.yandex.practicum.catsgram.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.catsgram.dal.mappers.ImageRowMapper;
import ru.yandex.practicum.catsgram.model.Image;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ImageRepository {
    private final JdbcTemplate jdbc;
    private final ImageRowMapper mapper;

    public List<Image> findByPostId(long postId) {
        String sql = """
                SELECT id, original_name, file_path, post_id
                FROM image_storage
                WHERE post_id = ?
                """;

        return jdbc.query(sql, mapper, postId);
    }

    public Optional<Image> findById(long imageId) {
        String sql = """
                SELECT id, original_name, file_path, post_id
                FROM image_storage
                WHERE id = ?
                """;

        List<Image> images = jdbc.query(sql, mapper, imageId);
        return images.stream().findFirst();
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
