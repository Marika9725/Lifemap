package com.lifemap.model;

import org.springframework.lang.NonNull;

import java.util.*;

public interface UserRepository {
    User save(User user);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
//    boolean deleteByEmail(String email);
    void deleteById(@NonNull Long id);
}
