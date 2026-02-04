package com.lifemap.model.projection;

import com.lifemap.TestUtils;
import com.lifemap.model.LifeArea;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LifeAreaReadDTOUnitTest {

    private static final TestUtils testUtils = new TestUtils();

    @Nested
    class LifeAreaReadDTOConstructor {

        @Test
        void shouldThrowsNullPointerExceptionWhenLifeAreaIsNull() {
            assertThrows(NullPointerException.class, () -> new LifeAreaReadDTO(null));
        }

        @ParameterizedTest
        @MethodSource("createInvalidLifeAreas")
        void shouldThrowsNullPointerExceptionWhenLifeAreasVariablesAreInvalid(LifeArea lifeArea) {
            assertThrows(NullPointerException.class, () -> new LifeAreaReadDTO(lifeArea));
        }

        @Test
        void shouldCreateLifeAreaReadDTOFromLifeArea() {
            //given
            var lifeArea = TestUtils.createTestLifeArea();

            //when
            var lifeAreaReadDTO = new LifeAreaReadDTO(lifeArea);

            //then
            assertThat(lifeAreaReadDTO, notNullValue());
        }

        private static Stream<Arguments> createInvalidLifeAreas() {
            return Stream.of(
                    Arguments.of(TestUtils.createTestLifeArea(null, "health", (byte) 5)),
                    Arguments.of(TestUtils.createTestLifeArea(1L, null, (byte) 5))
            );
        }
    }
}
