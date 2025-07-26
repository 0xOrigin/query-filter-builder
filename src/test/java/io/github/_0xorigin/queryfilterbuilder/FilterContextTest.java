package io.github._0xorigin.queryfilterbuilder;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractFilterField;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;
import io.github._0xorigin.queryfilterbuilder.base.function.CustomFilterFunction;
import io.github._0xorigin.queryfilterbuilder.base.wrapper.CustomFilterWrapper;
import io.github._0xorigin.queryfilterbuilder.registries.FilterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import java.util.Map;
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
    private FilterRegistry filterRegistry;

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
            Set<Operator> addedOperators = result.getFieldOperators().get(fieldName);
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
            assertEquals(1, result.getFieldOperators().size());
            Set<Operator> operators = result.getFieldOperators().get(fieldName);
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

                // When
                FilterContext<TestEntity> result = FilterContext
                        .buildForType(TestEntity.class)
                        .queryParam(request, builder -> {
                            builder.addFilter(fieldName, String.class, mockFilterFunction);
                        })
                        .build();

                // Then
                assertTrue(result.getCustomFieldFilters().containsKey(fieldName));

                CustomFilterWrapper<TestEntity, ?> wrapper = result.getCustomFieldFilters().get(fieldName);
                assertNotNull(wrapper);
                assertEquals(mockFilterFunction, wrapper.customFilterFunction());
            }
        }

        @Test
        void shouldOverwriteExistingCustomFilter() {
            // Given
            String fieldName = "customField";
            try (MockedStatic<FilterRegistry> filterRegistry = mockStatic(FilterRegistry.class)) {

                CustomFilterFunction<TestEntity> newFilterFunction = mock(CustomFilterFunction.class);

                // When
                FilterContext<TestEntity> result = FilterContext
                        .buildForType(TestEntity.class)
                        .queryParam(request, builder -> builder.addFilter(fieldName, String.class, mockFilterFunction)
                                .addFilter(fieldName, Integer.class, newFilterFunction)
                        )
                        .build();

                // Then
                assertEquals(1, result.getCustomFieldFilters().size());
                CustomFilterWrapper<TestEntity, ?> wrapper = result.getCustomFieldFilters().get(fieldName);
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

            try (MockedStatic<FilterRegistry> filterRegistry = mockStatic(FilterRegistry.class)) {

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
                assertEquals(1, result.getFieldOperators().size());
                assertEquals(1, result.getCustomFieldFilters().size());
                assertTrue(result.getFieldOperators().containsKey(fieldName1));
                assertTrue(result.getCustomFieldFilters().containsKey(fieldName2));
            }
        }
    }

    @Nested
    class ImmutabilityTests {

        @Test
        void shouldReturnUnmodifiableFieldOperators() {
            // Given
            String fieldName = "testField";
            FilterContext<TestEntity> result = FilterContext
                    .buildForType(TestEntity.class)
                    .queryParam(request, builder -> builder.addFilter(fieldName, Operator.EQ))
                    .build();

            // When
            Map<String, Set<Operator>> operators = result.getFieldOperators();

            // Then
            assertThrows(UnsupportedOperationException.class,
                    () -> operators.put("newField", Set.of(Operator.GT)));
        }

        @Test
        void shouldReturnUnmodifiableCustomFilters() {
            // Given
            String fieldName = "testField";
            try (MockedStatic<FilterRegistry> filterRegistry = mockStatic(FilterRegistry.class)) {
                FilterContext<TestEntity> result = FilterContext
                        .buildForType(TestEntity.class)
                        .queryParam(request, builder -> builder.addFilter(fieldName, String.class, mockFilterFunction))
                        .build();

                // When
                Map<String, CustomFilterWrapper<TestEntity, ?>> customFilters = result.getCustomFieldFilters();

                // Then
                assertThrows(UnsupportedOperationException.class,
                        () -> customFilters.put("newField", new CustomFilterWrapper<>(dataType, mockFilterFunction)));
            }
        }
    }

    // Helper class for testing
    private static class TestEntity {
        private String id;
        private String name;
    }
}