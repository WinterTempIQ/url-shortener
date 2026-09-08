package com.github.wintertempiq.urlshortener;

import com.github.wintertempiq.urlshortener.user.entity.User;
import com.github.wintertempiq.urlshortener.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("docker")
@Testcontainers
class FlywayMigrationIntegrationTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16")
                    .withDatabaseName("shortener")
                    .withUsername("test")
                    .withPassword("test");

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void flyway_appliedAllMigrations_tablesExistInRealPostgres() {
        for (String table : new String[]{"users", "links", "refresh_tokens"}) {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT count(*) FROM information_schema.tables WHERE table_schema = 'public' AND table_name = ?",
                    Integer.class,
                    table
            );
            assertEquals(1, count, "Таблица '" + table + "' не найдена после миграции Flyway");
        }
    }

    @Test
    void entity_savesIntoRealPostgres() {
        User user = new User();
        user.setEmail("integration@example.com");
        user.setPassword("$2a$10$dummyHashValueForIntegrationTestOnly123");

        User saved = userRepository.save(user);

        assertNotNull(saved.getId());
        assertEquals("integration@example.com", saved.getEmail());

        Integer count = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM users WHERE email = ?",
                Integer.class,
                "integration@example.com"
        );
        assertEquals(1, count, "Пользователь не найден в реальном Postgres после save()");
    }
}