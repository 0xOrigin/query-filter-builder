//package io.github._0xorigin.queryfilterbuilder.operators;
//
//import io.github._0xorigin.queryfilterbuilder.base.wrapper.ErrorWrapper;
//import io.github._0xorigin.queryfilterbuilder.base.wrapper.FilterWrapper;
//import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;
//import jakarta.persistence.criteria.CriteriaBuilder;
//import jakarta.persistence.criteria.Expression;
//import jakarta.persistence.criteria.Path;
//import jakarta.persistence.criteria.Predicate;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.mockito.junit.jupiter.MockitoSettings;
//import org.mockito.quality.Strictness;
//import org.springframework.validation.BeanPropertyBindingResult;
//import org.springframework.validation.BindingResult;
//import org.springframework.validation.FieldError;
//
//import java.sql.Time;
//import java.sql.Timestamp;
//import java.time.*;
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//@MockitoSettings(strictness = Strictness.LENIENT)
//class NotBetweenTest {
//
//    @Mock
//    private Path<?> path;
//
//    @Mock
//    private CriteriaBuilder cb;
//
//    @Mock
//    private Predicate predicate;
//
//    private NotBetween notBetween;
//    private BindingResult bindingResult;
//    private ErrorWrapper errorWrapper;
//
//    @BeforeEach
//    void setUp() {
//        notBetween = new NotBetween();
//        bindingResult = new BeanPropertyBindingResult(path, "path");
//        errorWrapper = new ErrorWrapper(bindingResult, null);
//    }
//
//    @Test
//    void apply_WithNullValues_ReturnsPredicate() {
//        List<Object> values = Arrays.asList(null, null);
//        when(cb.conjunction()).thenReturn(predicate);
//
//        Predicate result = notBetween.apply(path, cb, values, errorWrapper);
//
//        assertNotNull(result);
//        verify(cb).conjunction();
//        assertFalse(errorWrapper.getBindingResult().hasErrors());
//    }
//
//    @Test
//    void apply_WithEmptyList_ReturnsPredicate() {
//        List<Object> values = Collections.emptyList();
//        when(cb.conjunction()).thenReturn(predicate);
//
//        Predicate result = notBetween.apply(path, cb, values, errorWrapper);
//
//        assertNotNull(result);
//        verify(cb).conjunction();
//        assertFalse(errorWrapper.getBindingResult().hasErrors());
//    }
//
//    @Test
//    void apply_WithIncorrectNumberOfValues_ReturnsNull() {
//        List<Object> values = Arrays.asList(1, 2, 3);
//
//        errorWrapper = new ErrorWrapper(bindingResult, new FilterWrapper("", "", Operator.NOT_BETWEEN, values));
//        Predicate result = notBetween.apply(path, cb, values, errorWrapper);
//
//        assertNull(result);
//        assertTrue(errorWrapper.getBindingResult().hasErrors());
//        FieldError error = errorWrapper.getBindingResult().getFieldErrors().get(0);
//        assertTrue(Objects.requireNonNull(error.getDefaultMessage()).contains("exactly 2 elements"));
//    }
//
//    @Test
//    void apply_WithNumericValues_ReturnsPredicate() {
//        List<Integer> values = Arrays.asList(1, 10);
//        when(path.getJavaType()).thenReturn((Class) Integer.class);
//        when(path.as(Comparable.class)).thenReturn((Expression<Comparable>) path);
//        when(cb.between(any(), any(Comparable.class), any(Comparable.class))).thenReturn(predicate);
//        when(cb.not(any(Predicate.class))).thenReturn(predicate);
//
//        Predicate result = notBetween.apply(path, cb, values, errorWrapper);
//
//        assertNotNull(result);
//        verify(cb).between((Expression<Integer>) path, 1, 10);
//        verify(cb).not(predicate);
//        assertFalse(errorWrapper.getBindingResult().hasErrors());
//    }
//
//    @Test
//    void apply_WithLocalDateValues_ReturnsPredicate() {
//        LocalDate start = LocalDate.of(2024, 1, 1);
//        LocalDate end = LocalDate.of(2024, 12, 31);
//        List<LocalDate> values = Arrays.asList(start, end);
//
//        when(path.getJavaType()).thenReturn((Class) LocalDate.class);
//        when(cb.between(any(), any(Date.class), any(Date.class))).thenReturn(predicate);
//        when(cb.not(any(Predicate.class))).thenReturn(predicate);
//
//        Predicate result = notBetween.apply(path, cb, values, errorWrapper);
//
//        assertNotNull(result);
//        verify(cb).between(
//                any(),
//                eq(Date.from(start.atStartOfDay(ZoneId.systemDefault()).toInstant())),
//                eq(Date.from(end.atStartOfDay(ZoneId.systemDefault()).toInstant()))
//        );
//        verify(cb).not(predicate);
//        assertFalse(errorWrapper.getBindingResult().hasErrors());
//    }
//
//    @Test
//    void apply_WithLocalDateTimeValues_ReturnsPredicate() {
//        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0, 1);
//        LocalDateTime end = LocalDateTime.of(2024, 12, 31, 23, 59, 59);
//        List<LocalDateTime> values = Arrays.asList(start, end);
//
//        when(path.getJavaType()).thenReturn((Class) LocalDateTime.class);
//        when(cb.between(any(), any(Timestamp.class), any(Timestamp.class))).thenReturn(predicate);
//        when(cb.not(any(Predicate.class))).thenReturn(predicate);
//
//        errorWrapper = new ErrorWrapper(bindingResult, new FilterWrapper("", "", Operator.NOT_BETWEEN, values));
//        Predicate result = notBetween.apply(path, cb, values, errorWrapper);
//
//        assertNotNull(result);
//        verify(cb).between(
//                any(),
//                eq(Timestamp.from(start.atZone(ZoneId.systemDefault()).toInstant())),
//                eq(Timestamp.from(end.atZone(ZoneId.systemDefault()).toInstant()))
//        );
//        verify(cb).not(predicate);
//        assertFalse(errorWrapper.getBindingResult().hasErrors());
//    }
//
//    @Test
//    void apply_WithLocalTimeValues_ReturnsPredicate() {
//        LocalTime start = LocalTime.of(9, 0, 1);
//        LocalTime end = LocalTime.of(17, 0, 1);
//        List<LocalTime> values = Arrays.asList(start, end);
//
//        when(path.getJavaType()).thenReturn((Class) LocalTime.class);
//        when(cb.between(any(), any(Time.class), any(Time.class))).thenReturn(predicate);
//        when(cb.not(any(Predicate.class))).thenReturn(predicate);
//
//        errorWrapper = new ErrorWrapper(bindingResult, new FilterWrapper("", "", Operator.NOT_BETWEEN, values));
//        Predicate result = notBetween.apply(path, cb, values, errorWrapper);
//
//        assertNotNull(result);
//        verify(cb).between(any(), eq(Time.valueOf(start)), eq(Time.valueOf(end)));
//        verify(cb).not(predicate);
//        assertFalse(errorWrapper.getBindingResult().hasErrors());
//    }
//
//    @Test
//    void apply_WithTimestampValues_ReturnsPredicate() {
//        Timestamp start = Timestamp.valueOf("2024-01-01 00:00:00");
//        Timestamp end = Timestamp.valueOf("2024-12-31 23:59:59");
//        List<Timestamp> values = Arrays.asList(start, end);
//
//        when(path.getJavaType()).thenReturn((Class) Timestamp.class);
//        when(cb.between(any(), any(Timestamp.class), any(Timestamp.class))).thenReturn(predicate);
//        when(cb.not(any(Predicate.class))).thenReturn(predicate);
//
//        Predicate result = notBetween.apply(path, cb, values, errorWrapper);
//
//        assertNotNull(result);
//        verify(cb).between(any(), eq(start), eq(end));
//        verify(cb).not(predicate);
//        assertFalse(errorWrapper.getBindingResult().hasErrors());
//    }
//
//    @Test
//    void apply_WithStringValues_ReturnsPredicate() {
//        List<String> values = Arrays.asList("A", "Z");
//        when(path.getJavaType()).thenReturn((Class) String.class);
//        when(path.as(Comparable.class)).thenReturn((Expression<Comparable>) path);
//        when(cb.between(any(), any(Comparable.class), any(Comparable.class))).thenReturn(predicate);
//        when(cb.not(any(Predicate.class))).thenReturn(predicate);
//
//        Predicate result = notBetween.apply(path, cb, values, errorWrapper);
//
//        assertNotNull(result);
//        verify(cb).between((Expression<String>) path, "A", "Z");
//        verify(cb).not(predicate);
//        assertFalse(errorWrapper.getBindingResult().hasErrors());
//    }
//
//    @Test
//    void apply_WithInvalidDateFormat_ReturnsNull() {
//        List<String> values = Arrays.asList("invalid-date", "2024-12-31");
//        when(path.getJavaType()).thenReturn((Class) LocalDate.class);
//
//        errorWrapper = new ErrorWrapper(bindingResult, new FilterWrapper("", "", Operator.NOT_BETWEEN, values));
//        Predicate result = notBetween.apply(path, cb, values, errorWrapper);
//
//        assertNull(result);
//        assertTrue(errorWrapper.getBindingResult().hasErrors());
//    }
//
//}
