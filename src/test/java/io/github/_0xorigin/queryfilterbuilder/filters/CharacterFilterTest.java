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
class CharacterFilterTest {

    @Mock
    private FilterErrorWrapper filterErrorWrapper;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private FilterWrapper filterWrapper;

    @InjectMocks
    private CharacterFilter characterFilter;

    @Test
    void getDataType_returnsCharacterClass() {
        assertThat(characterFilter.getDataType()).isEqualTo(Character.class);
    }

    @Test
    void getSupportedOperators_returnsExpectedOperators() {
        Set<Operator> expectedOperators = Set.of(
            Operator.EQ, Operator.NEQ, Operator.GT, Operator.LT, Operator.GTE, Operator.LTE,
            Operator.IS_NULL, Operator.IS_NOT_NULL, Operator.IN, Operator.NOT_IN, Operator.BETWEEN, Operator.NOT_BETWEEN
        );

        assertThat(characterFilter.getSupportedOperators()).containsExactlyInAnyOrderElementsOf(expectedOperators);
    }

    @Test
    void cast_validString_returnsCharacter() {
        String value = "A";

        Character result = characterFilter.cast(value);

        assertThat(result).isEqualTo('A');
    }

    @Test
    void cast_emptyString_throwsStringIndexOutOfBoundsException() {
        String value = "";

        assertThatThrownBy(() -> characterFilter.cast(value))
            .isInstanceOf(StringIndexOutOfBoundsException.class);
    }

    @Test
    void cast_nullValue_throwsNullPointerException() {
        assertThatThrownBy(() -> characterFilter.cast(null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void safeCast_validString_returnsCharacter() {
        String value = "A";

        Character result = characterFilter.safeCast(value, filterErrorWrapper);

        assertThat(result).isEqualTo('A');
        verifyNoInteractions(bindingResult);
    }

    @Test
    void safeCast_emptyString_addsErrorAndReturnsNull() {
        String value = "";
        when(filterErrorWrapper.bindingResult()).thenReturn(bindingResult);
        when(bindingResult.getObjectName()).thenReturn("objectName");
        when(filterErrorWrapper.filterWrapper()).thenReturn(filterWrapper);
        when(filterWrapper.originalFieldName()).thenReturn("fieldName");

        Character result = characterFilter.safeCast(value, filterErrorWrapper);

        assertThat(result).isNull();
        verify(bindingResult).addError(any());
    }

    @Test
    void safeCast_nullValue_addsErrorAndReturnsNull() {
        when(filterErrorWrapper.bindingResult()).thenReturn(bindingResult);
        when(bindingResult.getObjectName()).thenReturn("objectName");
        when(filterErrorWrapper.filterWrapper()).thenReturn(filterWrapper);
        when(filterWrapper.originalFieldName()).thenReturn("fieldName");

        Character result = characterFilter.safeCast(null, filterErrorWrapper);

        assertThat(result).isNull();
        verify(bindingResult).addError(any());
    }
}
