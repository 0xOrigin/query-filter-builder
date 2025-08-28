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

import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ZonedDateTimeFilterTest {

    @Mock
    private FilterErrorWrapper filterErrorWrapper;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private FilterWrapper filterWrapper;

    @InjectMocks
    private ZonedDateTimeFilter zonedDateTimeFilter;

    @Test
    void getDataType_returnsZonedDateTimeClass() {
        assertThat(zonedDateTimeFilter.getDataType()).isEqualTo(ZonedDateTime.class);
    }

    @Test
    void getSupportedOperators_returnsExpectedOperators() {
        Set<Operator> expectedOperators = Set.of(
            Operator.EQ, Operator.NEQ, Operator.GT, Operator.LT, Operator.GTE, Operator.LTE,
            Operator.IS_NULL, Operator.IS_NOT_NULL, Operator.IN, Operator.NOT_IN, Operator.BETWEEN, Operator.NOT_BETWEEN
        );

        assertThat(zonedDateTimeFilter.getSupportedOperators()).containsExactlyInAnyOrderElementsOf(expectedOperators);
    }

    @Test
    void cast_validString_returnsZonedDateTime() {
        String value = "2025-08-27T15:30:00+02:00[Europe/Paris]";

        ZonedDateTime result = zonedDateTimeFilter.cast(value);

        assertThat(result).isEqualTo(ZonedDateTime.parse("2025-08-27T15:30:00+02:00[Europe/Paris]"));
    }

    @Test
    void cast_invalidString_throwsDateTimeParseException() {
        String value = "invalid";

        assertThatThrownBy(() -> zonedDateTimeFilter.cast(value))
            .isInstanceOf(DateTimeParseException.class);
    }

    @Test
    void cast_invalidDateTimeString_throwsDateTimeParseException() {
        String value = "2025-08-27T15:30:00";

        assertThatThrownBy(() -> zonedDateTimeFilter.cast(value))
            .isInstanceOf(DateTimeParseException.class);
    }

    @Test
    void safeCast_validString_returnsZonedDateTime() {
        String value = "2025-08-27T15:30:00+02:00[Europe/Paris]";

        ZonedDateTime result = zonedDateTimeFilter.safeCast(value, filterErrorWrapper);

        assertThat(result).isEqualTo(ZonedDateTime.parse("2025-08-27T15:30:00+02:00[Europe/Paris]"));
        verifyNoInteractions(bindingResult);
    }

    @Test
    void safeCast_invalidString_addsErrorAndReturnsNull() {
        String value = "invalid";
        when(filterErrorWrapper.bindingResult()).thenReturn(bindingResult);
        when(bindingResult.getObjectName()).thenReturn("objectName");
        when(filterErrorWrapper.filterWrapper()).thenReturn(filterWrapper);
        when(filterWrapper.originalFieldName()).thenReturn("fieldName");

        ZonedDateTime result = zonedDateTimeFilter.safeCast(value, filterErrorWrapper);

        assertThat(result).isNull();
        verify(bindingResult).addError(any());
    }
}
