package com.lifemap.service;

import com.lifemap.TestUtils;
import com.lifemap.model.*;
import com.lifemap.model.projection.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;

import java.util.*;

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
}