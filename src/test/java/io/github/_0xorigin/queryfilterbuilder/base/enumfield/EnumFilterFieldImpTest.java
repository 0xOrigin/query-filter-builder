package io.github._0xorigin.queryfilterbuilder.base.enumfield;

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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnumFilterFieldImpTest {

    // Sample enum for testing
    enum TestEnum {
        VALUE1, VALUE2, VALUE3
    }

    @Mock
    private FilterErrorWrapper filterErrorWrapper;

    @Mock
    private FilterWrapper filterWrapper;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private EnumFilterFieldImp enumFilterField;

    @Test
    void testCast_ValidValue_ReturnsEnum() {
        // Arrange
        String value = "VALUE1";
        Class<TestEnum> enumClass = TestEnum.class;

        // Act
        TestEnum result = enumFilterField.cast(enumClass, value);

        // Assert
        assertThat(result).isEqualTo(TestEnum.VALUE1);
    }

    @Test
    void testCast_InvalidValue_ThrowsIllegalArgumentException() {
        // Arrange
        String invalidValue = "INVALID";
        Class<TestEnum> enumClass = TestEnum.class;

        // Act & Assert
        assertThatThrownBy(() -> enumFilterField.cast(enumClass, invalidValue))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining(invalidValue);
    }

    @Test
    void testCast_NullValue_ThrowsNullPointerException() {
        // Arrange
        String nullValue = null;
        Class<TestEnum> enumClass = TestEnum.class;

        // Act & Assert
        assertThatThrownBy(() -> enumFilterField.cast(enumClass, nullValue))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("null");
    }

    @Test
    void testSafeCast_ValidValue_ReturnsEnum() {
        // Arrange
        String value = "VALUE2";
        Class<TestEnum> enumClass = TestEnum.class;

        // Act
        TestEnum result = enumFilterField.safeCast(enumClass, value, filterErrorWrapper);

        // Assert
        assertThat(result).isEqualTo(TestEnum.VALUE2);
        verify(filterErrorWrapper, never()).bindingResult();
        verifyNoInteractions(bindingResult);
    }

    @Test
    void testSafeCast_InvalidValue_ReturnsNullAndAddsError() {
        // Arrange
        when(filterErrorWrapper.bindingResult()).thenReturn(bindingResult);
        when(bindingResult.getObjectName()).thenReturn("objectName");
        when(filterErrorWrapper.filterWrapper()).thenReturn(filterWrapper);
        when(filterErrorWrapper.filterWrapper().originalFieldName()).thenReturn("testField");
        String invalidValue = "INVALID";
        Class<TestEnum> enumClass = TestEnum.class;

        // Act
        TestEnum result = enumFilterField.safeCast(enumClass, invalidValue, filterErrorWrapper);

        // Assert
        assertThat(result).isNull();
        verify(filterErrorWrapper, times(1)).bindingResult();
        verify(filterErrorWrapper.filterWrapper(), times(1)).originalFieldName();
        verify(bindingResult, times(1)).addError(any());
    }

    @Test
    void testSafeCast_NullValue_ReturnsNullAndAddsError() {
        // Arrange
        when(filterErrorWrapper.bindingResult()).thenReturn(bindingResult);
        when(bindingResult.getObjectName()).thenReturn("objectName");
        when(filterErrorWrapper.filterWrapper()).thenReturn(filterWrapper);
        when(filterErrorWrapper.filterWrapper().originalFieldName()).thenReturn("testField");
        String nullValue = null;
        Class<TestEnum> enumClass = TestEnum.class;

        // Act
        TestEnum result = enumFilterField.safeCast(enumClass, nullValue, filterErrorWrapper);

        // Assert
        assertThat(result).isNull();
        verify(filterErrorWrapper, times(1)).bindingResult();
        verify(filterErrorWrapper.filterWrapper(), times(1)).originalFieldName();
        verify(bindingResult, times(1)).addError(any());
    }

    @Test
    void testGetSupportedOperators_ReturnsExpectedOperators() {
        // Act
        Set<Operator> supportedOperators = enumFilterField.getSupportedOperators();

        // Assert
        assertThat(supportedOperators)
            .isNotNull()
            .hasSize(18)
            .containsExactlyInAnyOrder(
                Operator.EQ, Operator.NEQ, Operator.GT, Operator.LT, Operator.GTE, Operator.LTE,
                Operator.IS_NULL, Operator.IS_NOT_NULL, Operator.IN, Operator.NOT_IN, Operator.BETWEEN, Operator.NOT_BETWEEN,
                Operator.CONTAINS, Operator.ICONTAINS, Operator.STARTS_WITH, Operator.ISTARTS_WITH, Operator.ENDS_WITH, Operator.IENDS_WITH
            );
    }

    @Test
    void testGetSupportedOperators_Immutability_CannotModifySet() {
        // Arrange
        Set<Operator> supportedOperators = enumFilterField.getSupportedOperators();

        // Act & Assert
        assertThatThrownBy(() -> supportedOperators.add(null))
            .isInstanceOf(UnsupportedOperationException.class);
    }
}
