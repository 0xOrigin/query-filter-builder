//package io.github._0xorigin.queryfilterbuilder.operators;
//
//import io.github._0xorigin.queryfilterbuilder.base.wrapper.ErrorWrapper;
//import jakarta.persistence.criteria.CriteriaBuilder;
//import jakarta.persistence.criteria.Path;
//import jakarta.persistence.criteria.Predicate;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class NotEqualsTest {
//
//    @Mock
//    private Path<?> path;
//
//    @Mock
//    private CriteriaBuilder criteriaBuilder;
//
//    @Mock
//    private ErrorWrapper errorWrapper;
//
//    @Mock
//    private Predicate predicate;
//
//    private NotEquals notEqualsOperator;
//
//    @BeforeEach
//    void setUp() {
//        notEqualsOperator = new NotEquals();
//    }
//
//    @Test
//    void apply_WithValidSingleValue_ShouldCreateEqualPredicate() {
//        // Arrange
//        String testValue = "test";
//        List<String> values = Collections.singletonList(testValue);
//        when(criteriaBuilder.notEqual(path, testValue)).thenReturn(predicate);
//
//        // Act
//        notEqualsOperator.apply(path, criteriaBuilder, values, errorWrapper);
//
//        // Assert
//        verify(criteriaBuilder).notEqual(path, testValue);
//    }
//
//    @Test
//    void apply_WithEmptyList_ShouldReturnConjunction() {
//        // Arrange
//        List<?> emptyList = Collections.emptyList();
//        when(criteriaBuilder.conjunction()).thenReturn(predicate);
//
//        // Act
//        notEqualsOperator.apply(path, criteriaBuilder, emptyList, errorWrapper);
//
//        // Assert
//        verify(criteriaBuilder).conjunction();
//    }
//
//    @Test
//    void apply_WithNullValue_ShouldReturnConjunction() {
//        // Arrange
//        List<?> listWithNull = Collections.singletonList(null);
//        when(criteriaBuilder.conjunction()).thenReturn(predicate);
//
//        // Act
//        notEqualsOperator.apply(path, criteriaBuilder, listWithNull, errorWrapper);
//
//        // Assert
//        verify(criteriaBuilder).conjunction();
//    }
//
//    @Test
//    void apply_WithMultipleValues_ShouldUseFirstValue() {
//        // Arrange
//        String firstValue = "first";
//        List<String> values = Arrays.asList(firstValue, "second", "third");
//        when(criteriaBuilder.notEqual(path, firstValue)).thenReturn(predicate);
//
//        // Act
//        notEqualsOperator.apply(path, criteriaBuilder, values, errorWrapper);
//
//        // Assert
//        verify(criteriaBuilder).notEqual(path, firstValue);
//    }
//
//}
