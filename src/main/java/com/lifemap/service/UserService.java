package com.lifemap.service;

import com.lifemap.model.UserRepository;
import com.lifemap.model.projection.UserDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

@Service
public class UserService {
    private final UserRepository repo;
    private final PasswordEncoder encoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.repo = userRepository;
        this.encoder = passwordEncoder;
    }

    @Transactional
    public boolean register(UserDTO toSave, BindingResult result, String confirmPassword) {
        if (repo.existsByEmail(toSave.getEmail())) {
            result.rejectValue("email", "user.invalid.email.alreadyExists");
        }

        if (repo.existsByUsername(toSave.getUsername())) {
            result.rejectValue("username", "user.invalid.username.alreadyExists");
        }

        if (!toSave.getPassword().equals(confirmPassword)) {
            result.reject("user.invalid.password");
        }

        if (!result.hasErrors()) {
            //TODO: add saving with WheelOfLife and basic LifeAreas
            var saved = repo.save(toSave.toUser(encoder));
            return saved.getId() != null;
        }

        return false;
    }
}
