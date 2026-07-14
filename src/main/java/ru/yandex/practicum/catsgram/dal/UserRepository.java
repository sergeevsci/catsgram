package ru.yandex.practicum.catsgram.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.catsgram.model.User;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository extends BaseRepository<User> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_EMAIL_QUERY = "SELECT * FROM users WHERE email = ?";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM users WHERE id = ?";
    private static final String EXISTS_BY_EMAIL_QUERY = "SELECT COUNT(*) FROM users WHERE LOWER(email) = LOWER(?)";
    private static final String EXISTS_BY_EMAIL_AND_ID_NOT_QUERY = """
            SELECT COUNT(*)
            FROM users
            WHERE LOWER(email) = LOWER(?) AND id <> ?
            """;

    public UserRepository(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    public List<User> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<User> findByEmail(String email) {
        return findOne(FIND_BY_EMAIL_QUERY, email);
    }

    public Optional<User> findById(Long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public boolean existsByEmail(String email) {
        Integer count = jdbc.queryForObject(EXISTS_BY_EMAIL_QUERY, Integer.class, email);
        return count != null && count > 0;
    }

    public boolean existsByEmailAndIdNot(String email, Long id) {
        Integer count = jdbc.queryForObject(EXISTS_BY_EMAIL_AND_ID_NOT_QUERY, Integer.class, email, id);
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

        update(sql, user.getUsername(), user.getEmail(), user.getPassword(), user.getId());
        return user;
    }

    public boolean deleteById(long userId) {
        return delete(DELETE_BY_ID_QUERY, userId);
    }
}
