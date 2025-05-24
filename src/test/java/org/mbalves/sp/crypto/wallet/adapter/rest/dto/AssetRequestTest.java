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

class AssetRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenAllFieldsValid_thenNoViolations() {
        // Arrange
        AssetRequest request = new AssetRequest();
        request.setSymbol("BTC");
        request.setQuantity(0.5);

        // Act
        Set<ConstraintViolation<AssetRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenSymbolIsBlank_thenViolation() {
        // Arrange
        AssetRequest request = new AssetRequest();
        request.setSymbol("");
        request.setQuantity(0.5);

        // Act
        Set<ConstraintViolation<AssetRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
        assertEquals("symbol", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void whenSymbolIsNull_thenViolation() {
        // Arrange
        AssetRequest request = new AssetRequest();
        request.setSymbol(null);
        request.setQuantity(0.5);

        // Act
        Set<ConstraintViolation<AssetRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
        assertEquals("symbol", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void whenQuantityIsNegative_thenViolation() {
        // Arrange
        AssetRequest request = new AssetRequest();
        request.setSymbol("BTC");
        request.setQuantity(-0.5);

        // Act
        Set<ConstraintViolation<AssetRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
        assertEquals("quantity", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void whenQuantityIsZero_thenViolation() {
        // Arrange
        AssetRequest request = new AssetRequest();
        request.setSymbol("BTC");
        request.setQuantity(0.0);

        // Act
        Set<ConstraintViolation<AssetRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
        assertEquals("quantity", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void whenQuantityIsNull_thenViolation() {
        // Arrange
        AssetRequest request = new AssetRequest();
        request.setSymbol("BTC");
        request.setQuantity(null);

        // Act
        Set<ConstraintViolation<AssetRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
        assertEquals("quantity", violations.iterator().next().getPropertyPath().toString());
    }
}
