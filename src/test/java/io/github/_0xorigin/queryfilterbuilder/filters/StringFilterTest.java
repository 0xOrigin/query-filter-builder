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
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class StringFilterTest {

    @Mock
    private FilterErrorWrapper filterErrorWrapper;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private FilterWrapper filterWrapper;

    @InjectMocks
    private StringFilter stringFilter;

    @Test
    void getDataType_returnsStringClass() {
        assertThat(stringFilter.getDataType()).isEqualTo(String.class);
    }

    @Test
    void getSupportedOperators_returnsExpectedOperators() {
        Set<Operator> expectedOperators = Set.of(
            Operator.EQ, Operator.NEQ, Operator.GT, Operator.LT, Operator.GTE, Operator.LTE,
            Operator.IS_NULL, Operator.IS_NOT_NULL, Operator.IN, Operator.NOT_IN, Operator.BETWEEN, Operator.NOT_BETWEEN,
            Operator.CONTAINS, Operator.ICONTAINS, Operator.STARTS_WITH, Operator.ISTARTS_WITH, Operator.ENDS_WITH, Operator.IENDS_WITH
        );

        assertThat(stringFilter.getSupportedOperators()).containsExactlyInAnyOrderElementsOf(expectedOperators);
    }

    @Test
    void cast_validString_returnsSameString() {
        String value = "test";

        String result = stringFilter.cast(value);

        assertThat(result).isEqualTo("test");
    }

    @Test
    void cast_nullValue_returnsNull() {
        String value = null;

        String result = stringFilter.cast(value);

        assertThat(result).isNull();
    }

    @Test
    void safeCast_validString_returnsSameString() {
        String value = "test";

        String result = stringFilter.safeCast(value, filterErrorWrapper);

        assertThat(result).isEqualTo("test");
        verifyNoInteractions(bindingResult);
    }

    @Test
    void safeCast_nullValue_returnsNullWithoutAddingError() {
        String value = null;

        String result = stringFilter.safeCast(value, filterErrorWrapper);

        assertThat(result).isNull();
        verifyNoInteractions(bindingResult);
    }
}
