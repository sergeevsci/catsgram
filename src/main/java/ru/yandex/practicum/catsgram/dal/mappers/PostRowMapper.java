package ru.yandex.practicum.catsgram.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.catsgram.model.Post;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Component
public class PostRowMapper implements RowMapper<Post> {
    @Override
    public Post mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Post post = new Post();
        post.setId(resultSet.getLong("id"));
        post.setAuthorId(resultSet.getLong("authorId"));
        post.setDescription(resultSet.getString("description"));

        Timestamp publicationPost_date = resultSet.getTimestamp("publicationPost_date");
        post.setPostDate(publicationPost_date.toInstant());

        return post;
    }
}
