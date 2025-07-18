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
//import org.springframework.validation.BeanPropertyBindingResult;
//import org.springframework.validation.BindingResult;
//
//import java.sql.Date;
//import java.sql.Time;
//import java.sql.Timestamp;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.time.ZoneId;
//import java.util.Collections;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class GreaterThanOrEqualsTest {
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
//    private GreaterThanOrEqual greaterThanOrEqual;
//    private BindingResult bindingResult;
//    private ErrorWrapper errorWrapper;
//
//    @BeforeEach
//    void setUp() {
//        greaterThanOrEqual = new GreaterThanOrEqual();
//        bindingResult = new BeanPropertyBindingResult(path, "path");
//        errorWrapper = new ErrorWrapper(bindingResult, null);
//    }
//
//    @Test
//    void apply_WithNullValues_ReturnsPredicate() {
//        List<Object> values = Collections.singletonList(null);
//        when(cb.conjunction()).thenReturn(predicate);
//
//        Predicate result = greaterThanOrEqual.apply(path, cb, values, errorWrapper);
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
//        Predicate result = greaterThanOrEqual.apply(path, cb, values, errorWrapper);
//
//        assertNotNull(result);
//        verify(cb).conjunction();
//        assertFalse(errorWrapper.getBindingResult().hasErrors());
//    }
//
//    @Test
//    void apply_WithNumericValue_ReturnsPredicate() {
//        List<Integer> values = Collections.singletonList(10);
//        when(path.getJavaType()).thenReturn((Class) Integer.class);
//        when(path.as(Comparable.class)).thenReturn((Expression<Comparable>) path);
//        when(cb.greaterThanOrEqualTo(any(), any(Comparable.class))).thenReturn(predicate);
//
//        Predicate result = greaterThanOrEqual.apply(path, cb, values, errorWrapper);
//
//        assertNotNull(result);
//        verify(cb).greaterThanOrEqualTo((Expression<Integer>) path, 10);
//        assertFalse(errorWrapper.getBindingResult().hasErrors());
//    }
//
//    @Test
//    void apply_WithLocalDateValue_ReturnsPredicate() {
//        LocalDate date = LocalDate.of(2024, 1, 1);
//        List<LocalDate> values = Collections.singletonList(date);
//
//        when(path.getJavaType()).thenReturn((Class) LocalDate.class);
//        when(cb.greaterThanOrEqualTo(any(), any(Date.class))).thenReturn(predicate);
//
//        Predicate result = greaterThanOrEqual.apply(path, cb, values, errorWrapper);
//
//        assertNotNull(result);
//        verify(cb).greaterThanOrEqualTo(any(), eq(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())));
//        assertFalse(errorWrapper.getBindingResult().hasErrors());
//    }
//
//    @Test
//    void apply_WithLocalDateTimeValue_ReturnsPredicate() {
//        LocalDateTime dateTime = LocalDateTime.of(2024, 1, 1, 12, 0);
//        List<LocalDateTime> values = Collections.singletonList(dateTime);
//
//        when(path.getJavaType()).thenReturn((Class) LocalDateTime.class);
//        when(cb.greaterThanOrEqualTo(any(), any(Timestamp.class))).thenReturn(predicate);
//
//        Predicate result = greaterThanOrEqual.apply(path, cb, values, errorWrapper);
//
//        assertNotNull(result);
//        verify(cb).greaterThanOrEqualTo(any(), eq(Timestamp.from(dateTime.atZone(ZoneId.systemDefault()).toInstant())));
//        assertFalse(errorWrapper.getBindingResult().hasErrors());
//    }
//
//    @Test
//    void apply_WithLocalTimeValue_ReturnsPredicate() {
//        LocalTime time = LocalTime.of(12, 0, 1);
//        List<LocalTime> values = Collections.singletonList(time);
//
//        when(path.getJavaType()).thenReturn((Class) LocalTime.class);
//        when(cb.greaterThanOrEqualTo(any(), any(Time.class))).thenReturn(predicate);
//
//        errorWrapper = new ErrorWrapper(bindingResult, new FilterWrapper("", "", Operator.GTE, values));
//        Predicate result = greaterThanOrEqual.apply(path, cb, values, errorWrapper);
//
//        assertNotNull(result);
//        verify(cb).greaterThanOrEqualTo(any(), eq(Time.valueOf(time)));
//        assertFalse(errorWrapper.getBindingResult().hasErrors());
//    }
//
//    @Test
//    void apply_WithTimestampValue_ReturnsPredicate() {
//        Timestamp timestamp = Timestamp.valueOf("2024-01-01 12:00:00");
//        List<Timestamp> values = Collections.singletonList(timestamp);
//
//        when(path.getJavaType()).thenReturn((Class) Timestamp.class);
//        when(cb.greaterThanOrEqualTo(any(), any(Timestamp.class))).thenReturn(predicate);
//
//        Predicate result = greaterThanOrEqual.apply(path, cb, values, errorWrapper);
//
//        assertNotNull(result);
//        verify(cb).greaterThanOrEqualTo(any(), eq(timestamp));
//        assertFalse(errorWrapper.getBindingResult().hasErrors());
//    }
//
//    @Test
//    void apply_WithStringValue_ReturnsPredicate() {
//        List<String> values = Collections.singletonList("test");
//        when(path.getJavaType()).thenReturn((Class) String.class);
//        when(path.as(Comparable.class)).thenReturn((Expression<Comparable>) path);
//        when(cb.greaterThanOrEqualTo(any(), any(String.class))).thenReturn(predicate);
//
//        Predicate result = greaterThanOrEqual.apply(path, cb, values, errorWrapper);
//
//        assertNotNull(result);
//        verify(cb).greaterThanOrEqualTo((Expression<String>) path, "test");
//        assertFalse(errorWrapper.getBindingResult().hasErrors());
//    }
//
//    @Test
//    void apply_WithInvalidDateFormat_ReturnsNull() {
//        List<String> values = Collections.singletonList("invalid-date");
//        when(path.getJavaType()).thenReturn((Class) LocalDate.class);
//
//        errorWrapper = new ErrorWrapper(bindingResult, new FilterWrapper("", "", Operator.GTE, values));
//        Predicate result = greaterThanOrEqual.apply(path, cb, values, errorWrapper);
//
//        assertNull(result);
//        assertTrue(errorWrapper.getBindingResult().hasErrors());
//    }
//
//}
