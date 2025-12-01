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
class ShortFilterTest {

    @Mock
    private FilterErrorWrapper filterErrorWrapper;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private FilterWrapper filterWrapper;

    @InjectMocks
    private ShortFilter shortFilter;

    @Test
    void getDataType_returnsShortClass() {
        assertThat(shortFilter.getDataType()).isEqualTo(Short.class);
    }

    @Test
    void getSupportedOperators_returnsExpectedOperators() {
        Set<Operator> expectedOperators = Set.of(
            Operator.EQ, Operator.NEQ, Operator.GT, Operator.LT, Operator.GTE, Operator.LTE,
            Operator.IS_NULL, Operator.IS_NOT_NULL, Operator.IN, Operator.NOT_IN, Operator.BETWEEN, Operator.NOT_BETWEEN
        );

        assertThat(shortFilter.getSupportedOperators()).containsExactlyInAnyOrderElementsOf(expectedOperators);
    }

    @Test
    void cast_validString_returnsShort() {
        String value = "12345";

        Short result = shortFilter.cast(value);

        assertThat(result).isEqualTo((short) 12345);
    }

    @Test
    void cast_invalidString_throwsNumberFormatException() {
        String value = "invalid";

        assertThatThrownBy(() -> shortFilter.cast(value))
            .isInstanceOf(NumberFormatException.class);
    }

    @Test
    void cast_outOfRangeString_throwsNumberFormatException() {
        String value = "32768";

        assertThatThrownBy(() -> shortFilter.cast(value))
            .isInstanceOf(NumberFormatException.class);
    }

    @Test
    void safeCast_validString_returnsShort() {
        String value = "12345";

        Short result = shortFilter.safeCast(value, filterErrorWrapper);

        assertThat(result).isEqualTo((short) 12345);
        verifyNoInteractions(bindingResult);
    }

    @Test
    void safeCast_invalidString_addsErrorAndReturnsNull() {
        String value = "invalid";
        when(filterErrorWrapper.bindingResult()).thenReturn(bindingResult);
        when(bindingResult.getObjectName()).thenReturn("objectName");
        when(filterErrorWrapper.filterWrapper()).thenReturn(filterWrapper);
        when(filterWrapper.originalFieldName()).thenReturn("fieldName");

        Short result = shortFilter.safeCast(value, filterErrorWrapper);

        assertThat(result).isNull();
        verify(bindingResult).addError(any());
    }

    @Test
    void safeCast_outOfRangeString_addsErrorAndReturnsNull() {
        String value = "32768";
        when(filterErrorWrapper.bindingResult()).thenReturn(bindingResult);
        when(bindingResult.getObjectName()).thenReturn("objectName");
        when(filterErrorWrapper.filterWrapper()).thenReturn(filterWrapper);
        when(filterWrapper.originalFieldName()).thenReturn("fieldName");

        Short result = shortFilter.safeCast(value, filterErrorWrapper);

        assertThat(result).isNull();
        verify(bindingResult).addError(any());
    }
}
