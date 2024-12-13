package io.github._0xorigin.queryfilterbuilder;

import io.github._0xorigin.queryfilterbuilder.base.AbstractFilterField;
import io.github._0xorigin.queryfilterbuilder.base.CustomFilterFunction;
import io.github._0xorigin.queryfilterbuilder.base.CustomFilterWrapper;
import io.github._0xorigin.queryfilterbuilder.base.Operator;
import io.github._0xorigin.queryfilterbuilder.registries.FilterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FilterContextTest {

    private FilterContext<TestEntity> filterContext;

    @Mock
    private CustomFilterFunction<TestEntity> mockFilterFunction;

    @Mock
    private AbstractFilterField<?> mockFilterField;

    @BeforeEach
    void setUp() {
        filterContext = new FilterContext<>();
    }

    @Nested
    class OperatorFilterTests {

        @Test
        void shouldAddFilterWithMultipleOperators() {
            // Given
            String fieldName = "testField";
            Operator[] operators = {Operator.EQ, Operator.GT, Operator.LT};

            // When
            FilterContext<TestEntity> result = filterContext.addFilter(fieldName, operators);

            // Then
            assertSame(filterContext, result, "Method should return the same instance for chaining");
            assertTrue(filterContext.getFieldOperators().containsKey(fieldName));

            Set<Operator> addedOperators = filterContext.getFieldOperators().get(fieldName);
            assertEquals(3, addedOperators.size());
            assertTrue(addedOperators.containsAll(Set.of(Operator.EQ, Operator.GT, Operator.LT)));
        }

        @Test
        void shouldThrowExceptionForEmptyOperators() {
            // Given
            String fieldName = "testField";
            Operator[] operators = {};

            // Then
            assertThrows(IllegalArgumentException.class,
                    () -> filterContext.addFilter(fieldName, operators),
                    "At least one operator must be provided");
        }

        @Test
        void shouldOverwriteExistingOperatorFilter() {
            // Given
            String fieldName = "testField";
            Operator[] operators1 = {Operator.EQ};
            Operator[] operators2 = {Operator.GT, Operator.LT};

            // When
            filterContext.addFilter(fieldName, operators1);
            filterContext.addFilter(fieldName, operators2);

            // Then
            assertEquals(1, filterContext.getFieldOperators().size());
            Set<Operator> operators = filterContext.getFieldOperators().get(fieldName);
            assertEquals(2, operators.size());
            assertTrue(operators.containsAll(Set.of(Operator.GT, Operator.LT)));
        }
    }

    @Nested
    class CustomFilterTests {

        @Test
        void shouldAddCustomFilter() {
            // Given
            String fieldName = "customField";
            try (MockedStatic<FilterRegistry> filterRegistry = mockStatic(FilterRegistry.class)) {
                filterRegistry.when(() -> FilterRegistry.getFieldFilter(any())).thenReturn(mockFilterField);

                // When
                FilterContext<TestEntity> result = filterContext.addFilter(fieldName, String.class, mockFilterFunction);

                // Then
                assertSame(filterContext, result);
                assertTrue(filterContext.getCustomFieldFilters().containsKey(fieldName));

                CustomFilterWrapper<TestEntity> wrapper = filterContext.getCustomFieldFilters().get(fieldName);
                assertNotNull(wrapper);
                assertEquals(mockFilterField, wrapper.getFilterField());
                assertEquals(mockFilterFunction, wrapper.getCustomFilterFunction());
            }
        }

        @Test
        void shouldOverwriteExistingCustomFilter() {
            // Given
            String fieldName = "customField";
            try (MockedStatic<FilterRegistry> filterRegistry = mockStatic(FilterRegistry.class)) {
                filterRegistry.when(() -> FilterRegistry.getFieldFilter(any())).thenReturn(mockFilterField);

                CustomFilterFunction<TestEntity> newFilterFunction = mock(CustomFilterFunction.class);

                // When
                filterContext.addFilter(fieldName, String.class, mockFilterFunction);
                filterContext.addFilter(fieldName, Integer.class, newFilterFunction);

                // Then
                assertEquals(1, filterContext.getCustomFieldFilters().size());
                CustomFilterWrapper<TestEntity> wrapper = filterContext.getCustomFieldFilters().get(fieldName);
                assertEquals(newFilterFunction, wrapper.getCustomFilterFunction());
            }
        }
    }

    @Nested
    class CombinedFilterTests {

        @Test
        void shouldHandleMultipleFilterTypes() {
            // Given
            String fieldName1 = "field1";
            String fieldName2 = "field2";
            Operator[] operators1 = {Operator.EQ};

            try (MockedStatic<FilterRegistry> filterRegistry = mockStatic(FilterRegistry.class)) {
                filterRegistry.when(() -> FilterRegistry.getFieldFilter(any())).thenReturn(mockFilterField);

                // When
                filterContext
                        .addFilter(fieldName1, operators1)
                        .addFilter(fieldName2, String.class, mockFilterFunction);

                // Then
                assertEquals(1, filterContext.getFieldOperators().size());
                assertEquals(1, filterContext.getCustomFieldFilters().size());
                assertTrue(filterContext.getFieldOperators().containsKey(fieldName1));
                assertTrue(filterContext.getCustomFieldFilters().containsKey(fieldName2));
            }
        }
    }

    @Nested
    class ImmutabilityTests {

        @Test
        void shouldReturnUnmodifiableFieldOperators() {
            // Given
            String fieldName = "testField";
            filterContext.addFilter(fieldName, Operator.EQ);

            // When
            Map<String, Set<Operator>> operators = filterContext.getFieldOperators();

            // Then
            assertThrows(UnsupportedOperationException.class,
                    () -> operators.put("newField", Set.of(Operator.GT)));
        }

        @Test
        void shouldReturnUnmodifiableCustomFilters() {
            // Given
            String fieldName = "testField";
            try (MockedStatic<FilterRegistry> filterRegistry = mockStatic(FilterRegistry.class)) {
                filterRegistry.when(() -> FilterRegistry.getFieldFilter(any())).thenReturn(mockFilterField);
                filterContext.addFilter(fieldName, String.class, mockFilterFunction);

                // When
                Map<String, CustomFilterWrapper<TestEntity>> customFilters = filterContext.getCustomFieldFilters();

                // Then
                assertThrows(UnsupportedOperationException.class,
                        () -> customFilters.put("newField", new CustomFilterWrapper<>(mockFilterField, mockFilterFunction)));
            }
        }
    }

    // Helper class for testing
    private static class TestEntity {
        private String id;
        private String name;
    }
}