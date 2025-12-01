package io.github._0xorigin.queryfilterbuilder.filters;

import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;
import io.github._0xorigin.queryfilterbuilder.base.wrappers.FilterErrorWrapper;
import io.github._0xorigin.queryfilterbuilder.base.wrappers.FilterWrapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BooleanFilterTest {

    @Mock
    private FilterErrorWrapper filterErrorWrapper;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private FilterWrapper filterWrapper;

    @InjectMocks
    private BooleanFilter booleanFilter;

    @Test
    void getDataType_returnsBooleanClass() {
        assertThat(booleanFilter.getDataType()).isEqualTo(Boolean.class);
    }

    @Test
    void getSupportedOperators_returnsExpectedOperators() {
        Set<Operator> expectedOperators = Set.of(
            Operator.EQ, Operator.NEQ, Operator.IS_NULL, Operator.IS_NOT_NULL
        );

        assertThat(booleanFilter.getSupportedOperators()).containsExactlyInAnyOrderElementsOf(expectedOperators);
    }

    @Test
    void cast_trueString_returnsTrue() {
        String value = "true";

        Boolean result = booleanFilter.cast(value);

        assertThat(result).isTrue();
    }

    @Test
    void cast_falseString_returnsFalse() {
        String value = "false";

        Boolean result = booleanFilter.cast(value);

        assertThat(result).isFalse();
    }

    @Test
    void cast_nullString_returnsNull() {
        String value = "null";

        Boolean result = booleanFilter.cast(value);

        assertThat(result).isNull();
    }

    @Test
    void cast_invalidString_returnsFalse() {
        String value = "abc";

        Boolean result = booleanFilter.cast(value);

        assertThat(result).isFalse();
    }

    @Test
    void cast_nullValue_throwsNullPointerException() {
        assertThatThrownBy(() -> booleanFilter.cast(null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void safeCast_trueString_returnsTrue() {
        String value = "true";

        Boolean result = booleanFilter.safeCast(value, filterErrorWrapper);

        assertThat(result).isTrue();
        verifyNoInteractions(bindingResult);
    }

    @Test
    void safeCast_falseString_returnsFalse() {
        String value = "false";

        Boolean result = booleanFilter.safeCast(value, filterErrorWrapper);

        assertThat(result).isFalse();
        verifyNoInteractions(bindingResult);
    }

    @Test
    void safeCast_nullString_returnsNull() {
        String value = "null";

        Boolean result = booleanFilter.safeCast(value, filterErrorWrapper);

        assertThat(result).isNull();
        verifyNoInteractions(bindingResult);
    }

    @Test
    void safeCast_invalidString_returnsFalse() {
        String value = "abc";

        Boolean result = booleanFilter.safeCast(value, filterErrorWrapper);

        assertThat(result).isFalse();
        verifyNoInteractions(bindingResult);
    }

    @Test
    void safeCast_nullValue_addsErrorAndReturnsNull() {
        when(filterErrorWrapper.bindingResult()).thenReturn(bindingResult);
        when(bindingResult.getObjectName()).thenReturn("objectName");
        when(filterErrorWrapper.filterWrapper()).thenReturn(filterWrapper);
        when(filterWrapper.originalFieldName()).thenReturn("fieldName");

        Boolean result = booleanFilter.safeCast(null, filterErrorWrapper);

        assertThat(result).isNull();
        verify(bindingResult).addError(any());
    }
}
