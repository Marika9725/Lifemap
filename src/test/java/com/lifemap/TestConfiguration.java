package com.lifemap;

import com.lifemap.model.*;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.*;

@Configuration
public class TestConfiguration {
    @Bean
    @Primary
    @Profile("integration")
    DataSource dataSource() {
        var dbName = "test-" + UUID.randomUUID();
        var result = new DriverManagerDataSource(
                "jdbc:h2:mem:" + dbName + ";DB_CLOSE_DELAY=-1",
                "sa",
                ""
        );
        result.setDriverClassName("org.h2.Driver");

        return result;
    }


}
