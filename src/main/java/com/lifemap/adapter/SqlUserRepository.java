package com.lifemap.adapter;

import com.lifemap.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

//TODO: make integration tests with real H2 database
//TODO: userWithGivenUsernameExists
//TODO: userWithGivenUsernameNotFound
//TODO: returnListOfFoundUsersWithGivenEmailOrWithGivenUsername

@Repository
public interface SqlUserRepository extends UserRepository, JpaRepository<User, Long> {
    List<User> findUsersByEmailOrUsername(String email, String username);
    Optional<User> findByUsername(String username);
}
