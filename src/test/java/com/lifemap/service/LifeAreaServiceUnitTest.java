package com.lifemap.service;

import com.lifemap.TestUtils;
import com.lifemap.model.*;
import com.lifemap.model.projection.LifeAreaReadDTO;
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
            var lifeAreaDTO = testUtils.createTestLifeAreaCreateDTO("health");
            var wheelOfLife = testUtils.createTestWheelOfLifeWithAreas();
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
            var lifeAreaDTO = testUtils.createTestLifeAreaCreateDTO(null);
            var wheelOfLife = TestUtils.createTestWheelOfLife();
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
            var lifeAreaDTO = testUtils.createTestLifeAreaCreateDTO("health");
            var wheelOfLife = TestUtils.createTestWheelOfLife();
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
        @NullSource
        @ValueSource(longs = -1)
        public void shouldReturnFalseWhenArgumentsAreInvalid(Long lifeAreaId) {
            assertFalse(lifeAreaService.removeLifeArea(lifeAreaId));
            verify(lifeAreaRepository, never()).deleteByIdReturningCount(anyLong());
        }

        @Test
        public void shouldReturnFalseWhenLifeAreaIdNotExists() {
            //given
            var wheelOfLife = testUtils.createTestWheelOfLifeWithAreas();
            var sizeBefore = wheelOfLife.getLifeAreas().size();

            //when
            var result = lifeAreaService.removeLifeArea(4L);
            var sizeAfter = wheelOfLife.getLifeAreas().size();

            //then
            assertFalse(result);
            assertThat(sizeAfter, is(sizeBefore));
            verify(lifeAreaRepository, times(1)).deleteByIdReturningCount(anyLong());
        }

        @Test
        public void shouldReturnTrueWhenLifeAreaIsSuccessfullyRemoved() {
            //given
            when(lifeAreaRepository.deleteByIdReturningCount(1L)).thenReturn(1);

            //when
            var result = lifeAreaService.removeLifeArea(1L);

            //then
            assertTrue(result);
            verify(lifeAreaRepository, times(1)).deleteByIdReturningCount(1L);
        }
    }

    @Nested
    public class UpdateRateTests {

        @ParameterizedTest
        @NullSource
        @ValueSource(longs = {-1L})
        void shouldReturnFalseWhenIdIsInvalid(Long lifeAreaId) {
            //given + when
            var result = lifeAreaService.updateRate(lifeAreaId, (byte) 5);

            //then
            assertFalse(result);
            verify(lifeAreaRepository, never()).findById(anyLong());
        }

        @ParameterizedTest
        @ValueSource(bytes = {-1, 11})
        void shouldReturnFalseWhenRateIsInvalid(byte lifeAreaRate) {
            //given + when
            var result = lifeAreaService.updateRate(1L, lifeAreaRate);

            //then
            assertFalse(result);
            verify(lifeAreaRepository, never()).findById(anyLong());
        }

        @Test
        void shouldReturnFalseWhenLifeAreaNotFound() {
            //given
            when(lifeAreaRepository.findById(anyLong())).thenReturn(Optional.empty());

            //when
            var result = lifeAreaService.updateRate(1L, (byte) 5);

            //then
            assertFalse(result);
            verify(lifeAreaRepository, times(1)).findById(anyLong());
        }

        @Test
        void shouldReturnTrueWhenLifeAreaRateIsSuccessfullyChanged() {
            //given
            var lifeArea = TestUtils.createTestLifeArea();
            when(lifeAreaRepository.findById(anyLong())).thenReturn(Optional.of(lifeArea));

            //when
            var result = lifeAreaService.updateRate(1L, (byte) 5);

            //then
            assertTrue(result);
            verify(lifeAreaRepository, times(1)).findById(anyLong());
        }
    }

    @Nested
    public class GetSortedLifeAreasTests {
        @ParameterizedTest
        @NullSource
        @ValueSource(longs = -1)
        public void shouldReturnEmptyListWhenWheelOfLifeIsValid(Long wheelOfLifeId) {
            //given + when
            var result = lifeAreaService.getSortedLifeAreas(wheelOfLifeId, SortBy.NAME_ASC);

            //then
            assertThat(result, is(List.of()));
            assertThat(result.size(), is(0));
        }

        @Test
        public void shouldReturnEmptyListWhenLifeAreasNotFound() {
            //given
            when(lifeAreaRepository.findAllByWheelOfLifeId(anyLong())).thenReturn(List.of());

            //when
            var result = lifeAreaService.getSortedLifeAreas(1L, SortBy.NAME_ASC);

            //then
            assertThat(result, is(List.of()));
            assertThat(result.size(), is(0));
        }

        @Test
        public void shouldSortByNameAscendingWhenSortByIsNull() {
            //given
            var lifeAreas = testUtils.createTestLifeAreas();
            var namesExpected = lifeAreas.stream().map(LifeArea::getName).sorted().toList();
            when(lifeAreaRepository.findAllByWheelOfLifeId(anyLong())).thenReturn(lifeAreas);

            //when
            var result = lifeAreaService.getSortedLifeAreas(1L, null);
            var namesActual = result.stream().map(LifeAreaReadDTO::getName).toList();

            //then
            assertThat(namesActual, is(namesExpected));
        }

        @ParameterizedTest
        @MethodSource("getSortStrategy")
        public void shouldSortLifeAreasCorrectly(Comparator<LifeArea> comparator, SortBy sortBy) {
            //given
            var lifeAreas = testUtils.createTestLifeAreas();
            var namesOrderExpected = lifeAreas.stream().sorted(comparator).map(LifeArea::getName).toList();

            when(lifeAreaRepository.findAllByWheelOfLifeId(anyLong())).thenReturn(lifeAreas);

            //when
            var result = lifeAreaService.getSortedLifeAreas(1L, sortBy);
            var namesOrderActual = result.stream().map(LifeAreaReadDTO::getName).toList();

            //then
            assertThat(namesOrderActual, is(namesOrderExpected));
        }

        public static Stream<Arguments> getSortStrategy() {
            return Stream.of(
                    Arguments.of(Comparator.comparing(LifeArea::getName), SortBy.NAME_ASC),
                    Arguments.of(Comparator.comparing(LifeArea::getName).reversed(), SortBy.NAME_DESC),
                    Arguments.of(Comparator.comparing(LifeArea::getRate), SortBy.RATE_ASC),
                    Arguments.of(Comparator.comparing(LifeArea::getRate).reversed(), SortBy.RATE_DESC)
            );
        }
    }

    @Nested
    public class GetWorstLifeAreasTests {
        @Test
        public void shouldReturnEmptyListWhenWheelOfLifeIdIsNull() {
            //given + when
            var result = lifeAreaService.getWorstLifeAreas(null, 5.5);

            //then
            verify(lifeAreaRepository, never()).findAllByWheelOfLifeId(anyLong());
            assertThat(result, is(List.of()));
            assertThat(result.size(), is(0));
        }

        @Test
        public void shouldReturnEmptyListWhenAverageIsZero() {
            // given
            var lifeAreas = testUtils.createTestLifeAreas();
            when(lifeAreaRepository.findAllByWheelOfLifeId(anyLong())).thenReturn(lifeAreas);

            // when
            var result = lifeAreaService.getWorstLifeAreas(1L, 0.0);

            //then
            verify(lifeAreaRepository, times(1)).findAllByWheelOfLifeId(anyLong());
            assertThat(result, is(List.of()));
            assertThat(result.size(), is(0));
        }

        @Test
        public void shouldReturnEmptyListWhenThereAreNoLifeAreasBelowAverage() {
            //given
            var lifeAreas = testUtils.createTestLifeAreas();
            lifeAreas.forEach(la -> la.setRate((byte) 8));

            when(lifeAreaRepository.findAllByWheelOfLifeId(anyLong())).thenReturn(lifeAreas);

            //when
            var result = lifeAreaService.getWorstLifeAreas(1L, 8.0);

            //then
            verify(lifeAreaRepository, times(1)).findAllByWheelOfLifeId(anyLong());
            assertThat(result, is(List.of()));
            assertThat(result.size(), is(0));
        }

        @Test
        public void shouldReturnLifeAreasWithRatesBelowAverage() {
            // given
            var lifeAreas = testUtils.createTestLifeAreas();
            var average = lifeAreas.stream()
                    .mapToDouble(LifeArea::getRate)
                    .average()
                    .orElse(0.0);
            var expected = lifeAreas.stream().filter(la -> la.getRate() < Math.ceil(average)).toList();

            when(lifeAreaRepository.findAllByWheelOfLifeId(anyLong())).thenReturn(lifeAreas);

            // when
            var actual = lifeAreaService.getWorstLifeAreas(1L, average);

            // then
            verify(lifeAreaRepository, times(1)).findAllByWheelOfLifeId(anyLong());
            assertThat(actual.size(), is(expected.size()));
            assertTrue(actual.stream().allMatch(la -> la.getRate() < Math.ceil(average)));
        }
    }
}