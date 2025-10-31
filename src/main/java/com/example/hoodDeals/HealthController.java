package com.example.hoodDeals;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

  private final JdbcTemplate jdbc;
  public HealthController(JdbcTemplate jdbc) { this.jdbc = jdbc; }

  @GetMapping("/")
  public String root() {
    return "HoodDeals backend is running ✅";
  }

  @GetMapping("/health/db")
  public String dbHealth() {
    // Simple DB round-trip to Supabase
    String now = jdbc.queryForObject("select now()", String.class);
    return "DB OK, time: " + now;
  }
}
