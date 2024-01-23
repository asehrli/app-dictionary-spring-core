package org.example.dao;

import lombok.RequiredArgsConstructor;
import org.example.model.Category;
import org.example.util.MapperUtil;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class CategoryDAO {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private static final RowMapper<Category> categoryMapper = (rs, rowNum) -> MapperUtil.mapToCategory(rs, "");


    public Optional<Category> save(Category category) {
        String sql = "insert into category (name, user_id) values (:name, :userId) returning id;";
        var params = Map.of("name", category.getName(), "userId", category.getUser().getId());
        UUID id = jdbcTemplate.queryForObject(sql, params, UUID.class);
        return getById(id);
    }
    public Optional<Category> getById(UUID id) {
        try {
            String sql = """
                    select
                        c.id as id,
                        c.name as name,
                        u.id as user_id,
                        u.name as user_name,
                        u.email as user_email,
                        u.password as user_password,
                        u.enabled as user_enabled
                    from category c
                    join users u on u.id = c.user_id
                                and c.id = :id""";

            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, Map.of("id", id), categoryMapper));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public boolean exists(String name, UUID userId) {
        String sql = "select exists(select * from category where name = :name and user_id = :userId)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Map.of("name", name, "userId", userId), Boolean.class));
    }

    public List<Category> getAllByUserId(UUID userId) {
        try {
            String sql = """
                    select
                        c.id as id,
                        c.name as name,
                        u.id as user_id,
                        u.name as user_name,
                        u.email as user_email,
                        u.password as user_password,
                        u.enabled as user_enabled
                    from category c
                    join users u on u.id = c.user_id
                    where u.id = :userId""";

            return jdbcTemplate.query(sql, Map.of("userId", userId), categoryMapper);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
    public void delete(UUID id) {
        String sql = "delete from category where id = :id";
        jdbcTemplate.update(sql, Map.of("id", id));
    }
}