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

import java.time.Year;
import java.time.format.DateTimeParseException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class YearFilterTest {

    @Mock
    private FilterErrorWrapper filterErrorWrapper;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private FilterWrapper filterWrapper;

    @InjectMocks
    private YearFilter yearFilter;

    @Test
    void getDataType_returnsYearClass() {
        assertThat(yearFilter.getDataType()).isEqualTo(Year.class);
    }

    @Test
    void getSupportedOperators_returnsExpectedOperators() {
        Set<Operator> expectedOperators = Set.of(
            Operator.EQ, Operator.NEQ, Operator.GT, Operator.LT, Operator.GTE, Operator.LTE,
            Operator.IS_NULL, Operator.IS_NOT_NULL, Operator.IN, Operator.NOT_IN, Operator.BETWEEN, Operator.NOT_BETWEEN
        );

        assertThat(yearFilter.getSupportedOperators()).containsExactlyInAnyOrderElementsOf(expectedOperators);
    }

    @Test
    void cast_validString_returnsYear() {
        String value = "2025";

        Year result = yearFilter.cast(value);

        assertThat(result).isEqualTo(Year.parse("2025"));
    }

    @Test
    void cast_invalidString_throwsDateTimeParseException() {
        String value = "invalid";

        assertThatThrownBy(() -> yearFilter.cast(value))
            .isInstanceOf(DateTimeParseException.class);
    }

    @Test
    void cast_invalidYearString_throwsDateTimeParseException() {
        String value = "y2025";

        assertThatThrownBy(() -> yearFilter.cast(value))
            .isInstanceOf(DateTimeParseException.class);
    }

    @Test
    void safeCast_validString_returnsYear() {
        String value = "2025";

        Year result = yearFilter.safeCast(value, filterErrorWrapper);

        assertThat(result).isEqualTo(Year.parse("2025"));
        verifyNoInteractions(bindingResult);
    }

    @Test
    void safeCast_invalidString_addsErrorAndReturnsNull() {
        String value = "invalid";
        when(filterErrorWrapper.bindingResult()).thenReturn(bindingResult);
        when(bindingResult.getObjectName()).thenReturn("objectName");
        when(filterErrorWrapper.filterWrapper()).thenReturn(filterWrapper);
        when(filterWrapper.originalFieldName()).thenReturn("fieldName");

        Year result = yearFilter.safeCast(value, filterErrorWrapper);

        assertThat(result).isNull();
        verify(bindingResult).addError(any());
    }
}
