package io.github._0xorigin.queryfilterbuilder.registries;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractFilterField;
import io.github._0xorigin.queryfilterbuilder.configs.FilterFieldConfig;
import io.github._0xorigin.queryfilterbuilder.configs.FilterFieldRegistryConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {FilterFieldConfig.class, FilterFieldRegistryConfig.class})
@ExtendWith(MockitoExtension.class)
class FilterFieldRegistryTest {

    @Mock
    private AbstractFilterField<String> stringFilterField;

    @Mock
    private AbstractFilterField<Integer> integerFilterField;

    @Autowired
    private FilterFieldRegistry realRegistry;

    @Test
    void givenNonEmptyInjectedList_whenConstructed_thenPopulatesMapCorrectly() {
        // Arrange
        List<AbstractFilterField<? extends Comparable<?>>> filterFields = List.of(stringFilterField, integerFilterField);
        when(stringFilterField.getDataType()).thenReturn(String.class);
        when(integerFilterField.getDataType()).thenReturn(Integer.class);

        // Act
        FilterFieldRegistry registry = new FilterFieldRegistry(filterFields);

        // Assert
        assertThat(registry.getFilterField(String.class))
            .isNotNull()
            .isSameAs(stringFilterField)
            .withFailMessage("String filter field should be correctly mapped");
        assertThat(registry.getFilterField(Integer.class))
            .isNotNull()
            .isSameAs(integerFilterField)
            .withFailMessage("Integer filter field should be correctly mapped");
        assertThat(filterFields)
            .hasSize(2)
            .withFailMessage("Injected list should contain 2 filter fields");
    }

    @Test
    void givenNonExistentDataType_whenGetFilterField_thenReturnsNull() {
        // Arrange
        List<AbstractFilterField<? extends Comparable<?>>> filterFields = List.of(stringFilterField);
        FilterFieldRegistry registry = new FilterFieldRegistry(filterFields);

        // Act
        AbstractFilterField<?> result = registry.getFilterField(Long.class);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void givenDuplicateDataTypeInInjectedList_whenConstructed_thenUsesLastFilterField() {
        // Arrange
        @SuppressWarnings("unchecked")
        AbstractFilterField<String> newStringFilterField = mock(AbstractFilterField.class);
        when(newStringFilterField.getDataType()).thenReturn(String.class);
        List<AbstractFilterField<? extends Comparable<?>>> filterFields = List.of(stringFilterField, newStringFilterField);

        // Act
        FilterFieldRegistry registry = new FilterFieldRegistry(filterFields);

        // Assert
        assertThat(registry.getFilterField(String.class))
            .isNotNull()
            .isSameAs(newStringFilterField)
            .withFailMessage("Should map to the last filter field for String data type");
        assertThat(filterFields)
            .hasSize(2)
            .withFailMessage("Injected list should contain 2 filter fields");
    }

    @Test
    void givenMultipleFilterFieldsInInjectedList_whenConstructed_thenHandlesAllCorrectly() {
        // Arrange
        @SuppressWarnings("unchecked")
        AbstractFilterField<Double> doubleFilterField = mock(AbstractFilterField.class);
        when(doubleFilterField.getDataType()).thenReturn(Double.class);
        when(stringFilterField.getDataType()).thenReturn(String.class);
        when(integerFilterField.getDataType()).thenReturn(Integer.class);
        List<AbstractFilterField<? extends Comparable<?>>> filterFields = List.of(stringFilterField, integerFilterField, doubleFilterField);

        // Act
        FilterFieldRegistry registry = new FilterFieldRegistry(filterFields);

        // Assert
        assertThat(registry.getFilterField(String.class))
            .isSameAs(stringFilterField)
            .withFailMessage("String filter field should be correctly mapped");
        assertThat(registry.getFilterField(Integer.class))
            .isSameAs(integerFilterField)
            .withFailMessage("Integer filter field should be correctly mapped");
        assertThat(registry.getFilterField(Double.class))
            .isSameAs(doubleFilterField)
            .withFailMessage("Double filter field should be correctly mapped");
        assertThat(filterFields)
            .hasSize(3)
            .withFailMessage("Injected list should contain 3 filter fields");
    }

    @Test
    void filterFieldRegistry_autowired_shouldContainAllFilterFields() {
        final var NUMBER_FILTER_FIELDS = 21;
        var filterFields = realRegistry.getFilterFields();
        assertThat(filterFields)
            .isNotNull()
            .hasSize(NUMBER_FILTER_FIELDS);
    }
}
