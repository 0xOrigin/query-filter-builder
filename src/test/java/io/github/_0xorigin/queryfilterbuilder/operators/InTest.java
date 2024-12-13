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
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InTest {

    @Mock
    private Path<?> path;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private Predicate predicate;

    private In in;
    private BindingResult bindingResult;
    private ErrorWrapper errorWrapper;

    @BeforeEach
    void setUp() {
        in = new In();
        bindingResult = new BeanPropertyBindingResult(path, "path");
        errorWrapper = new ErrorWrapper(bindingResult, null);
    }

    @Test
    void apply_WithNullValues_ReturnsPredicate() {
        List<Object> values = Collections.singletonList(null);
        when(cb.conjunction()).thenReturn(predicate);

        Predicate result = in.apply(path, cb, values, errorWrapper);

        assertNotNull(result);
        verify(cb).conjunction();
        assertFalse(errorWrapper.getBindingResult().hasErrors());
    }

    @Test
    void apply_WithEmptyList_ReturnsPredicate() {
        List<Object> values = Collections.emptyList();
        when(cb.conjunction()).thenReturn(predicate);

        Predicate result = in.apply(path, cb, values, errorWrapper);

        assertNotNull(result);
        verify(cb).conjunction();
        assertFalse(errorWrapper.getBindingResult().hasErrors());
    }

    @Test
    void apply_WithNumericValues_ReturnsPredicate() {
        List<Integer> values = Arrays.asList(1, 2, 3);
        when(path.in(values)).thenReturn(predicate);

        Predicate result = in.apply(path, cb, values, errorWrapper);

        assertNotNull(result);
        verify(path).in(values);
        assertFalse(errorWrapper.getBindingResult().hasErrors());
    }

    @Test
    void apply_WithStringValues_ReturnsPredicate() {
        List<String> values = Arrays.asList("A", "B", "C");
        when(path.in(values)).thenReturn(predicate);

        Predicate result = in.apply(path, cb, values, errorWrapper);

        assertNotNull(result);
        verify(path).in(values);
        assertFalse(errorWrapper.getBindingResult().hasErrors());
    }

    @Test
    void apply_WithSingleValue_ReturnsPredicate() {
        List<String> values = Collections.singletonList("test");
        when(path.in(values)).thenReturn(predicate);

        Predicate result = in.apply(path, cb, values, errorWrapper);

        assertNotNull(result);
        verify(path).in(values);
        assertFalse(errorWrapper.getBindingResult().hasErrors());
    }

    @Test
    void apply_WithMixedTypes_ReturnsPredicate() {
        List<Object> values = Arrays.asList("A", 1, true, Instant.now());
        when(path.in(values)).thenReturn(predicate);

        Predicate result = in.apply(path, cb, values, errorWrapper);

        assertNotNull(result);
        verify(path).in(values);
        assertFalse(errorWrapper.getBindingResult().hasErrors());
    }

}
