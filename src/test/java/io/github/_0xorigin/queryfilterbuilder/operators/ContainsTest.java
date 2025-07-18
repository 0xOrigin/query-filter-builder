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
//import org.springframework.validation.BindingResult;
//
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class ContainsTest {
//
//    @Mock
//    private Path<?> path;
//
//    @Mock
//    private CriteriaBuilder criteriaBuilder;
//
//    @Mock
//    private BindingResult bindingResult;
//
//    private Contains containsOperator;
//    private ErrorWrapper errorWrapper;
//
//    @BeforeEach
//    void setUp() {
//        containsOperator = new Contains();
//        errorWrapper = new ErrorWrapper(bindingResult, null);
//    }
//
//    @Test
//    void apply_WithValidValue_ReturnsLikePredicate() {
//        // Arrange
//        String searchValue = "test";
//        List<String> values = Collections.singletonList(searchValue);
//        Path<String> stringPath = mock(Path.class);
//        Predicate expectedPredicate = mock(Predicate.class);
//
//        when(path.as(String.class)).thenReturn(stringPath);
//        when(criteriaBuilder.like(eq(stringPath), eq("%" + searchValue + "%")))
//                .thenReturn(expectedPredicate);
//
//        // Act
//        Predicate result = containsOperator.apply(path, criteriaBuilder, values, errorWrapper);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(expectedPredicate, result);
//        verify(path).as(String.class);
//        verify(criteriaBuilder).like(eq(stringPath), eq("%" + searchValue + "%"));
//    }
//
//    @Test
//    void apply_WithNullValue_ReturnsConjunction() {
//        // Arrange
//        List<String> values = Collections.singletonList(null);
//        Predicate expectedPredicate = mock(Predicate.class);
//
//        when(criteriaBuilder.conjunction()).thenReturn(expectedPredicate);
//
//        // Act
//        Predicate result = containsOperator.apply(path, criteriaBuilder, values, errorWrapper);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(expectedPredicate, result);
//        verify(criteriaBuilder).conjunction();
//        verify(path, never()).as(any());
//    }
//
//    @Test
//    void apply_WithEmptyList_ReturnsConjunction() {
//        // Arrange
//        List<String> values = Collections.emptyList();
//        Predicate expectedPredicate = mock(Predicate.class);
//
//        when(criteriaBuilder.conjunction()).thenReturn(expectedPredicate);
//
//        // Act
//        Predicate result = containsOperator.apply(path, criteriaBuilder, values, errorWrapper);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(expectedPredicate, result);
//        verify(criteriaBuilder).conjunction();
//        verify(path, never()).as(any());
//    }
//
//    @Test
//    void apply_WithSpecialCharacters_ReturnsLikePredicateWithEscapedCharacters() {
//        // Arrange
//        String searchValue = "test%_";
//        List<String> values = Collections.singletonList(searchValue);
//        Path<String> stringPath = mock(Path.class);
//        Predicate expectedPredicate = mock(Predicate.class);
//
//        when(path.as(String.class)).thenReturn(stringPath);
//        when(criteriaBuilder.like(eq(stringPath), eq("%" + searchValue + "%")))
//                .thenReturn(expectedPredicate);
//
//        // Act
//        Predicate result = containsOperator.apply(path, criteriaBuilder, values, errorWrapper);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(expectedPredicate, result);
//        verify(path).as(String.class);
//        verify(criteriaBuilder).like(eq(stringPath), eq("%" + searchValue + "%"));
//    }
//
//    @Test
//    void apply_WithMultipleValues_UsesFirstValueOnly() {
//        // Arrange
//        String firstValue = "test1";
//        List<String> values = Arrays.asList(firstValue, "test2", "test3");
//        Path<String> stringPath = mock(Path.class);
//        Predicate expectedPredicate = mock(Predicate.class);
//
//        when(path.as(String.class)).thenReturn(stringPath);
//        when(criteriaBuilder.like(eq(stringPath), eq("%" + firstValue + "%")))
//                .thenReturn(expectedPredicate);
//
//        // Act
//        Predicate result = containsOperator.apply(path, criteriaBuilder, values, errorWrapper);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(expectedPredicate, result);
//        verify(path).as(String.class);
//        verify(criteriaBuilder).like(eq(stringPath), eq("%" + firstValue + "%"));
//    }
//
//}
