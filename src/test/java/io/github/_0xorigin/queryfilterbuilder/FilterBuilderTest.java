package io.github._0xorigin.queryfilterbuilder;

import io.github._0xorigin.queryfilterbuilder.base.Parser;
import io.github._0xorigin.queryfilterbuilder.base.PathGenerator;
import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractFilterField;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.FilterOperator;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;
import io.github._0xorigin.queryfilterbuilder.base.wrapper.CustomFilterWrapper;
import io.github._0xorigin.queryfilterbuilder.base.wrapper.FilterWrapper;
import io.github._0xorigin.queryfilterbuilder.exceptions.InvalidFilterConfigurationException;
import io.github._0xorigin.queryfilterbuilder.registries.FilterOperatorRegistry;
import io.github._0xorigin.queryfilterbuilder.registries.FilterRegistry;
import jakarta.persistence.criteria.*;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FilterBuilderTest {

    @Mock
    private Parser filterParser;

    @Mock
    private Path<Object> path;

    @Mock
    private Predicate predicate;

    @Mock
    private PathGenerator<TestEntity> pathGenerator;

    @Mock
    private FilterValidator filterValidator;

    @Mock
    private FilterRegistry filterRegistry;

    @Mock
    private FilterOperatorRegistry filterOperatorRegistry;

    @Mock
    private AbstractFilterField<String> filterField;

    @Mock
    private Root<TestEntity> root;

    @Mock
    private CriteriaQuery<?> criteriaQuery;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private FilterContext<TestEntity> filterContext;

    @Mock
    private FilterOperator filterOperator;

    @Mock
    private HttpServletRequest request;

    private FilterBuilder<TestEntity> filterBuilder;

    private static class TestEntity {

        private String field;
        private Integer numericField;

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public Integer getNumericField() {
            return numericField;
        }

        public void setNumericField(Integer numericField) {
            this.numericField = numericField;
        }
    }

    @BeforeEach
    void setUp() {
        filterBuilder = new FilterBuilder<>(filterParser, pathGenerator, filterRegistry, filterOperatorRegistry);
        when(filterOperator.apply(any(), any(), any(), any())).thenReturn(Optional.of(predicate));
    }

    @Nested
    class BasicFilterTests {

        @Test
        void buildFilterPredicate_WithNoFilters_ReturnConjunction() {
            when(filterParser.parse(request)).thenReturn(Collections.emptyList());
            when(criteriaBuilder.conjunction()).thenReturn(mock(Predicate.class));

            Specification result = filterBuilder.buildFilterSpecification(request, filterContext);

            assertNotNull(result);
            verify(criteriaBuilder).conjunction();
            verify(filterParser).parse(request);
        }

        @Test
        void buildFilterPredicate_WithValidFilter_ReturnsPredicate() {
            try (MockedStatic<FilterRegistry> mockedRegistry = mockStatic(FilterRegistry.class);
                 MockedStatic<FilterOperatorRegistry> mockedOperatorRegistry = mockStatic(FilterOperatorRegistry.class)) {
                // Setup
                String field = "field";
                Operator operator = Operator.EQ;
                FilterWrapper validFilter = new FilterWrapper(field, field, operator, List.of("value"));

                // Mock registries
                mockedRegistry.when(() -> filterRegistry.getFieldFilter(String.class))
                        .thenReturn(filterField);
                mockedOperatorRegistry.when(() -> filterOperatorRegistry.getOperator(operator))
                        .thenReturn(filterOperator);

                // Mock other dependencies
                when(filterParser.parse(request)).thenReturn(List.of(validFilter));
                when(pathGenerator.generate(eq(root), eq(field), any())).thenReturn((Path) path);
                when(path.getJavaType()).thenReturn((Class) String.class);
                when(criteriaBuilder.and(any())).thenReturn(predicate);

                Map<String, Set<Operator>> fieldOperators = new HashMap<>();
                fieldOperators.put(field, Set.of(operator));
                when(filterContext.getFieldOperators()).thenReturn(fieldOperators);

                // Execute
                Specification result = filterBuilder.buildFilterSpecification(request, filterContext);

                // Verify
                assertNotNull(result);
                verify(filterParser).parse(request);
                verify(pathGenerator).generate(eq(root), eq(field), any());
                verify(filterField).cast(any());
            }
        }
    }

    @Nested
    class CustomFilterTests {

        @Test
        void buildFilterPredicate_WithCustomFilter_ReturnsPredicate() {
            try (MockedStatic<FilterRegistry> mockedRegistry = mockStatic(FilterRegistry.class);
                 MockedStatic<FilterOperatorRegistry> mockedOperatorRegistry = mockStatic(FilterOperatorRegistry.class)) {
                // Setup
                String field = "customField";
                Operator operator = Operator.EQ;
                FilterWrapper customFilter = new FilterWrapper(field, field, operator, List.of("value"));

                // Mock registries
                mockedRegistry.when(() -> filterRegistry.getFieldFilter(String.class))
                        .thenReturn(filterField);
                mockedOperatorRegistry.when(() -> filterOperatorRegistry.getOperator(operator))
                        .thenReturn(filterOperator);

                // Mock other dependencies
                when(filterParser.parse(request)).thenReturn(List.of(customFilter));
                when(pathGenerator.generate(eq(root), eq(field), any())).thenReturn((Path) path);
                when(path.getJavaType()).thenReturn((Class) String.class);
                when(criteriaBuilder.and(any())).thenReturn(predicate);

                CustomFilterWrapper<TestEntity, ?> customFilterWrapper = mock(CustomFilterWrapper.class);
                Map<String, CustomFilterWrapper<TestEntity, ?>> customFilters = new HashMap<>();
                customFilters.put("customField", customFilterWrapper);

                when(filterParser.parse(request)).thenReturn(List.of(customFilter));
                when(filterContext.getCustomFieldFilters()).thenReturn(customFilters);

                when(customFilterWrapper.customFilterFunction()).thenReturn((r, q, cb, values, errors) -> Optional.of(predicate));

                Specification result = filterBuilder.buildFilterSpecification(request, filterContext);

                assertNotNull(result);
                assertEquals(predicate, result);
            }
        }

        @Test
        void buildFilterPredicate_WithInvalidCustomFilter_ThrowsException() {
            String field = "customField";
            FilterWrapper customFilter = new FilterWrapper(field, field, Operator.EQ, List.of("value"));
            CustomFilterWrapper<TestEntity, ?> customFilterWrapper = mock(CustomFilterWrapper.class);
            AbstractFilterField<?> filterField = mock(AbstractFilterField.class);

            Map<String, CustomFilterWrapper<TestEntity, ?>> customFilters = new HashMap<>();
            customFilters.put(field, customFilterWrapper);

            when(filterParser.parse(request)).thenReturn(List.of(customFilter));
            when(filterContext.getCustomFieldFilters()).thenReturn(customFilters);
            doThrow(RuntimeException.class).when(filterValidator).validateFilterFieldAndOperator(any(), any(), any(), any());

            assertThrows(RuntimeException.class, () ->
                    filterBuilder.buildFilterSpecification(request, filterContext)
            );
        }
    }

    @Nested
    class ValidationTests {

        @Test
        void buildFilterPredicate_WithInvalidPathGeneration_ThrowsException() {
            String field = "invalidField";
            FilterWrapper filter = new FilterWrapper(field, field, Operator.EQ, List.of("value"));
            when(filterParser.parse(request)).thenReturn(List.of(filter));
            Map<String, Set<Operator>> fieldOperators = new HashMap<>();
            fieldOperators.put(field, Set.of(Operator.EQ));
            when(filterContext.getFieldOperators()).thenReturn(fieldOperators);

            doThrow(InvalidFilterConfigurationException.class)
                    .when(pathGenerator).generate(any(), any(), any());

            assertThrows(InvalidFilterConfigurationException.class, () ->
                    filterBuilder.buildFilterSpecification(request, filterContext)
            );
        }

    }

}