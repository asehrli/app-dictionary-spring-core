package org.example.dao;

import lombok.RequiredArgsConstructor;
import org.example.model.Attachment;
import org.example.model.Word;
import org.example.util.MapperUtil;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class WordDAO {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final AttachmentDAO attachmentDAO;
    private final RowMapper<Word> wordMapper = (rs, rn) -> MapperUtil.mapToWord(rs, "");

    public Optional<Word> save(Word word) {
        Attachment attachment = attachmentDAO.save(word.getAttachment()).orElseThrow();

        String sql = "insert into word (itself, translate, attachment_id, category_id) " +
                "values (:name, :translate, :attachmentId, :categoryId) returning id;";

        var params = Map.of(
                "name", word.getName(),
                "translate", word.getTranslate(),
                "attachmentId", attachment.getId(),
                "categoryId", word.getCategory().getId()
        );


        UUID id = jdbcTemplate.queryForObject(sql, params, UUID.class);

        return getById(id);
    }

    public Optional<Word> getById(UUID id) {
        try {
            String sql = """
                    select
                        w.id as id,
                        w.itself as name,
                        w.translate as translate,
                        c.id as category_id,
                        c.name as category_name,
                        u.id as category_user_id,
                        u.name as category_user_name,
                        u.email as category_user_email,
                        u.password as category_user_password,
                        u.enabled as category_user_enabled
                    from word w
                    join attachment a on a.id = w.attachment_id
                    join category c on c.id = w.category_id
                    join users u on u.id = c.user_id
                    where w.id = :id""";

            var params = Map.of("id", id);

            Word word = jdbcTemplate.queryForObject(sql, params, wordMapper);

            return Optional.ofNullable(word);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<Word> getAllByCategoryId(UUID categoryId) {
        try {
            String sql = """
                    select
                        w.id as id,
                        w.itself as name,
                        w.translate as translate,
                        a.id as attachment_id,
                        a.name as attachment_name,
                        a.url as attachment_url,
                        a.content_type as attachment_content_type,
                        a.size as attachment_size,
                        c.id as category_id,
                        c.name as category_name,
                        u.id as category_user_id,
                        u.name as category_user_name,
                        u.email as category_user_email,
                        u.password as category_user_password,
                        u.enabled as category_user_enabled
                    from word w
                    join attachment a on a.id = w.attachment_id
                    join category c on c.id = w.category_id and c.id = :categoryId
                    join users u on u.id = c.user_id""";

            var params = Map.of("categoryId", categoryId);
            return jdbcTemplate.query(sql, params, wordMapper);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public void delete(UUID id) {
        String sql = "delete from word where id = :id";
        var params = Map.of("id", id);
        jdbcTemplate.update(sql, params);
    }

    @Transactional
    public Optional<Word> edit(UUID id, Word word) {
        Attachment attachment = attachmentDAO.save(word.getAttachment()).orElseThrow();

        String sql = """
                update word
                set itself = :name,
                    translate = :translate,
                    description = :description,
                    attachment_id = :attachmentId
                where id = :id
                returning id""";

        var params = Map.of(
                "name", word.getName(),
                "translate", word.getTranslate(),
                "description", word.getDescription(),
                "attachment_id", attachment.getId()
        );

        UUID editedWordId = jdbcTemplate.queryForObject(sql, params, UUID.class);

        return getById(editedWordId);
    }
}
