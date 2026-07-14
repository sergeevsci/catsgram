package ru.yandex.practicum.catsgram.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.catsgram.model.User;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository extends BaseRepository<User> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_EMAIL_QUERY = "SELECT * FROM users WHERE email = ?";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM users WHERE id = ?";
    private static final String INSERT_QUERY = """
            INSERT INTO users(username, email, password, registration_date)
            VALUES (?, ?, ?, ?)
            RETURNING id
            """;
    private static final String UPDATE_QUERY = """
            UPDATE users
            SET username = ?, email = ?, password = ?
            WHERE id = ?
            """;
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

    public User save(User user) {
        long id = insert(
                INSERT_QUERY,
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                Timestamp.from(user.getRegistrationDate())
        );
        user.setId(id);
        return user;
    }

    public User update(User user) {
        update(
                UPDATE_QUERY,
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getId()
        );
        return user;
    }

    public boolean deleteById(long userId) {
        return delete(DELETE_BY_ID_QUERY, userId);
    }
}
