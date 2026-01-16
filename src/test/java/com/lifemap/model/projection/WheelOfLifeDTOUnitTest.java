package com.lifemap.model.projection;

import com.lifemap.TestUtils;
import com.lifemap.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.TestAnnotationUtils;

import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WheelOfLifeDTOUnitTest {

    public final TestUtils testUtils = new TestUtils();

    @Test
    public void shouldMapWheelOfLifeToDTO() {
        //given
        var wheelOfLife = testUtils.createTestWheelOfLife();
        var lifeAreas = wheelOfLife.getLifeAreas();
        var originalNames = lifeAreas.stream()
                .map(LifeArea::getName)
                .toArray();

        //when
        var wheelOfLifeDTO = new WheelOfLifeDTO(wheelOfLife);
        var dtoNames = wheelOfLifeDTO.getLifeAreas().stream()
                .map(LifeAreaDTO::getName)
                .collect(Collectors.toSet());

        //then
        assertThat(wheelOfLifeDTO.getLifeAreas().size(), is(lifeAreas.size()));
        assertThat(dtoNames, containsInAnyOrder(originalNames));
    }

    @Test
    public void shouldReturnSortedLifeAreasDTOByName() {
        //given
        var wheelOfLifeDTO = new WheelOfLifeDTO(testUtils.createTestWheelOfLife());
        var sortedNames = new String[]{"finance", "health", "relationships"};

        //when
        var names = wheelOfLifeDTO.getLifeAreas().stream().map(LifeAreaDTO::getName).collect(Collectors.toList());

        //then
        assertThat(names, containsInRelativeOrder(sortedNames));
    }
}

