package ru.yandex.practicum.catsgram.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.catsgram.dal.mappers.UserRowMapper;
import ru.yandex.practicum.catsgram.model.User;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final JdbcTemplate jdbc;
    private final UserRowMapper mapper;

    public List<User> findAll() {
        String query = "SELECT * FROM users";
        return jdbc.query(query, mapper);
    }

    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        List<User> users = jdbc.query(sql, mapper, id);
        return users.stream().findFirst();
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE LOWER(email) = LOWER(?)";
        Integer count = jdbc.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    public boolean existsByEmailAndIdNot(String email, Long id) {
        String sql = "SELECT COUNT(*) FROM users WHERE LOWER(email) = LOWER(?) AND id <> ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, email, id);
        return count != null && count > 0;
    }

    public User create(User user) {
        String sql = """
                INSERT INTO users (username, email, password, registration_date)
                VALUES (?, ?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, new String[] {"id"});
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassword());
            statement.setTimestamp(4, Timestamp.from(user.getRegistrationDate()));
            return statement;
        }, keyHolder);

        user.setId(keyHolder.getKeyAs(Long.class));
        return user;
    }

    public User update(User user) {
        String sql = """
                UPDATE users
                SET username = ?, email = ?, password = ?
                WHERE id = ?
                """;

        jdbc.update(sql, user.getUsername(), user.getEmail(), user.getPassword(), user.getId());
        return user;
    }
}
