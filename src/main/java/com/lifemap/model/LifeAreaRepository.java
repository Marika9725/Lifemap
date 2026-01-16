package com.lifemap.model;

import org.springframework.data.jpa.repository.Query;

import java.util.*;

public interface LifeAreaRepository {
    boolean existsByNameAndWheelOfLifeId(String name, Long wheelOfLifeId);
    LifeArea save(LifeArea lifeArea);
//    Optional<LifeArea> findByNameAndWheelOfLifeId(String name, Long wheelOfLifeId);
    List<Byte> findAllRatesByWheelOfLifeId(Long wheelOfLifeId);
    List<LifeArea> findAllByWheelOfLifeId(Long wheelOfLifeId);
}
