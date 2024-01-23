package org.example.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.model.Attachment;
import org.springframework.web.multipart.MultipartFile;

public record AddWordDTO(
        @NotBlank
        String name,
        @NotBlank
        String translate,
        String description,
        @NotNull
        MultipartFile image) {
}
