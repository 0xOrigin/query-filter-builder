package io.github._0xorigin.queryfilterbuilder.registries;

import io.github._0xorigin.queryfilterbuilder.base.filteroperator.FilterOperator;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;
import io.github._0xorigin.queryfilterbuilder.base.services.LocalizationService;
import io.github._0xorigin.queryfilterbuilder.configs.FilterOperatorConfig;
import io.github._0xorigin.queryfilterbuilder.configs.FilterOperatorRegistryConfig;
import io.github._0xorigin.queryfilterbuilder.operators.Equals;
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

@SpringBootTest(classes = {LocalizationService.class, FilterOperatorConfig.class, FilterOperatorRegistryConfig.class})
@ExtendWith(MockitoExtension.class)
class FilterOperatorRegistryTest {

    @Mock
    private FilterOperator greaterThanOperator;

    @Mock
    private FilterOperator lessThanOperator;

    // Use real Equals implementation
    private final FilterOperator equalsOperator = new Equals();

    @Autowired
    private FilterOperatorRegistry registry;

    @Test
    void givenNonEmptyInjectedList_whenConstructed_thenPopulatesMapCorrectly() {
        // Arrange
        List<FilterOperator> operators = List.of(equalsOperator, greaterThanOperator);
        when(greaterThanOperator.getOperatorConstant()).thenReturn(Operator.GT);

        // Act
        FilterOperatorRegistry registry = new FilterOperatorRegistry(operators);

        // Assert
        assertThat(registry.getOperator(Operator.EQ))
            .isNotNull()
            .isSameAs(equalsOperator)
            .withFailMessage("Equals operator should be correctly mapped");
        assertThat(registry.getOperator(Operator.GT))
            .isNotNull()
            .isSameAs(greaterThanOperator)
            .withFailMessage("Greater-than operator should be correctly mapped");
        assertThat(operators)
            .hasSize(2)
            .withFailMessage("Injected list should contain 2 operators");
    }

    @Test
    void givenNonExistentOperator_whenGetOperator_thenReturnsNull() {
        // Arrange
        List<FilterOperator> operators = List.of(equalsOperator);
        FilterOperatorRegistry registry = new FilterOperatorRegistry(operators);

        // Act
        FilterOperator result = registry.getOperator(Operator.NEQ);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void givenDuplicateOperatorInInjectedList_whenConstructed_thenUsesLastOperator() {
        // Arrange
        FilterOperator newEqualsOperator = mock(FilterOperator.class);
        when(newEqualsOperator.getOperatorConstant()).thenReturn(Operator.EQ);

        List<FilterOperator> operators = List.of(equalsOperator, newEqualsOperator);

        // Act
        FilterOperatorRegistry registry = new FilterOperatorRegistry(operators);

        // Assert
        assertThat(registry.getOperator(Operator.EQ))
            .isNotNull()
            .isSameAs(newEqualsOperator)
            .withFailMessage("Should map to the last operator for EQ operator constant");
    }

    @Test
    void givenMultipleOperatorsInInjectedList_whenConstructed_thenHandlesAllCorrectly() {
        // Arrange
        List<FilterOperator> operators = List.of(equalsOperator, greaterThanOperator, lessThanOperator);
        when(greaterThanOperator.getOperatorConstant()).thenReturn(Operator.GT);
        when(lessThanOperator.getOperatorConstant()).thenReturn(Operator.LT);

        // Act
        FilterOperatorRegistry registry = new FilterOperatorRegistry(operators);

        // Assert
        assertThat(registry.getOperator(Operator.EQ))
            .isSameAs(equalsOperator)
            .withFailMessage("Equals operator should be correctly mapped");
        assertThat(registry.getOperator(Operator.GT))
            .isSameAs(greaterThanOperator)
            .withFailMessage("Greater-than operator should be correctly mapped");
        assertThat(registry.getOperator(Operator.LT))
            .isSameAs(lessThanOperator)
            .withFailMessage("Less-than operator should be correctly mapped");
        assertThat(operators)
            .hasSize(3)
            .withFailMessage("Injected list should contain 3 operators");
    }

    @Test
    void filterOperatorRegistry_autowired_shouldContainAllFilterOperators() {
        final var NUMBER_FILTER_OPERATORS = 18;
        var filterOperators = registry.getOperators();
        assertThat(filterOperators)
            .isNotNull()
            .hasSize(NUMBER_FILTER_OPERATORS);
    }
}
