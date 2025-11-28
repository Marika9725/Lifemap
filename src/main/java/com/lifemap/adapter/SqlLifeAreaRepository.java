package com.lifemap.adapter;

import com.lifemap.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SqlLifeAreaRepository extends LifeAreaRepository, JpaRepository<LifeArea, Long> {
    boolean existsByNameAndWheelOfLifeId(String name, Long wheelOfLifeId);
    Optional<LifeArea> findByNameAndWheelOfLifeId(String name, Long wheelOfLifeId);
}
