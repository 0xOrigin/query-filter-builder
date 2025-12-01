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
class LongFilterTest {

    @Mock
    private FilterErrorWrapper filterErrorWrapper;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private FilterWrapper filterWrapper;

    @InjectMocks
    private LongFilter longFilter;

    @Test
    void getDataType_returnsLongClass() {
        assertThat(longFilter.getDataType()).isEqualTo(Long.class);
    }

    @Test
    void getSupportedOperators_returnsExpectedOperators() {
        Set<Operator> expectedOperators = Set.of(
            Operator.EQ, Operator.NEQ, Operator.GT, Operator.LT, Operator.GTE, Operator.LTE,
            Operator.IS_NULL, Operator.IS_NOT_NULL, Operator.IN, Operator.NOT_IN, Operator.BETWEEN, Operator.NOT_BETWEEN
        );

        assertThat(longFilter.getSupportedOperators()).containsExactlyInAnyOrderElementsOf(expectedOperators);
    }

    @Test
    void cast_validString_returnsLong() {
        String value = "1234567890";

        Long result = longFilter.cast(value);

        assertThat(result).isEqualTo(1234567890L);
    }

    @Test
    void cast_invalidString_throwsNumberFormatException() {
        String value = "invalid";

        assertThatThrownBy(() -> longFilter.cast(value))
            .isInstanceOf(NumberFormatException.class);
    }

    @Test
    void safeCast_validString_returnsLong() {
        String value = "1234567890";

        Long result = longFilter.safeCast(value, filterErrorWrapper);

        assertThat(result).isEqualTo(1234567890L);
        verifyNoInteractions(bindingResult);
    }

    @Test
    void safeCast_invalidString_addsErrorAndReturnsNull() {
        String value = "invalid";
        when(filterErrorWrapper.bindingResult()).thenReturn(bindingResult);
        when(bindingResult.getObjectName()).thenReturn("objectName");
        when(filterErrorWrapper.filterWrapper()).thenReturn(filterWrapper);
        when(filterWrapper.originalFieldName()).thenReturn("fieldName");

        Long result = longFilter.safeCast(value, filterErrorWrapper);

        assertThat(result).isNull();
        verify(bindingResult).addError(any());
    }
}
