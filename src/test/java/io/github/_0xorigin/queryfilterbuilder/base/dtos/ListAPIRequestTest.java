package io.github._0xorigin.queryfilterbuilder.base.dtos;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ListAPIRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testValidListAPIRequestWithFiltersAndSorts() {
        // Arrange
        FilterRequest filterRequest = new FilterRequest("firstName", "eq", "John");
        SortRequest sortRequest = new SortRequest("firstName", Sort.Direction.ASC);
        ListAPIRequest request = new ListAPIRequest(List.of(filterRequest), List.of(sortRequest));

        // Act
        Set<ConstraintViolation<ListAPIRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isEmpty();
        assertThat(request.filters()).hasSize(1);
        assertThat(request.sorts()).hasSize(1);
        assertThat(request.filters().get(0)).isEqualTo(filterRequest);
        assertThat(request.sorts().get(0)).isEqualTo(sortRequest);
    }

    @Test
    void testValidListAPIRequestWithOnlyFilters() {
        // Arrange
        FilterRequest filterRequest = new FilterRequest("firstName", "eq", "John");
        ListAPIRequest request = new ListAPIRequest(List.of(filterRequest), null);

        // Act
        Set<ConstraintViolation<ListAPIRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isEmpty();
        assertThat(request.filters()).hasSize(1);
        assertThat(request.sorts()).isNull();
    }

    @Test
    void testValidListAPIRequestWithOnlySorts() {
        // Arrange
        SortRequest sortRequest = new SortRequest("firstName", Sort.Direction.ASC);
        ListAPIRequest request = new ListAPIRequest(null, List.of(sortRequest));

        // Act
        Set<ConstraintViolation<ListAPIRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isEmpty();
        assertThat(request.filters()).isNull();
        assertThat(request.sorts()).hasSize(1);
    }

    @Test
    void testValidListAPIRequestWithEmptyLists() {
        // Arrange
        ListAPIRequest request = new ListAPIRequest(List.of(), List.of());

        // Act
        Set<ConstraintViolation<ListAPIRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isEmpty();
        assertThat(request.filters()).isEmpty();
        assertThat(request.sorts()).isEmpty();
    }

    @Test
    void testValidListAPIRequestWithNullLists() {
        // Arrange
        ListAPIRequest request = new ListAPIRequest(null, null);

        // Act
        Set<ConstraintViolation<ListAPIRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isEmpty();
        assertThat(request.filters()).isNull();
        assertThat(request.sorts()).isNull();
    }

    @Test
    void testListAPIRequestWithInvalidFilter() {
        // Arrange
        FilterRequest invalidFilterRequest = new FilterRequest("", "eq", "John");
        ListAPIRequest request = new ListAPIRequest(List.of(invalidFilterRequest), null);

        // Act
        Set<ConstraintViolation<ListAPIRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isNotEmpty().hasSize(1);
        ConstraintViolation<ListAPIRequest> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath()).hasToString("filters[0].field");
    }

    @Test
    void testListAPIRequestWithInvalidSort() {
        // Arrange
        SortRequest invalidSortRequest = new SortRequest("", Sort.Direction.ASC);
        ListAPIRequest request = new ListAPIRequest(null, List.of(invalidSortRequest));

        // Act
        Set<ConstraintViolation<ListAPIRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isNotEmpty().hasSize(1);
        ConstraintViolation<ListAPIRequest> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath()).hasToString("sorts[0].field");
    }

    @Test
    void testListAPIRequestWithBothInvalid() {
        // Arrange
        FilterRequest invalidFilterRequest = new FilterRequest("", "eq", "John");
        SortRequest invalidSortRequest = new SortRequest("", Sort.Direction.ASC);
        ListAPIRequest request = new ListAPIRequest(List.of(invalidFilterRequest), List.of(invalidSortRequest));

        // Act
        Set<ConstraintViolation<ListAPIRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isNotEmpty().hasSize(2);
    }

    @Test
    void testListAPIRequestEquality() {
        // Arrange
        FilterRequest filterRequest = new FilterRequest("firstName", "eq", "John");
        SortRequest sortRequest = new SortRequest("firstName", Sort.Direction.ASC);

        ListAPIRequest request1 = new ListAPIRequest(List.of(filterRequest), List.of(sortRequest));
        ListAPIRequest request2 = new ListAPIRequest(List.of(filterRequest), List.of(sortRequest));
        ListAPIRequest request3 = new ListAPIRequest(List.of(filterRequest), null);

        // Assert
        assertThat(request1).isEqualTo(request2).isNotEqualTo(request3).hasSameHashCodeAs(request2);
    }

    @Test
    void testListAPIRequestToString() {
        // Arrange
        FilterRequest filterRequest = new FilterRequest("firstName", "eq", "John");
        SortRequest sortRequest = new SortRequest("firstName", Sort.Direction.ASC);
        ListAPIRequest request = new ListAPIRequest(List.of(filterRequest), List.of(sortRequest));

        // Act
        String toString = request.toString();

        // Assert
        assertThat(toString).contains("firstName").contains("John").contains("ASC");
    }
}
