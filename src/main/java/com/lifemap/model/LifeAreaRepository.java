package com.lifemap.model;

import java.util.Optional;

public interface LifeAreaRepository {
    boolean existsByNameAndWheelOfLifeId(String name, Long wheelOfLifeId);
    LifeArea save(LifeArea lifeArea);
//    Optional<LifeArea> findByNameAndWheelOfLifeId(String name, Long wheelOfLifeId);
}
