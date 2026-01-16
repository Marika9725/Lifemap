package com.lifemap.service;

import com.lifemap.model.*;
import com.lifemap.model.projection.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.LocaleResolver;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final WheelOfLifeService wheelOfLifeService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, WheelOfLifeService wheelOfLifeService) {
        this.repo = userRepository;
        this.encoder = passwordEncoder;
        this.wheelOfLifeService = wheelOfLifeService;
    }

    @Transactional
    public boolean register(UserDTO toSave, BindingResult result, String confirmPassword) {
        validUserData(toSave, result, confirmPassword);

        if (!result.hasErrors()) {
            var saved = repo.save(createNewUser(toSave));

            return saved.getId() != null;
        }

        return false;
    }

    private void validUserData(UserDTO toSave, BindingResult result, String confirmPassword) {
        if (repo.existsByEmail(toSave.getEmail())) {
            result.rejectValue("email", "user.invalid.email.alreadyExists");
        }

        if (repo.existsByUsername(toSave.getUsername())) {
            result.rejectValue("username", "user.invalid.username.alreadyExists");
        }

        if (!toSave.getPassword().equals(confirmPassword)) {
            result.reject("user.invalid.password");
        }
    }

    private User createNewUser(UserDTO toSave) {
        var user = toSave.toUser(encoder);
        var wheelOfLife = wheelOfLifeService.createDefaultWheelOfLife();
        user.setWheelOfLife(wheelOfLife);

        return user;
    }
}
