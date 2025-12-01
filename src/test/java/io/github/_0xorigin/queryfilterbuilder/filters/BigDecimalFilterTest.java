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

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BigDecimalFilterTest {

    @Mock
    private FilterErrorWrapper filterErrorWrapper;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private FilterWrapper filterWrapper;

    @InjectMocks
    private BigDecimalFilter bigDecimalFilter;

    @Test
    void getDataType_returnsBigDecimalClass() {
        assertThat(bigDecimalFilter.getDataType()).isEqualTo(BigDecimal.class);
    }

    @Test
    void getSupportedOperators_returnsExpectedOperators() {
        Set<Operator> expectedOperators = Set.of(
            Operator.EQ, Operator.NEQ, Operator.GT, Operator.LT, Operator.GTE, Operator.LTE,
            Operator.IS_NULL, Operator.IS_NOT_NULL, Operator.IN, Operator.NOT_IN, Operator.BETWEEN, Operator.NOT_BETWEEN
        );

        assertThat(bigDecimalFilter.getSupportedOperators()).containsExactlyInAnyOrderElementsOf(expectedOperators);
    }

    @Test
    void cast_validString_returnsBigDecimal() {
        String value = "123.45";

        BigDecimal result = bigDecimalFilter.cast(value);

        assertThat(result).isEqualTo(new BigDecimal("123.45"));
    }

    @Test
    void cast_invalidString_throwsNumberFormatException() {
        String value = "invalid";

        assertThatThrownBy(() -> bigDecimalFilter.cast(value))
            .isInstanceOf(NumberFormatException.class);
    }

    @Test
    void safeCast_validString_returnsBigDecimal() {
        String value = "123.45";

        BigDecimal result = bigDecimalFilter.safeCast(value, filterErrorWrapper);

        assertThat(result).isEqualTo(new BigDecimal("123.45"));
        verifyNoInteractions(bindingResult);
    }

    @Test
    void safeCast_invalidString_addsErrorAndReturnsNull() {
        String value = "invalid";
        when(filterErrorWrapper.bindingResult()).thenReturn(bindingResult);
        when(bindingResult.getObjectName()).thenReturn("objectName");
        when(filterErrorWrapper.filterWrapper()).thenReturn(filterWrapper);
        when(filterWrapper.originalFieldName()).thenReturn("fieldName");

        BigDecimal result = bigDecimalFilter.safeCast(value, filterErrorWrapper);

        assertThat(result).isNull();
        verify(bindingResult).addError(any());
    }
}
