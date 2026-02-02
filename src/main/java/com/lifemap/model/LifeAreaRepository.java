package com.lifemap.model;

import java.util.*;

public interface LifeAreaRepository {
    boolean existsByNameAndWheelOfLifeId(String name, Long wheelOfLifeId);
    LifeArea save(LifeArea lifeArea);
//    Optional<LifeArea> findByNameAndWheelOfLifeId(String name, Long wheelOfLifeId);
    List<Byte> findAllRatesByWheelOfLifeId(Long wheelOfLifeId);
    List<LifeArea> findAllByWheelOfLifeId(Long wheelOfLifeId);
    int deleteByNameAndWheelOfLifeId(String name, Long wheelOfLifeId);
}
