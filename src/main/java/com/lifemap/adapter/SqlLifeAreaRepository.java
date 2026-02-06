package com.lifemap.adapter;

import com.lifemap.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.*;

public interface SqlLifeAreaRepository extends LifeAreaRepository, JpaRepository<LifeArea, Long> {
    @Query("SELECT l.rate FROM LifeArea l WHERE l.wheelOfLife.id = :wheelOfLifeId")
    List<Byte> findAllRatesByWheelOfLifeId(Long wheelOfLifeId);
    List<LifeArea> findAllByWheelOfLifeId(Long wheelOfLifeId);
    @Modifying
    @Query("DELETE FROM LifeArea l WHERE l.id = :lifeAreaId")
    int deleteByIdReturningCount(@Param("lifeAreaId") Long id);
    Optional<LifeArea> findById(@NonNull Long id);
}
