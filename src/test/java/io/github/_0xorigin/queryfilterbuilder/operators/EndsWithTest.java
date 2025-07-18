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
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class EndsWithTest {
//
//    @Mock
//    private Path<?> path;
//
//    @Mock
//    private Path<String> stringPath;
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
//    private EndsWith endsWithOperator;
//
//    @BeforeEach
//    void setUp() {
//        endsWithOperator = new EndsWith();
//    }
//
//    @Test
//    void apply_WithValidSingleValue_ShouldCreateLikePredicate() {
//        // Arrange
//        String testValue = "test";
//        List<String> values = Collections.singletonList(testValue);
//        when(path.as(String.class)).thenReturn(stringPath);
//        when(criteriaBuilder.like(stringPath, "%" + testValue)).thenReturn(predicate);
//
//        // Act
//        endsWithOperator.apply(path, criteriaBuilder, values, errorWrapper);
//
//        // Assert
//        verify(path).as(String.class);
//        verify(criteriaBuilder).like(stringPath, "%" + testValue);
//    }
//
//    @Test
//    void apply_WithEmptyList_ShouldReturnConjunction() {
//        // Arrange
//        List<?> emptyList = Collections.emptyList();
//        when(criteriaBuilder.conjunction()).thenReturn(predicate);
//
//        // Act
//        endsWithOperator.apply(path, criteriaBuilder, emptyList, errorWrapper);
//
//        // Assert
//        verify(criteriaBuilder).conjunction();
//        verify(path, never()).as(String.class);
//    }
//
//    @Test
//    void apply_WithNullValue_ShouldReturnConjunction() {
//        // Arrange
//        List<?> listWithNull = Collections.singletonList(null);
//        when(criteriaBuilder.conjunction()).thenReturn(predicate);
//
//        // Act
//        endsWithOperator.apply(path, criteriaBuilder, listWithNull, errorWrapper);
//
//        // Assert
//        verify(criteriaBuilder).conjunction();
//        verify(path, never()).as(String.class);
//    }
//
//    @Test
//    void apply_WithMultipleValues_ShouldUseFirstValue() {
//        // Arrange
//        String firstValue = "first";
//        List<String> values = Arrays.asList(firstValue, "second", "third");
//        when(path.as(String.class)).thenReturn(stringPath);
//        when(criteriaBuilder.like(stringPath, "%" + firstValue)).thenReturn(predicate);
//
//        // Act
//        endsWithOperator.apply(path, criteriaBuilder, values, errorWrapper);
//
//        // Assert
//        verify(path).as(String.class);
//        verify(criteriaBuilder).like(stringPath, "%" + firstValue);
//    }
//
//    @Test
//    void apply_WithSpecialCharacters_ShouldCreateLikePredicateWithSpecialCharacters() {
//        // Arrange
//        String testValue = "test%_";  // Contains special SQL LIKE characters
//        List<String> values = Collections.singletonList(testValue);
//        when(path.as(String.class)).thenReturn(stringPath);
//        when(criteriaBuilder.like(stringPath, "%" + testValue)).thenReturn(predicate);
//
//        // Act
//        endsWithOperator.apply(path, criteriaBuilder, values, errorWrapper);
//
//        // Assert
//        verify(path).as(String.class);
//        verify(criteriaBuilder).like(stringPath, "%" + testValue);
//    }
//
//}
