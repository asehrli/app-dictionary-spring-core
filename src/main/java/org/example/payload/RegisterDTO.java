package org.example.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class RegisterDTO {
    @NotBlank(message = "name is required")
    String name;

    @Email(message = "email is invalid")
    @NotBlank(message = "email is required")
    String email;

    @NotBlank(message = "password is required")
    String password;

    @NotBlank(message = "pre password is required")
    String prePassword;
}
