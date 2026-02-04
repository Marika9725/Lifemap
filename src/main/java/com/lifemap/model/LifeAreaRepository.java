package com.lifemap.model;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.*;

public interface LifeAreaRepository {
//    boolean existsByNameAndWheelOfLifeId(String name, Long wheelOfLifeId);
    LifeArea save(LifeArea lifeArea);
//    Optional<LifeArea> findByNameAndWheelOfLifeId(String name, Long wheelOfLifeId);
    List<Byte> findAllRatesByWheelOfLifeId(Long wheelOfLifeId);
    List<LifeArea> findAllByWheelOfLifeId(Long wheelOfLifeId);
//    int deleteByNameAndWheelOfLifeId(String name, Long wheelOfLifeId);
//    void deleteById(@NonNull Long id);
//    int deleteById(@Param("lifeAreaId") Long id);

    @Modifying
    @Query("DELETE FROM LifeArea l WHERE l.id = :lifeAreaId")
    int deleteByIdReturningCount(Long lifeAreaId);

    Optional<LifeArea> findById(@NonNull Long id);
}
