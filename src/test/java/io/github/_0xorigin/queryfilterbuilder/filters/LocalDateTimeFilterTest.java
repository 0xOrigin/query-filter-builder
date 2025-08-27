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

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocalDateTimeFilterTest {

    @Mock
    private FilterErrorWrapper filterErrorWrapper;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private FilterWrapper filterWrapper;

    @InjectMocks
    private LocalDateTimeFilter localDateTimeFilter;

    @Test
    void getDataType_returnsLocalDateTimeClass() {
        assertThat(localDateTimeFilter.getDataType()).isEqualTo(LocalDateTime.class);
    }

    @Test
    void getSupportedOperators_returnsExpectedOperators() {
        Set<Operator> expectedOperators = Set.of(
            Operator.EQ, Operator.NEQ, Operator.GT, Operator.LT, Operator.GTE, Operator.LTE,
            Operator.IS_NULL, Operator.IS_NOT_NULL, Operator.IN, Operator.NOT_IN, Operator.BETWEEN, Operator.NOT_BETWEEN
        );

        assertThat(localDateTimeFilter.getSupportedOperators()).containsExactlyInAnyOrderElementsOf(expectedOperators);
    }

    @Test
    void cast_validString_returnsLocalDateTime() {
        String value = "2025-08-27T15:30:00";

        LocalDateTime result = localDateTimeFilter.cast(value);

        assertThat(result).isEqualTo(LocalDateTime.parse("2025-08-27T15:30:00"));
    }

    @Test
    void cast_invalidString_throwsDateTimeParseException() {
        String value = "invalid";

        assertThatThrownBy(() -> localDateTimeFilter.cast(value))
            .isInstanceOf(DateTimeParseException.class);
    }

    @Test
    void cast_invalidDateTimeString_throwsDateTimeParseException() {
        String value = "2025-08-27T1530:00";

        assertThatThrownBy(() -> localDateTimeFilter.cast(value))
            .isInstanceOf(DateTimeParseException.class);
    }

    @Test
    void safeCast_validString_returnsLocalDateTime() {
        String value = "2025-08-27T15:30:00";

        LocalDateTime result = localDateTimeFilter.safeCast(value, filterErrorWrapper);

        assertThat(result).isEqualTo(LocalDateTime.parse("2025-08-27T15:30:00"));
        verifyNoInteractions(bindingResult);
    }

    @Test
    void safeCast_invalidString_addsErrorAndReturnsNull() {
        String value = "invalid";
        when(filterErrorWrapper.bindingResult()).thenReturn(bindingResult);
        when(bindingResult.getObjectName()).thenReturn("objectName");
        when(filterErrorWrapper.filterWrapper()).thenReturn(filterWrapper);
        when(filterWrapper.originalFieldName()).thenReturn("fieldName");

        LocalDateTime result = localDateTimeFilter.safeCast(value, filterErrorWrapper);

        assertThat(result).isNull();
        verify(bindingResult).addError(any());
    }
}
