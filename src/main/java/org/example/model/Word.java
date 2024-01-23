package org.example.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Word {
    UUID id;
    String name;
    String translate;
    String description;
    Attachment attachment;
    Category category;
}
