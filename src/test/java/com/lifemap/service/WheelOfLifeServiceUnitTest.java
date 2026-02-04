package com.lifemap.service;

import com.lifemap.TestUtils;
import com.lifemap.model.*;
import com.lifemap.model.projection.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.*;
import java.util.stream.*;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.is;

@ExtendWith(MockitoExtension.class)
public class WheelOfLifeServiceUnitTest {

    @Mock
    public LifeAreaRepository lifeAreaRepository;

    @Mock
    public MessageSource messageSource;

    @InjectMocks
    public WheelOfLifeService wheelOfLifeService;

    @Nested
    public class CreateDefaultWheelOfLifeTests {
        @ParameterizedTest
        @ValueSource(strings = {"pl", "en"})
        public void shouldCreateDefaultWheelOfLifeWithDefaultLifeAreasWithNamesDependsOnLocaleLanguage(String language) {
            //given
            LocaleContextHolder.setLocale(Locale.forLanguageTag(language));

            when(messageSource.getMessage(anyString(), any(), any(Locale.class)))
                    .thenAnswer(invocation -> {
                        String code = invocation.getArgument(0);
                        Locale locale = invocation.getArgument(2);
                        return code + "-" + locale;
                    });

            //when
            var result = wheelOfLifeService.createDefaultWheelOfLife();
            var numLifeAreas = result.getLifeAreas().size();
            var names = result.getLifeAreas().stream()
                    .map(LifeArea::getName)
                    .collect(Collectors.toSet());

            //then
            assertThat(result, notNullValue());
            assertThat(numLifeAreas, is(6));
            assertThat(names, hasItems(
                    "lifeArea.health-" + language,
                    "lifeArea.career-" + language,
                    "lifeArea.finances-" + language,
                    "lifeArea.personalDevelopment-" + language,
                    "lifeArea.fun-" + language,
                    "lifeArea.relationships-" + language
            ));
        }

        @Test
        public void lifeAreasShouldHavePolishNamesWhenLanguageIsNotSupported() {
            //given
            LocaleContextHolder.setLocale(Locale.forLanguageTag("de"));

            when(messageSource.getMessage(anyString(), any(), any(Locale.class)))
                    .thenAnswer(invocation -> {
                        String code = invocation.getArgument(0);
                        Locale locale = invocation.getArgument(2);
                        return code + "-" + locale;
                    });

            //when
            var result = wheelOfLifeService.createDefaultWheelOfLife();
            var numLifeAreas = result.getLifeAreas().size();
            var names = result.getLifeAreas().stream()
                    .map(LifeArea::getName)
                    .collect(Collectors.toSet());

            //then
            assertThat(result, notNullValue());
            assertThat(numLifeAreas, is(6));
            assertThat(names, hasItems(
                    "lifeArea.health-pl",
                    "lifeArea.career-pl",
                    "lifeArea.finances-pl",
                    "lifeArea.personalDevelopment-pl",
                    "lifeArea.fun-pl",
                    "lifeArea.relationships-pl"
            ));
        }
    }

    @Nested
    public class CalculateAverageTests {
        @Test
        public void shouldReturnZeroWhenGivenWheelOfLifeIsNull() {
            assertThat(wheelOfLifeService.calculateAverage(null), is(0.0));
        }

        @Test
        public void shouldReturnZeroWhenThereAreNoLifeAreasInGivenWheelOfLife() {
            //given
            var wheelOfLife = TestUtils.createTestWheelOfLife();
            var wheelOfLifeReadDTO = new WheelOfLifeReadDTO(wheelOfLife);

            //when
            var result = wheelOfLifeService.calculateAverage(wheelOfLifeReadDTO.getLifeAreas());

            //then
            assertThat(result, is(0.0));
        }

        @Test
        public void shouldReturnCorrectAverage() {
            //given
            var lifeAreaDTO1 = new LifeAreaReadDTO(TestUtils.createTestLifeArea());
            var lifeAreaDTO2 = new LifeAreaReadDTO(TestUtils.createTestLifeArea(2L, "testLifeArea", (byte) 4));
            var lifeAreaDTO3 = new LifeAreaReadDTO(TestUtils.createTestLifeArea(3L, "testLifeArea", (byte) 3));
            var lifeAreas = List.of(lifeAreaDTO1, lifeAreaDTO2, lifeAreaDTO3);
            var sumRates = lifeAreas.stream().mapToDouble(LifeAreaReadDTO::getRate).sum();
            var expected = sumRates / (double) lifeAreas.size();

            //when
            var actual = wheelOfLifeService.calculateAverage(lifeAreas);

            //then
            assertThat(actual, is(expected));
        }
    }
}
