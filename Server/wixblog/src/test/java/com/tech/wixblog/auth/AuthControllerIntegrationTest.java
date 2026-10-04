package com.tech.wixblog.auth;

import com.tech.wixblog.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class AuthControllerIntegrationTest {

    private WebTestClient webTestClient;

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        this.webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void shouldRegisterUser() {
        /*
         * This test runs against the configured PostgreSQL database rather than an
         * in-memory or containerised one, so the row survives between runs. A fixed
         * email therefore made the test pass once and then fail with 409 on every
         * subsequent run. A per-run identifier keeps it repeatable.
         *
         * See the notes in the handover: this test still mutates the developer's real
         * database and should be moved to a transactional rollback or Testcontainers.
         */
        String unique = UUID.randomUUID().toString().substring(0, 8);
        String email = "john-" + unique + "@example.com";
        String username = "john" + unique;

        Map<String, String> request = Map.of(
                "email", email,
                "username", username,
                "password", "Password123"
                                            );

        webTestClient.post()
                .uri("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated();

        assertThat(
                userRepository.findByEmailIgnoreCase(email)
                  ).isPresent();
    }
}
