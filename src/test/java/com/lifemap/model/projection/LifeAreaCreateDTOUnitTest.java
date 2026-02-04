package com.lifemap.model.projection;

import com.lifemap.TestUtils;
import com.lifemap.model.LifeArea;
import org.junit.jupiter.api.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class LifeAreaCreateDTOUnitTest {

    private final TestUtils testUtils = new TestUtils();

    @Nested
    public class ToLifeAreaTest {
        @Test
        public void shouldCreateLifeAreaFromLifeAreaCreateDTO() {
            //given + when
            var lifeAreaCreateDTO = testUtils.createTestLifeAreaCreateDTO();

            //when
            var lifeArea = lifeAreaCreateDTO.toLifeArea();

            //then
            assertThat(lifeArea, is(notNullValue()));
            assertThat(lifeArea.getName(), is("testArea"));
            assertThat(lifeArea.getRate(), is((byte) 8));
            assertThat(lifeArea, instanceOf(LifeArea.class));
        }

        @Test
        public void shouldReturnNullWhenLifeAreaCreateDTOHasNoName() {
            var lifeAreaCreateDTO = testUtils.createTestLifeAreaCreateDTO(null);

            //when
            var lifeArea = lifeAreaCreateDTO.toLifeArea();

            //then
            assertThat(lifeArea, is(nullValue()));
        }
    }
}
