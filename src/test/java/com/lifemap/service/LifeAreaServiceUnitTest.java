package com.lifemap.service;

import com.lifemap.TestUtils;
import com.lifemap.model.*;
import com.lifemap.model.projection.LifeAreaDTO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;

import java.util.*;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LifeAreaServiceUnitTest {

    @Mock
    private LifeAreaRepository lifeAreaRepository;

    @InjectMocks
    private LifeAreaService lifeAreaService;

    private final TestUtils testUtils = new TestUtils();

    @Nested
    public class AddLifeAreaTests {
        @Test
        public void shouldReturnFalseWhenArgumentsAreNull() {
            assertFalse(lifeAreaService.addLifeArea(null, null, null));
        }

        @Test
        public void shouldNotAddLifeAreaWhenLifeAreaNameIsDuplicated() {
            //given
            var lifeAreaDTO = createTestLifeAreaDTO("health");
            var wheelOfLife = testUtils.createTestWheelOfLife();
            var sizeBefore = wheelOfLife.getLifeAreas().size();

            var bindingResult = new BeanPropertyBindingResult(lifeAreaDTO, "lifeAreaDTO");

            //when
            var result = lifeAreaService.addLifeArea(lifeAreaDTO, wheelOfLife, bindingResult);
            var sizeAfter = wheelOfLife.getLifeAreas().size();

            //then
            assertFalse(result);
            assertThat(Objects.requireNonNull(bindingResult.getFieldError("name")).getCode(), is("lifeArea.invalid.name.alreadyExists"));
            assertThat(sizeAfter, is(sizeBefore));
        }

        @Test
        public void shouldNotAddLifeAreaWhenLifeAreaNameIsInvalid() {
            //given
            var lifeAreaDTO = createTestLifeAreaDTO(null);
            var wheelOfLife = createTestWheelOfLife();
            var sizeBefore = wheelOfLife.getLifeAreas().size();
            var bindingResult = new BeanPropertyBindingResult(lifeAreaDTO, "lifeAreaDTO");

            //when
            var result = lifeAreaService.addLifeArea(lifeAreaDTO, wheelOfLife, bindingResult);
            var sizeAfter = wheelOfLife.getLifeAreas().size();

            //then
            assertFalse(result);
            assertThat(Objects.requireNonNull(bindingResult.getFieldError("name")).getCode(), is("lifeArea.invalid.name"));
            assertThat(sizeAfter, is(sizeBefore));
        }

        @Test
        public void shouldReturnTrueWhenLifeAreaIsSuccessfullySaved() {
            //given
            var lifeAreaDTO = createTestLifeAreaDTO("health");
            var wheelOfLife = createTestWheelOfLife();
            var sizeBefore = wheelOfLife.getLifeAreas().size();

            var bindingResult = new BeanPropertyBindingResult(lifeAreaDTO, "lifeAreaDTO");

            when(lifeAreaRepository.save(ArgumentMatchers.any(LifeArea.class))).thenAnswer(invocation -> {
                LifeArea la = invocation.getArgument(0);
                la.setId(1L);
                return la;
            });

            //when
            var result = lifeAreaService.addLifeArea(lifeAreaDTO, wheelOfLife, bindingResult);
            var sizeAfter = wheelOfLife.getLifeAreas().size();

            //then
            assertTrue(result);
            assertFalse(bindingResult.hasFieldErrors("name"));
            assertThat(sizeAfter, is(++sizeBefore));
            assertTrue(wheelOfLife.getLifeAreas().stream().anyMatch(area -> area.getName().equals(lifeAreaDTO.getName())));
        }

    }

    @Nested
    public class DeleteLifeAreaTests {
        @ParameterizedTest
        @MethodSource("provideLifeAreas")
        public void shouldReturnFalseWhenArgumentsAreInvalid(String lifeAreaName, WheelOfLife wheelOfLife) {
            lifeAreaService.removeLifeArea(lifeAreaName, wheelOfLife);
        }

        @Test
        public void shouldReturnFalseWhenNameOfLifeNotExists() {
            //given
            var wheelOfLife = testUtils.createTestWheelOfLife();
            wheelOfLife.setId(1L);
            var sizeBefore = wheelOfLife.getLifeAreas().size();

            //when
            var result = lifeAreaService.removeLifeArea("career", wheelOfLife);
            var sizeAfter = wheelOfLife.getLifeAreas().size();

            //then
            assertFalse(result);
            assertThat(sizeAfter, is(sizeBefore));
        }

        @Test
        public void shouldReturnTrueWhenLifeAreaIsSuccessfullyRemoved() {
            //given
            var wheelOfLife = testUtils.createTestWheelOfLife();
            wheelOfLife.setId(1L);
            var sizeBefore = wheelOfLife.getLifeAreas().size();

            //when
            var result = lifeAreaService.removeLifeArea("health", wheelOfLife);
            var sizeAfter = wheelOfLife.getLifeAreas().size();

            //then
            assertTrue(result);
            assertThat(sizeAfter, is((sizeBefore - 1)));
        }

        static Stream<Arguments> provideLifeAreas() {
            WheelOfLife wheelOfLife = new WheelOfLife();
            wheelOfLife.setId(1L);

            return Stream.of(
                    Arguments.of(null, wheelOfLife),
                    Arguments.of("health", null),
                    Arguments.of(null, null),
                    Arguments.of(null, new WheelOfLife())
            );
        }
    }

    private static WheelOfLife createTestWheelOfLife() {
        var wheelOfLife = new WheelOfLife();
        wheelOfLife.setLifeAreas(new HashSet<>());
        wheelOfLife.setId(1L);
        return wheelOfLife;
    }

    private static LifeAreaDTO createTestLifeAreaDTO(String name) {
        var lifeAreaDTO = new LifeAreaDTO();
        lifeAreaDTO.setName(name);
        lifeAreaDTO.setRate((byte) 8);
        return lifeAreaDTO;
    }
}