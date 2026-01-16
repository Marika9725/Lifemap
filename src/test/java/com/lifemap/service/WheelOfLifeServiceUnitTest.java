package com.lifemap.service;

import com.lifemap.TestUtils;
import com.lifemap.model.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WheelOfLifeServiceUnitTest {

    private final TestUtils testUtils = new TestUtils();

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

            when(messageSource.getMessage(anyString(), any(), ArgumentMatchers.any(Locale.class)))
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

            when(messageSource.getMessage(anyString(), any(), ArgumentMatchers.any(Locale.class)))
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
            var wheelOfLife = new WheelOfLife();
            wheelOfLife.setId(1L);

            when(lifeAreaRepository.findAllRatesByWheelOfLifeId(1L)).thenReturn(Collections.emptyList());

            //when
            var result = wheelOfLifeService.calculateAverage(wheelOfLife);

            //then
            assertThat(result, is(0.0));
        }

        @Test
        public void shouldReturnCorrectAverage() {
            //given
            var wheelOfLife = testUtils.createTestWheelOfLife();
            wheelOfLife.setId(1L);
            var lifeAreas = wheelOfLife.getLifeAreas();
            var listOfRates = lifeAreas.stream().map(LifeArea::getRate).toList();
            var expected = (double) lifeAreas.stream().mapToInt(LifeArea::getRate).sum() / (double) lifeAreas.size();

            when(lifeAreaRepository.findAllRatesByWheelOfLifeId(anyLong())).thenReturn(listOfRates);

            //when
            var actual = wheelOfLifeService.calculateAverage(wheelOfLife);

            //then
            assertThat(actual, is(expected));
        }
    }
}
