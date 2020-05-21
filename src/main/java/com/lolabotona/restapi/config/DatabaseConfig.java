package com.lolabotona.restapi.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("mysql://bcb8795a7f5b8d:815838d8@us-cdbr-east-06.cleardb.net/heroku_e5e1c7c98d8ce9f?reconnect=true\r\n");
        return new HikariDataSource(config);
    }
}