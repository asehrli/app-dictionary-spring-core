package org.example.dao;

import lombok.RequiredArgsConstructor;
import org.example.model.User;
import org.example.util.MapperUtil;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserDAO {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private static final RowMapper<User> userMapper = (rs, rn) -> MapperUtil.mapToUser(rs, "");
    private static final String EMAIL_WORD = "email";

    public Optional<User> getByEmail(String email) {
        try {
            String sql = "select * from users where email = :email";
            User user = jdbcTemplate.queryForObject(sql, Map.of(EMAIL_WORD, email), userMapper);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty(); // ✅
        }
    }

    public Optional<User> save(User user) {
        String sql = """
                insert into users (name, email, password)
                values (:name, :email, :password)
                returning id, name, email, password, enabled""";

        var params = Map.of(
                "name", user.getName(),
                EMAIL_WORD, user.getEmail(),
                "password", user.getPassword()
        );

        User savedUser = jdbcTemplate.queryForObject(sql, params, userMapper);

        return Optional.ofNullable(savedUser);
    }

    public Optional<User> edit(UUID id, User user) {
        String sql = """
                update users
                set name = :name,
                    email = :email,
                    password = :password
                where id = :id
                returning id, name, email, password, enabled""";

        var params = Map.of(
                "name", user.getName(),
                EMAIL_WORD, user.getEmail(),
                "password", user.getPassword(),
                "id", id
        );

        User savedUser = jdbcTemplate.queryForObject(sql, params, userMapper);

        return Optional.ofNullable(savedUser);
    }

    public List<User> getAll() {
        String sql = "select * from users";
        return jdbcTemplate.query(sql, Map.of(), userMapper);
    }
}
