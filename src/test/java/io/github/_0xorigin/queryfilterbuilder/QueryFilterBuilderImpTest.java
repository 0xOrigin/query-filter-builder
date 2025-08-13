package io.github._0xorigin.queryfilterbuilder;

import io.github._0xorigin.queryfilterbuilder.base.builders.FilterBuilder;
import io.github._0xorigin.queryfilterbuilder.base.parsers.FilterParser;
import io.github._0xorigin.queryfilterbuilder.base.generators.PathGenerator;
import io.github._0xorigin.queryfilterbuilder.base.builders.SortBuilder;
import io.github._0xorigin.queryfilterbuilder.base.enums.SourceType;
import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractFilterField;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.FilterOperator;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;
import io.github._0xorigin.queryfilterbuilder.base.holders.CustomFilterHolder;
import io.github._0xorigin.queryfilterbuilder.base.validators.FilterValidator;
import io.github._0xorigin.queryfilterbuilder.base.wrappers.FilterWrapper;
import io.github._0xorigin.queryfilterbuilder.exceptions.QueryBuilderConfigurationException;
import io.github._0xorigin.queryfilterbuilder.registries.FilterFieldRegistry;
import io.github._0xorigin.queryfilterbuilder.registries.FilterOperatorRegistry;
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
class QueryFilterBuilderImpTest {

    @Mock
    private FilterParser filterParser;

    @Mock
    private Path<Object> path;

    @Mock
    private Predicate predicate;

    @Mock
    private PathGenerator<TestEntity> pathGenerator;

    @Mock
    private FilterValidator filterValidator;

    @Mock
    private FilterFieldRegistry filterFieldRegistry;

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

    private QueryFilterBuilderImp<TestEntity> queryFilterBuilderImp;

    private FilterBuilder<TestEntity> filterBuilder;

    private SortBuilder<TestEntity> sortBuilder;

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
        queryFilterBuilderImp = new QueryFilterBuilderImp<>(filterBuilder, sortBuilder);
        when(filterOperator.apply(any(), any(), any(), any())).thenReturn(Optional.of(predicate));
    }

    @Nested
    class BasicFilterTests {

        @Test
        void buildFilterPredicate_WithNoFilters_ReturnConjunction() {
            when(filterParser.parse(request)).thenReturn(Collections.emptyList());
            when(criteriaBuilder.conjunction()).thenReturn(mock(Predicate.class));

            Specification result = queryFilterBuilderImp.buildFilterSpecification(filterContext);

            assertNotNull(result);
            verify(criteriaBuilder).conjunction();
            verify(filterParser).parse(request);
        }

        @Test
        void buildFilterPredicate_WithValidFilter_ReturnsPredicate() {
            try (MockedStatic<FilterFieldRegistry> mockedRegistry = mockStatic(FilterFieldRegistry.class);
                 MockedStatic<FilterOperatorRegistry> mockedOperatorRegistry = mockStatic(FilterOperatorRegistry.class)) {
                // Setup
                String field = "field";
                Operator operator = Operator.EQ;
                FilterWrapper validFilter = new FilterWrapper(field, field, operator, List.of("value"), SourceType.QUERY_PARAM, Optional.empty());

                // Mock registries
                mockedRegistry.when(() -> filterFieldRegistry.getFilterField(String.class))
                        .thenReturn(filterField);
                mockedOperatorRegistry.when(() -> filterOperatorRegistry.getOperator(operator))
                        .thenReturn(filterOperator);

                // Mock other dependencies
                when(filterParser.parse(request)).thenReturn(List.of(validFilter));
                when(path.getJavaType()).thenReturn((Class) String.class);
                when(criteriaBuilder.and(any())).thenReturn(predicate);

                Map<String, Set<Operator>> fieldOperators = new HashMap<>();
                fieldOperators.put(field, Set.of(operator));

                // Execute
                Specification result = queryFilterBuilderImp.buildFilterSpecification(filterContext);

                // Verify
                assertNotNull(result);
                verify(filterParser).parse(request);
                verify(filterField).cast(any());
            }
        }
    }

    @Nested
    class CustomFilterTests {

        @Test
        void buildFilterPredicate_WithCustomFilter_ReturnsPredicate() {
            try (MockedStatic<FilterFieldRegistry> mockedRegistry = mockStatic(FilterFieldRegistry.class);
                 MockedStatic<FilterOperatorRegistry> mockedOperatorRegistry = mockStatic(FilterOperatorRegistry.class)) {
                // Setup
                String field = "customField";
                Operator operator = Operator.EQ;
                FilterWrapper customFilter = new FilterWrapper(field, field, operator, List.of("value"), SourceType.QUERY_PARAM, Optional.empty());

                // Mock registries
                mockedRegistry.when(() -> filterFieldRegistry.getFilterField(String.class))
                        .thenReturn(filterField);
                mockedOperatorRegistry.when(() -> filterOperatorRegistry.getOperator(operator))
                        .thenReturn(filterOperator);

                // Mock other dependencies
                when(filterParser.parse(request)).thenReturn(List.of(customFilter));
                when(path.getJavaType()).thenReturn((Class) String.class);
                when(criteriaBuilder.and(any())).thenReturn(predicate);

                CustomFilterHolder<TestEntity, ?> customFilterHolder = mock(CustomFilterHolder.class);
                Map<String, CustomFilterHolder<TestEntity, ?>> customFilters = new HashMap<>();
                customFilters.put("customField", customFilterHolder);

                when(filterParser.parse(request)).thenReturn(List.of(customFilter));

                when(customFilterHolder.customFilterFunction()).thenReturn((r, q, cb, values, errors) -> Optional.of(predicate));

                Specification result = queryFilterBuilderImp.buildFilterSpecification(filterContext);

                assertNotNull(result);
                assertEquals(predicate, result);
            }
        }

        @Test
        void buildFilterPredicate_WithInvalidCustomFilter_ThrowsException() {
            String field = "customField";
            FilterWrapper customFilter = new FilterWrapper(field, field, Operator.EQ, List.of("value"), SourceType.QUERY_PARAM, Optional.empty());
            CustomFilterHolder<TestEntity, ?> customFilterHolder = mock(CustomFilterHolder.class);
            AbstractFilterField<?> filterField = mock(AbstractFilterField.class);

            Map<String, CustomFilterHolder<TestEntity, ?>> customFilters = new HashMap<>();
            customFilters.put(field, customFilterHolder);

            when(filterParser.parse(request)).thenReturn(List.of(customFilter));

            assertThrows(RuntimeException.class, () ->
                    queryFilterBuilderImp.buildFilterSpecification(filterContext)
            );
        }
    }

    @Nested
    class ValidationTests {

        @Test
        void buildFilterPredicate_WithInvalidPathGeneration_ThrowsException() {
            String field = "invalidField";
            FilterWrapper filter = new FilterWrapper(field, field, Operator.EQ, List.of("value"), SourceType.QUERY_PARAM, Optional.empty());
            when(filterParser.parse(request)).thenReturn(List.of(filter));
            Map<String, Set<Operator>> fieldOperators = new HashMap<>();
            fieldOperators.put(field, Set.of(Operator.EQ));

            assertThrows(QueryBuilderConfigurationException.class, () ->
                    queryFilterBuilderImp.buildFilterSpecification(filterContext)
            );
        }

    }

}