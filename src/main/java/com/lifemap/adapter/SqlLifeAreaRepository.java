package com.lifemap.adapter;

import com.lifemap.model.*;
import org.springframework.data.jpa.repository.*;

import java.util.*;

public interface SqlLifeAreaRepository extends LifeAreaRepository, JpaRepository<LifeArea, Long> {
    boolean existsByNameAndWheelOfLifeId(String name, Long wheelOfLifeId);
    Optional<LifeArea> findByNameAndWheelOfLifeId(String name, Long wheelOfLifeId);
    @Query("SELECT l.rate FROM LifeArea l WHERE l.wheelOfLife.id = :wheelOfLifeId")
    List<Byte> findAllRatesByWheelOfLifeId(Long wheelOfLifeId);
    List<LifeArea> findAllByWheelOfLifeId(Long wheelOfLifeId);
    @Modifying
    @Query("DELETE FROM LifeArea l WHERE l.name = :name AND l.wheelOfLife.id = :wheelOfLifeId")
    int deleteByNameAndWheelOfLifeId(String name, Long wheelOfLifeId);
}
