package io.github._0xorigin.queryfilterbuilder.base.dtos;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class FilterRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testValidFilterRequest() {
        // Arrange
        FilterRequest filterRequest = new FilterRequest("firstName", "eq", "John");

        // Act
        Set<ConstraintViolation<FilterRequest>> violations = validator.validate(filterRequest);

        // Assert
        assertThat(violations).isEmpty();
        assertThat(filterRequest.field()).isEqualTo("firstName");
        assertThat(filterRequest.operator()).isEqualTo("eq");
        assertThat(filterRequest.value()).isEqualTo("John");
    }

    @Test
    void testFilterRequestWithNullOperator() {
        // Arrange
        FilterRequest filterRequest = new FilterRequest("firstName", null, "John");

        // Act
        Set<ConstraintViolation<FilterRequest>> violations = validator.validate(filterRequest);

        // Assert
        assertThat(violations).isEmpty();
        assertThat(filterRequest.field()).isEqualTo("firstName");
        assertThat(filterRequest.operator()).isNull();
        assertThat(filterRequest.value()).isEqualTo("John");
    }

    @Test
    void testFilterRequestWithBlankField() {
        // Arrange
        FilterRequest filterRequest = new FilterRequest("", "eq", "John");

        // Act
        Set<ConstraintViolation<FilterRequest>> violations = validator.validate(filterRequest);

        // Assert
        assertThat(violations).isNotEmpty().hasSize(1);
        ConstraintViolation<FilterRequest> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath()).hasToString("field");
        assertThat(violation.getMessage()).contains("must not be blank");
    }

    @Test
    void testFilterRequestWithNullField() {
        // Arrange
        FilterRequest filterRequest = new FilterRequest(null, "eq", "John");

        // Act
        Set<ConstraintViolation<FilterRequest>> violations = validator.validate(filterRequest);

        // Assert
        assertThat(violations).isNotEmpty().hasSize(1);
        ConstraintViolation<FilterRequest> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath()).hasToString("field");
    }

    @Test
    void testFilterRequestWithNullValue() {
        // Arrange
        FilterRequest filterRequest = new FilterRequest("firstName", "eq", null);

        // Act
        Set<ConstraintViolation<FilterRequest>> violations = validator.validate(filterRequest);

        // Assert
        assertThat(violations).isNotEmpty().hasSize(1);
        ConstraintViolation<FilterRequest> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath()).hasToString("value");
    }

    @Test
    void testFilterRequestWithEmptyValue() {
        // Arrange
        FilterRequest filterRequest = new FilterRequest("firstName", "eq", "");

        // Act
        Set<ConstraintViolation<FilterRequest>> violations = validator.validate(filterRequest);

        // Assert
        assertThat(violations).isEmpty();
        assertThat(filterRequest.field()).isEqualTo("firstName");
        assertThat(filterRequest.operator()).isEqualTo("eq");
        assertThat(filterRequest.value()).isEmpty();
    }

    @Test
    void testFilterRequestEquality() {
        // Arrange
        FilterRequest request1 = new FilterRequest("firstName", "eq", "John");
        FilterRequest request2 = new FilterRequest("firstName", "eq", "John");
        FilterRequest request3 = new FilterRequest("lastName", "eq", "John");

        // Assert
        assertThat(request1).isEqualTo(request2).isNotEqualTo(request3).hasSameHashCodeAs(request2);
    }

    @Test
    void testFilterRequestToString() {
        // Arrange
        FilterRequest filterRequest = new FilterRequest("firstName", "eq", "John");

        // Act
        String toString = filterRequest.toString();

        // Assert
        assertThat(toString).contains("firstName").contains("eq").contains("John");
    }
}
