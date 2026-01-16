package com.lifemap.model.projection;

import com.lifemap.TestUtils;
import com.lifemap.model.*;
import org.junit.jupiter.api.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class LifeAreaDtoUnitTest {

    private final TestUtils testUtils = new TestUtils();

    @Nested
    public class LifeAreaDTOConstructorTest {
        @Test
        public void shouldMapLifeAreaToDTO() {
            //given
            var lifeArea = new LifeArea();
            lifeArea.setName("testArea");
            lifeArea.setRate((byte) 8);

            //when
            var lifeAreaDTO = new LifeAreaDTO(lifeArea);

            //then
            assertThat(lifeAreaDTO.getName(), is(lifeArea.getName()));
            assertThat(lifeAreaDTO.getRate(), is(lifeArea.getRate()));
        }
    }

    @Nested
    public class ToLifeAreaTest {
        @Test
        public void shouldCreateLifeAreaFromLifeAreaDTO () {
            //given + when
            var lifeAreaDTO = new LifeAreaDTO();
            lifeAreaDTO.setName("testArea");
            lifeAreaDTO.setRate((byte) 8);

            //when
            var lifeArea = lifeAreaDTO.toLifeArea();

            //then
            assertThat(lifeArea, is(notNullValue()));
            assertThat(lifeArea.getName(), is("testArea"));
            assertThat(lifeArea.getRate(), is((byte) 8));
            assertThat(lifeArea, instanceOf(LifeArea.class));
        }

        @Test
        public void shouldReturnNullWhenLifeAreaDTOHasNoName() {
            var lifeAreaDTO = new LifeAreaDTO();
            lifeAreaDTO.setRate((byte) 5);

            //when
            var lifeArea = lifeAreaDTO.toLifeArea();

            //then
            assertThat(lifeArea, is(nullValue()));
        }
    }
}
