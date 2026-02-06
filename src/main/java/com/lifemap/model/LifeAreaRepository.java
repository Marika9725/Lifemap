package com.lifemap.model;

import org.springframework.data.jpa.repository.*;
import org.springframework.lang.NonNull;

import java.util.*;

public interface LifeAreaRepository {
    LifeArea save(LifeArea lifeArea);
    List<Byte> findAllRatesByWheelOfLifeId(Long wheelOfLifeId);
    List<LifeArea> findAllByWheelOfLifeId(Long wheelOfLifeId);

    @Modifying
    @Query("DELETE FROM LifeArea l WHERE l.id = :lifeAreaId")
    int deleteByIdReturningCount(Long lifeAreaId);

    Optional<LifeArea> findById(@NonNull Long id);
}
