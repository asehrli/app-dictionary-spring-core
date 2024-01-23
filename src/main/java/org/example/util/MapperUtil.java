package org.example.util;

import lombok.experimental.UtilityClass;
import org.example.model.Attachment;
import org.example.model.Category;
import org.example.model.User;
import org.example.model.Word;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@UtilityClass
public class MapperUtil {
    public User mapToUser(ResultSet rs, String prefix) throws SQLException {
        return User.builder()
                .id(rs.getObject(prefix + "id", UUID.class))
                .name(rs.getString(prefix + "name"))
                .email(rs.getString(prefix + "email"))
                .password(rs.getString(prefix + "password"))
                .enabled(rs.getBoolean(prefix + "enabled"))
                .build();
    }

    public Category mapToCategory(ResultSet rs, String prefix) throws SQLException {
        return Category.builder()
                .id(rs.getObject(prefix + "id", UUID.class))
                .name(rs.getString(prefix + "name"))
                .user(MapperUtil.mapToUser(rs, prefix + "user_"))
                .build();
    }

    public Attachment mapToAttachment(ResultSet rs, String prefix) throws SQLException {
        return Attachment.builder()
                .id(rs.getObject(prefix + "id", UUID.class))
                .name(rs.getString(prefix + "name"))
                .url(rs.getString(prefix + "url"))
                .contentType(rs.getString(prefix + "content_type"))
                .size(rs.getLong(prefix + "size"))
                .build();
    }

    public Word mapToWord(ResultSet rs, String prefix) throws SQLException {
        return Word.builder()
                .id(rs.getObject(prefix + "id", UUID.class))
                .name(rs.getString(prefix + "name"))
                .translate(rs.getString(prefix + "translate"))
                .category(MapperUtil.mapToCategory(rs, prefix + "category_"))
                .attachment(MapperUtil.mapToAttachment(rs, prefix + "attachment_"))
                .build();
    }

}
