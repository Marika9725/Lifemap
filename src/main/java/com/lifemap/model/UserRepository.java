package com.lifemap.model;

import java.util.*;

public interface UserRepository {
    User save(User user);
//    Optional<User> findById(Long id);
//    List<User> findUsersByEmailOrUsername(String email, String username);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
//    Optional<User> findByUsername(String username);
}
