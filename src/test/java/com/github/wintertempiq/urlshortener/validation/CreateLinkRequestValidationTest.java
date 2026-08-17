package com.github.wintertempiq.urlshortener.validation;

import com.github.wintertempiq.urlshortener.link.dto.CreateLinkRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CreateLinkRequestValidationTest {

    private ValidatorFactory validatorFactory;
    private Validator validator;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        validatorFactory = Validation
                .buildDefaultValidatorFactory();

        validator = validatorFactory.getValidator();

        objectMapper = new ObjectMapper();
    }

    @AfterEach
    void tearDown() {
        validatorFactory.close();
    }

    @Test
    void shouldAcceptValidHttpsUrl() throws Exception {
        String json = """
                {
                    "originalUrl": "https://google.com"
                }
                """;

        CreateLinkRequest request =
                objectMapper.readValue(json, CreateLinkRequest.class);

        Set<ConstraintViolation<CreateLinkRequest>> violations =
                validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldAcceptValidHttpUrl() throws Exception {
        String json = """
            {
                "originalUrl": "http://google.com"
            }
            """;

        CreateLinkRequest request =
                objectMapper.readValue(json, CreateLinkRequest.class);

        Set<ConstraintViolation<CreateLinkRequest>> violations =
                validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldRejectFtpUrl() throws Exception {
        String json = """
                {
                    "originalUrl": "ftp://google.com"
                }
                """;

        CreateLinkRequest request =
                objectMapper.readValue(json, CreateLinkRequest.class);

        Set<ConstraintViolation<CreateLinkRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldRejectJavascriptUrl() throws Exception {
        String json = """
                {
                    "originalUrl": "javascript:alert(1)"
                }
                """;

        CreateLinkRequest request =
                objectMapper.readValue(json, CreateLinkRequest.class);

        Set<ConstraintViolation<CreateLinkRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldRejectFileUrl() throws Exception {
        String json = """
            {
                "originalUrl": "file:///etc/passwd"
            }
            """;

        CreateLinkRequest request =
                objectMapper.readValue(json, CreateLinkRequest.class);

        Set<ConstraintViolation<CreateLinkRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldRejectPlainText() throws Exception {
        String json = """
            {
                "originalUrl": "helloworld"
            }
            """;

        CreateLinkRequest request =
                objectMapper.readValue(json, CreateLinkRequest.class);

        Set<ConstraintViolation<CreateLinkRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldRejectBlankUrl() throws Exception {
        String json = """
            {
                "originalUrl": ""
            }
            """;

        CreateLinkRequest request =
                objectMapper.readValue(json, CreateLinkRequest.class);

        Set<ConstraintViolation<CreateLinkRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldRejectNullUrl() throws Exception {
        String json = """
            {
                "originalUrl": null
            }
            """;

        CreateLinkRequest request =
                objectMapper.readValue(json, CreateLinkRequest.class);

        Set<ConstraintViolation<CreateLinkRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldRejectUrlLongerThan2048Characters() throws Exception {
        String url = "https://google.com/" + "a".repeat(2040);

        String json = """
            {
                "originalUrl": "%s"
            }
            """.formatted(url);

        CreateLinkRequest request =
                objectMapper.readValue(json, CreateLinkRequest.class);

        Set<ConstraintViolation<CreateLinkRequest>> violations =
                validator.validate(request);

        assertTrue(url.length() > 2048);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldRejectMalformedHttpUrl() throws Exception {
        String json = """
            {
                "originalUrl": "https://"
            }
            """;

        CreateLinkRequest request =
                objectMapper.readValue(json, CreateLinkRequest.class);

        Set<ConstraintViolation<CreateLinkRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
    }
}
