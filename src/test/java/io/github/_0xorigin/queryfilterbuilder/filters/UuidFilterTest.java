package io.github._0xorigin.queryfilterbuilder.filters;

import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;
import io.github._0xorigin.queryfilterbuilder.base.wrappers.FilterErrorWrapper;
import io.github._0xorigin.queryfilterbuilder.base.wrappers.FilterWrapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UuidFilterTest {

    @Mock
    private FilterErrorWrapper filterErrorWrapper;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private FilterWrapper filterWrapper;

    @InjectMocks
    private UuidFilter uuidFilter;

    @Test
    void getDataType_returnsUuidClass() {
        assertThat(uuidFilter.getDataType()).isEqualTo(UUID.class);
    }

    @Test
    void getSupportedOperators_returnsExpectedOperators() {
        Set<Operator> expectedOperators = Set.of(
            Operator.EQ, Operator.NEQ, Operator.GT, Operator.LT, Operator.GTE, Operator.LTE,
            Operator.IS_NULL, Operator.IS_NOT_NULL, Operator.IN, Operator.NOT_IN, Operator.BETWEEN, Operator.NOT_BETWEEN
        );

        assertThat(uuidFilter.getSupportedOperators()).containsExactlyInAnyOrderElementsOf(expectedOperators);
    }

    @Test
    void cast_validString_returnsUuid() {
        String value = "123e4567-e89b-12d3-a456-426614174000";

        UUID result = uuidFilter.cast(value);

        assertThat(result).isEqualTo(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "123e4567-12d3-a456-426614174000",
        "invalid"
    })
    void cast_invalidString_throwsIllegalArgumentException(String value) {
        assertThatThrownBy(() -> uuidFilter.cast(value))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void safeCast_validString_returnsUuid() {
        String value = "123e4567-e89b-12d3-a456-426614174000";

        UUID result = uuidFilter.safeCast(value, filterErrorWrapper);

        assertThat(result).isEqualTo(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        verifyNoInteractions(bindingResult);
    }

    @Test
    void safeCast_invalidString_addsErrorAndReturnsNull() {
        String value = "invalid";
        when(filterErrorWrapper.bindingResult()).thenReturn(bindingResult);
        when(bindingResult.getObjectName()).thenReturn("objectName");
        when(filterErrorWrapper.filterWrapper()).thenReturn(filterWrapper);
        when(filterWrapper.originalFieldName()).thenReturn("fieldName");

        UUID result = uuidFilter.safeCast(value, filterErrorWrapper);

        assertThat(result).isNull();
        verify(bindingResult).addError(any());
    }
}
