package io.github._0xorigin.queryfilterbuilder.operators;

import io.github._0xorigin.queryfilterbuilder.base.ErrorWrapper;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IsNullTest {

    @Mock
    private Path<?> path;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private ErrorWrapper errorWrapper;

    @Mock
    private Predicate predicate;

    private IsNull isNullOperator;

    @BeforeEach
    void setUp() {
        isNullOperator = new IsNull();
    }

    @Test
    void apply_WithTrueValue_ShouldCreateIsNullPredicate() {
        // Arrange
        List<Boolean> values = Collections.singletonList(true);
        when(criteriaBuilder.isNull(path)).thenReturn(predicate);

        // Act
        isNullOperator.apply(path, criteriaBuilder, values, errorWrapper);

        // Assert
        verify(criteriaBuilder).isNull(path);
        verify(criteriaBuilder, never()).isNotNull(any());
    }

    @Test
    void apply_WithFalseValue_ShouldCreateIsNotNullPredicate() {
        // Arrange
        List<Boolean> values = Collections.singletonList(false);
        when(criteriaBuilder.isNotNull(path)).thenReturn(predicate);

        // Act
        isNullOperator.apply(path, criteriaBuilder, values, errorWrapper);

        // Assert
        verify(criteriaBuilder).isNotNull(path);
        verify(criteriaBuilder, never()).isNull(any());
    }

    @Test
    void apply_WithEmptyList_ShouldReturnConjunction() {
        // Arrange
        List<?> emptyList = Collections.emptyList();
        when(criteriaBuilder.conjunction()).thenReturn(predicate);

        // Act
        isNullOperator.apply(path, criteriaBuilder, emptyList, errorWrapper);

        // Assert
        verify(criteriaBuilder).conjunction();
        verify(criteriaBuilder, never()).isNull(any());
        verify(criteriaBuilder, never()).isNotNull(any());
    }

    @Test
    void apply_WithNullValue_ShouldReturnConjunction() {
        // Arrange
        List<?> listWithNull = Collections.singletonList(null);
        when(criteriaBuilder.conjunction()).thenReturn(predicate);

        // Act
        isNullOperator.apply(path, criteriaBuilder, listWithNull, errorWrapper);

        // Assert
        verify(criteriaBuilder).conjunction();
        verify(criteriaBuilder, never()).isNull(any());
        verify(criteriaBuilder, never()).isNotNull(any());
    }

    @Test
    void apply_WithMultipleValues_ShouldUseFirstValue() {
        // Arrange
        List<Boolean> values = Arrays.asList(true, false, true);
        when(criteriaBuilder.isNull(path)).thenReturn(predicate);

        // Act
        isNullOperator.apply(path, criteriaBuilder, values, errorWrapper);

        // Assert
        verify(criteriaBuilder).isNull(path);
        verify(criteriaBuilder, never()).isNotNull(any());
    }

    @Test
    void apply_WithMultipleMixedValues_ShouldUseFirstValue() {
        // Arrange
        List<Boolean> values = Arrays.asList(false, true, false);
        when(criteriaBuilder.isNotNull(path)).thenReturn(predicate);

        // Act
        isNullOperator.apply(path, criteriaBuilder, values, errorWrapper);

        // Assert
        verify(criteriaBuilder).isNotNull(path);
        verify(criteriaBuilder, never()).isNull(any());
    }

}