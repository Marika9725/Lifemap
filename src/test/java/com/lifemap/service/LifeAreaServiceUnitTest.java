package com.lifemap.service;

import com.lifemap.model.*;
import com.lifemap.model.projection.LifeAreaDTO;
import lombok.NonNull;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LifeAreaServiceUnitTest {

    @Mock
    private LifeAreaRepository lifeAreaRepository;

    @InjectMocks
    private LifeAreaService lifeAreaService;

    @Nested
    public class AddLifeAreaTests {
        @Test
        public void shouldReturnFalseWhenArgumentsAreNull() {
            assertFalse(lifeAreaService.addLifeArea(null, null, null));
        }

        @Test
        public void shouldReturnFalseWhenNameOfLifeAreaAlreadyExists() {
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
        public void shouldNotAddLifeAreaWhenLifeAreaNameIsDuplicate() {
            //given
            var lifeAreaDTO = createTestLifeAreaDTO("health");
            var wheelOfLife = createTestWheelOfLife();
            var sizeBefore = wheelOfLife.getLifeAreas().size();

            var bindingResult = new BeanPropertyBindingResult(lifeAreaDTO, "lifeAreaDTO");

            when(lifeAreaRepository.existsByNameAndWheelOfLifeId(anyString(), anyLong())).thenReturn(true);

            //when
            var result = lifeAreaService.addLifeArea(lifeAreaDTO, wheelOfLife, bindingResult);
            var sizeAfter = wheelOfLife.getLifeAreas().size();

            //then
            assertFalse(result);
            assertThat(Objects.requireNonNull(bindingResult.getFieldError("name")).getCode(), is("lifeArea.invalid.name.alreadyExists"));
            assertThat(sizeAfter, is(sizeBefore));
        }

        @Test
        public void shouldReturnTrueWhenLifeAreaIsSuccessfullySaved() {
            //given
            var lifeAreaDTO = createTestLifeAreaDTO("health");
            var wheelOfLife = createTestWheelOfLife();
            var sizeBefore = wheelOfLife.getLifeAreas().size();

            var bindingResult = new BeanPropertyBindingResult(lifeAreaDTO, "lifeAreaDTO");

            when(lifeAreaRepository.existsByNameAndWheelOfLifeId(anyString(), anyLong())).thenReturn(false);
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
}
