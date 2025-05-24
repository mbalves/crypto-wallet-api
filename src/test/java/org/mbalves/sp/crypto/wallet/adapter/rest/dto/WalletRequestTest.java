package org.mbalves.sp.crypto.wallet.adapter.rest.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WalletRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenEmailIsValid_thenNoViolations() {
        // Arrange
        WalletRequest request = new WalletRequest();
        request.setEmail("test@example.com");

        // Act
        Set<ConstraintViolation<WalletRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenEmailIsInvalid_thenViolation() {
        // Arrange
        WalletRequest request = new WalletRequest();
        request.setEmail("invalid-email");

        // Act
        Set<ConstraintViolation<WalletRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
        assertEquals("email", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void whenEmailIsNull_thenViolation() {
        // Arrange
        WalletRequest request = new WalletRequest();
        request.setEmail(null);

        // Act
        Set<ConstraintViolation<WalletRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
        assertEquals("email", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void whenEmailIsEmpty_thenViolation() {
        // Arrange
        WalletRequest request = new WalletRequest();
        request.setEmail("");

        // Act
        Set<ConstraintViolation<WalletRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
        assertEquals("email", violations.iterator().next().getPropertyPath().toString());
    }
}
