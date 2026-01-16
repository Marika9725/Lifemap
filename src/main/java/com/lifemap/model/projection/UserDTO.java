package com.lifemap.model.projection;

import com.lifemap.model.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    @Email(message = "{user.invalid.email.error}")
    @NotBlank(message = "{user.invalid.email.blank}")
    @Size(min = 6, message = "{user.invalid.email.size}")
    private String email;

    @Size(min = 5, max = 20, message = "{user.invalid.username.size}")
    @NotBlank(message = "{user.invalid.username.blank}")
    private String username;

    @NotBlank(message = "{user.invalid.password.blank}")
    @Size(min = 8, message = "{user.invalid.password.size}")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*.+=-_]).{8,}$",
            message = "{user.invalid.password.pattern}"
    )
    private String password;

    public User toUser(PasswordEncoder encoder) {
        if (email == null || username == null || password == null) return null;

        var user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(encoder.encode(password));
        user.setRole(Role.ROLE_USER);
        return user;
    }
}
