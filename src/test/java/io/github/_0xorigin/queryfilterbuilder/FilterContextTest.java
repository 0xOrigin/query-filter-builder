package io.github._0xorigin.queryfilterbuilder;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractFilterField;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;
import io.github._0xorigin.queryfilterbuilder.base.functions.CustomFilterFunction;
import io.github._0xorigin.queryfilterbuilder.base.holders.CustomFilterHolder;
import io.github._0xorigin.queryfilterbuilder.registries.FilterFieldRegistry;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

class FilterContextTest {

    @Mock
    private CustomFilterFunction<TestEntity> mockFilterFunction;

    @Mock
    private AbstractFilterField<?> mockFilterField;

    @Mock
    private Class<String> dataType;

    @Mock
    private FilterFieldRegistry filterFieldRegistry;

    @Mock
    private HttpServletRequest request;

    @Nested
    class OperatorFilterTests {

        @Test
        void shouldAddFilterWithMultipleOperators() {
            // Given
            String fieldName = "testField";
            Operator[] operators = {Operator.EQ, Operator.GT, Operator.LT};

            // When
            FilterContext<TestEntity> result = FilterContext.buildForType(TestEntity.class).queryParam(request, builder -> builder.addFilter(fieldName, operators)).build();

            // Then
            Set<Operator> addedOperators = result.getFilters().get(fieldName).operators();
            assertEquals(3, addedOperators.size());
            assertTrue(addedOperators.containsAll(Set.of(Operator.EQ, Operator.GT, Operator.LT)));
        }

        @Test
        void shouldOverwriteExistingOperatorFilter() {
            // Given
            String fieldName = "testField";
            Operator[] operators1 = {Operator.EQ};
            Operator[] operators2 = {Operator.GT, Operator.LT};

            // When
            FilterContext<TestEntity> result = FilterContext
                    .buildForType(TestEntity.class)
                    .queryParam(request, builder -> {
                        builder.addFilter(fieldName, operators1)
                                .addFilter(fieldName, operators2);
                    })
                    .build();

            // Then
            Set<Operator> operators = result.getFilters().get(fieldName).operators();
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
            try (MockedStatic<FilterFieldRegistry> filterRegistry = mockStatic(FilterFieldRegistry.class)) {

                // When
                FilterContext<TestEntity> result = FilterContext
                        .buildForType(TestEntity.class)
                        .queryParam(request, builder -> {
                            builder.addFilter(fieldName, String.class, mockFilterFunction);
                        })
                        .build();

                // Then
                assertTrue(result.getCustomFilters().containsKey(fieldName));

                CustomFilterHolder<TestEntity, ?> wrapper = result.getCustomFilters().get(fieldName);
                assertNotNull(wrapper);
                assertEquals(mockFilterFunction, wrapper.customFilterFunction());
            }
        }

        @Test
        void shouldOverwriteExistingCustomFilter() {
            // Given
            String fieldName = "customField";
            try (MockedStatic<FilterFieldRegistry> filterRegistry = mockStatic(FilterFieldRegistry.class)) {

                CustomFilterFunction<TestEntity> newFilterFunction = mock(CustomFilterFunction.class);

                // When
                FilterContext<TestEntity> result = FilterContext
                        .buildForType(TestEntity.class)
                        .queryParam(request, builder -> builder.addFilter(fieldName, String.class, mockFilterFunction)
                                .addFilter(fieldName, Integer.class, newFilterFunction)
                        )
                        .build();

                // Then
                assertEquals(1, result.getCustomFilters().size());
                CustomFilterHolder<TestEntity, ?> wrapper = result.getCustomFilters().get(fieldName);
                assertEquals(newFilterFunction, wrapper.customFilterFunction());
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

            try (MockedStatic<FilterFieldRegistry> filterRegistry = mockStatic(FilterFieldRegistry.class)) {

                // When
                FilterContext<TestEntity> result = FilterContext
                        .buildForType(TestEntity.class)
                        .queryParam(request, builder -> {
                            builder.addFilter(fieldName1, operators1)
                                    .addFilter(fieldName1, operators1)
                                    .addFilter(fieldName2, String.class, mockFilterFunction);
                        })
                        .build();

                // Then
                assertEquals(1, result.getCustomFilters().size());
                assertTrue(result.getFilters().containsKey(fieldName1));
                assertTrue(result.getCustomFilters().containsKey(fieldName2));
            }
        }
    }

    // Helper class for testing
    private static class TestEntity {
        private String id;
        private String name;
    }
}