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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IsNotNullTest {

    @Mock
    private Path<?> path;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private Predicate predicate;

    @Mock
    private ErrorWrapper errorWrapper;

    private IsNotNull isNotNull;

    @BeforeEach
    void setUp() {
        isNotNull = new IsNotNull();
    }

    @Test
    void whenValuesContainNull_shouldReturnConjunction() {
        // Arrange
        List<Object> values = Arrays.asList(null, true);
        when(criteriaBuilder.conjunction()).thenReturn(predicate);

        // Act
        Predicate result = isNotNull.apply(path, criteriaBuilder, values, errorWrapper);

        // Assert
        assertEquals(predicate, result);
        verify(criteriaBuilder).conjunction();
        verifyNoMoreInteractions(path);
    }

    @Test
    void whenValuesIsEmpty_shouldReturnConjunction() {
        // Arrange
        List<Object> values = Collections.emptyList();
        when(criteriaBuilder.conjunction()).thenReturn(predicate);

        // Act
        Predicate result = isNotNull.apply(path, criteriaBuilder, values, errorWrapper);

        // Assert
        assertEquals(predicate, result);
        verify(criteriaBuilder).conjunction();
        verifyNoMoreInteractions(path);
    }

    @Test
    void whenValueIsFalse_shouldReturnIsNull() {
        // Arrange
        List<Object> values = Collections.singletonList(false);
        when(criteriaBuilder.isNull(path)).thenReturn(predicate);

        // Act
        Predicate result = isNotNull.apply(path, criteriaBuilder, values, errorWrapper);

        // Assert
        assertEquals(predicate, result);
        verify(criteriaBuilder).isNull(path);
    }

    @Test
    void whenValueIsTrue_shouldReturnIsNotNull() {
        // Arrange
        List<Object> values = Collections.singletonList(true);
        when(criteriaBuilder.isNotNull(path)).thenReturn(predicate);

        // Act
        Predicate result = isNotNull.apply(path, criteriaBuilder, values, errorWrapper);

        // Assert
        assertEquals(predicate, result);
        verify(criteriaBuilder).isNotNull(path);
    }

}
