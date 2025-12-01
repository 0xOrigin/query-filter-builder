package io.github._0xorigin.queryfilterbuilder.base.dtos;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.data.domain.Sort;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SortRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testValidSortRequestWithASC() {
        // Arrange
        SortRequest sortRequest = new SortRequest("firstName", Sort.Direction.ASC);

        // Act
        Set<ConstraintViolation<SortRequest>> violations = validator.validate(sortRequest);

        // Assert
        assertThat(violations).isEmpty();
        assertThat(sortRequest.field()).isEqualTo("firstName");
        assertThat(sortRequest.direction()).isEqualTo(Sort.Direction.ASC);
    }

    @Test
    void testValidSortRequestWithDESC() {
        // Arrange
        SortRequest sortRequest = new SortRequest("firstName", Sort.Direction.DESC);

        // Act
        Set<ConstraintViolation<SortRequest>> violations = validator.validate(sortRequest);

        // Assert
        assertThat(violations).isEmpty();
        assertThat(sortRequest.field()).isEqualTo("firstName");
        assertThat(sortRequest.direction()).isEqualTo(Sort.Direction.DESC);
    }

    @Test
    void testSortRequestWithNullDirection() {
        // Arrange
        SortRequest sortRequest = new SortRequest("firstName", null);

        // Act
        Set<ConstraintViolation<SortRequest>> violations = validator.validate(sortRequest);

        // Assert
        assertThat(violations).isEmpty();
        assertThat(sortRequest.field()).isEqualTo("firstName");
        // The compact constructor should set the direction to ASC if null
        assertThat(sortRequest.direction()).isEqualTo(Sort.Direction.ASC);
    }

    @Test
    void testSortRequestWithBlankField() {
        // Arrange
        SortRequest sortRequest = new SortRequest("", Sort.Direction.ASC);

        // Act
        Set<ConstraintViolation<SortRequest>> violations = validator.validate(sortRequest);

        // Assert
        assertThat(violations).isNotEmpty().hasSize(1);
        ConstraintViolation<SortRequest> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath()).hasToString("field");
        assertThat(violation.getMessage()).contains("must not be blank");
    }

    @Test
    void testSortRequestWithNullField() {
        // Arrange
        SortRequest sortRequest = new SortRequest(null, Sort.Direction.ASC);

        // Act
        Set<ConstraintViolation<SortRequest>> violations = validator.validate(sortRequest);

        // Assert
        assertThat(violations).isNotEmpty().hasSize(1);
        ConstraintViolation<SortRequest> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath()).hasToString("field");
    }

    @Test
    void testSortRequestEquality() {
        // Arrange
        SortRequest request1 = new SortRequest("firstName", Sort.Direction.ASC);
        SortRequest request2 = new SortRequest("firstName", Sort.Direction.ASC);
        SortRequest request3 = new SortRequest("lastName", Sort.Direction.ASC);
        SortRequest request4 = new SortRequest("firstName", Sort.Direction.DESC);

        // Assert
        assertThat(request1).isEqualTo(request2).isNotEqualTo(request3).isNotEqualTo(request4).hasSameHashCodeAs(request2);
    }

    @Test
    void testSortRequestToString() {
        // Arrange
        SortRequest sortRequest = new SortRequest("firstName", Sort.Direction.ASC);

        // Act
        String toString = sortRequest.toString();

        // Assert
        assertThat(toString).contains("firstName").contains("ASC");
    }

    @Test
    void testSortRequestCompactConstructorWithNullDirection() {
        // This test verifies that the compact constructor handles null direction correctly
        // Arrange & Act
        SortRequest sortRequest = new SortRequest("firstName", null);

        // Assert
        assertThat(sortRequest.direction()).isEqualTo(Sort.Direction.ASC);
    }

    @ParameterizedTest
    @ValueSource(strings = {"asc", "ASC"})
    void testSortRequestJsonCreator(String direction) {
        // Arrange
        SortRequest sortRequest = SortRequest.fromJson("firstName", direction);

        // Act
        Set<ConstraintViolation<SortRequest>> violations = validator.validate(sortRequest);

        // Assert
        assertThat(violations).isEmpty();
        assertThat(sortRequest.field()).isEqualTo("firstName");
        assertThat(sortRequest.direction()).isEqualTo(Sort.Direction.ASC);
    }

    @Test
    void testSortRequestJsonCreatorWithInvalidDirection() {
        // Arrange
        SortRequest sortRequest = SortRequest.fromJson("firstName", "INVALID");

        // Act
        Set<ConstraintViolation<SortRequest>> violations = validator.validate(sortRequest);

        // Assert
        assertThat(violations).isEmpty();
        assertThat(sortRequest.field()).isEqualTo("firstName");
        assertThat(sortRequest.direction()).isEqualTo(Sort.Direction.ASC);
    }

    @Test
    void testSortRequestObjectMapper() {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        String json = "{\"field\":\"name\",\"direction\":\"aSc\"}";
        SortRequest req = null;

        // Act
        try {
            req = mapper.readValue(json, SortRequest.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // Assert
        assertThat(req.field()).isEqualTo("name");
        assertThat(req.direction()).isEqualTo(Sort.Direction.ASC);
    }
}
