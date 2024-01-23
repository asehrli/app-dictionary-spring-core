package org.example.dao;

import lombok.RequiredArgsConstructor;
import org.example.model.Attachment;
import org.example.util.MapperUtil;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AttachmentDAO {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private static final RowMapper<Attachment> attachmentMapper = (rs, rn) -> MapperUtil.mapToAttachment(rs, "");

    public Optional<Attachment> save(Attachment attachment) {
        String sql = "insert into attachment (name, url, content_type, size) " +
                "values (:name, :url, :contentType, :size) returning id, name, url, content_type, size";

        var params = Map.of(
                "name", attachment.getName(),
                "url", attachment.getUrl(),
                "contentType", attachment.getContentType(),
                "size", attachment.getSize()
        );

        Attachment savedAttachment = jdbcTemplate.queryForObject(sql, params, attachmentMapper);

        return Optional.ofNullable(savedAttachment);
    }

    public void delete(UUID id) {
        String sql = "delete from attachment where id = :id";
        var params = Map.of("id", id);
        jdbcTemplate.update(sql, params);
    }

    public Optional<Attachment> getById(UUID id) {
        try {
            String sql = "select * from attachment where id = :id";
            var params = Map.of("id", id);
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, params, attachmentMapper));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

}
