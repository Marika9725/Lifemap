package com.lifemap.model.projection;

import com.lifemap.TestUtils;
import com.lifemap.model.LifeArea;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WheelOfLifeReadDTOUnitTest {

    public final TestUtils testUtils = new TestUtils();

    @Test
    public void shouldThrowsNullablePointExceptionWhenWheelOfLifeIsNull() {
        assertThrows(NullPointerException.class, () -> new WheelOfLifeReadDTO(null));
    }

    @Test
    public void shouldThrowsNullablePointExceptionWhenWheelOfLifeIdIsNull() {
        assertThrows(NullPointerException.class, () -> new WheelOfLifeReadDTO(TestUtils.createTestWheelOfLife(null)));
    }

    @Test
    public void shouldMapWheelOfLifeToReadDTO() {
        //given
        var wheelOfLife = testUtils.createTestWheelOfLifeWithAreas();
        var originalNames = wheelOfLife.getLifeAreas().stream()
                .map(LifeArea::getName)
                .toArray();

        //when
        var wheelOfLifeDTO = new WheelOfLifeReadDTO(wheelOfLife);
        var dtoNames = wheelOfLifeDTO.getLifeAreas().stream()
                .map(LifeAreaReadDTO::getName)
                .collect(Collectors.toList());

        //then
        assertThat(dtoNames.size(), is(originalNames.length));
        assertThat(dtoNames, containsInAnyOrder(originalNames));
    }

}

