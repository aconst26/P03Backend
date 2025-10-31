package com.example.hoodDeals;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
@Configuration
public class DatabaseTest {
    @Bean
    CommandLineRunner commandLineRunner(JdbcTemplate jdbcTemplate) {
        return args -> {
            try {
                // Run a simple query to test the connection
                String result = jdbcTemplate.queryForObject("SELECT version();", String.class);
                System.out.println("✅ Connected to Supabase! PostgreSQL version: " + result);
            } catch (Exception e) {
                System.err.println("❌ Database connection failed: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
}
