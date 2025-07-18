package io.github._0xorigin.queryfilterbuilder;

import io.github._0xorigin.queryfilterbuilder.base.wrapper.FilterWrapper;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FilterParserTest {

    @Mock
    private HttpServletRequest request;

    private FilterParser filterParser;
    private static final String FIELD_DELIMITER = "__";

    @BeforeEach
    void setUp() {
        filterParser = new FilterParser(request);
        ReflectionTestUtils.setField(filterParser, "FIELD_DELIMITER", FIELD_DELIMITER);
    }

    @Nested
    class BasicFunctionality {

        @Test
        void shouldReturnEmptyListForNoParameters() {
            // Given
            Map<String, String[]> emptyParams = new HashMap<>();
            when(request.getParameterMap()).thenReturn(emptyParams);

            // When
            List<FilterWrapper> result = filterParser.parse();

            // Then
            assertTrue(result.isEmpty());
        }

        @Test
        void shouldGetCorrectRequest() {
            // When
            HttpServletRequest result = filterParser.getRequest();

            // Then
            assertEquals(request, result);
        }

        @Test
        void shouldGetCorrectRequestQueryParams() {
            // Given
            Map<String, String[]> params = new HashMap<>();
            when(request.getParameterMap()).thenReturn(params);

            // When
            Map<String, String[]> result = filterParser.getRequestQueryParams();

            // Then
            assertEquals(params, result);
        }
    }

    @Nested
    class SimpleFilterParsing {

        @Test
        void shouldParseSimpleEqualFilter() {
            // Given
            Map<String, String[]> params = new HashMap<>();
            params.put("name", new String[]{"John"});
            when(request.getParameterMap()).thenReturn(params);

            // When
            List<FilterWrapper> result = filterParser.parse();

            // Then
            assertEquals(1, result.size());
            FilterWrapper wrapper = result.get(0);
            assertEquals("name", wrapper.field());
            assertEquals("name", wrapper.originalFieldName());
            assertEquals(Operator.EQ, wrapper.operator());
            assertEquals(List.of("John"), wrapper.values());
        }

        @Test
        void shouldParseExplicitOperatorFilter() {
            // Given
            Map<String, String[]> params = new HashMap<>();
            params.put("age__gt", new String[]{"25"});
            when(request.getParameterMap()).thenReturn(params);

            // When
            List<FilterWrapper> result = filterParser.parse();

            // Then
            assertEquals(1, result.size());
            FilterWrapper wrapper = result.get(0);
            assertEquals("age", wrapper.field());
            assertEquals("age__gt", wrapper.originalFieldName());
            assertEquals(Operator.GT, wrapper.operator());
            assertEquals(List.of("25"), wrapper.values());
        }
    }

    @Nested
    class ComplexFilterParsing {

        @Test
        void shouldParseNestedFieldFilter() {
            // Given
            Map<String, String[]> params = new HashMap<>();
            params.put("user__manager__name__icontains", new String[]{"Smith"});
            when(request.getParameterMap()).thenReturn(params);

            // When
            List<FilterWrapper> result = filterParser.parse();

            // Then
            assertEquals(1, result.size());
            FilterWrapper wrapper = result.get(0);
            assertEquals("user__manager__name", wrapper.field());
            assertEquals("user__manager__name__icontains", wrapper.originalFieldName());
            assertEquals(Operator.ICONTAINS, wrapper.operator());
            assertEquals(List.of("Smith"), wrapper.values());
        }

        @Test
        void shouldParseMultipleFilters() {
            // Given
            Map<String, String[]> params = new HashMap<>();
            params.put("name__startsWith", new String[]{"J"});
            params.put("age__gte", new String[]{"21"});
            params.put("active", new String[]{"true"});
            when(request.getParameterMap()).thenReturn(params);

            // When
            List<FilterWrapper> result = filterParser.parse();

            // Then
            assertEquals(3, result.size());
            assertTrue(result.stream().anyMatch(w ->
                    w.field().equals("name") &&
                            w.operator() == Operator.STARTS_WITH));
            assertTrue(result.stream().anyMatch(w ->
                    w.field().equals("age") &&
                            w.operator() == Operator.GTE));
            assertTrue(result.stream().anyMatch(w ->
                    w.field().equals("active") &&
                            w.operator() == Operator.EQ));
        }
    }

    @Nested
    class ValueParsing {

        @Test
        void shouldParseCommaSeparatedValues() {
            // Given
            Map<String, String[]> params = new HashMap<>();
            params.put("status__in", new String[]{"active,pending,suspended"});
            when(request.getParameterMap()).thenReturn(params);

            // When
            List<FilterWrapper> result = filterParser.parse();

            // Then
            assertEquals(1, result.size());
            FilterWrapper wrapper = result.get(0);
            assertEquals("status", wrapper.field());
            assertEquals(Operator.IN, wrapper.operator());
            assertEquals(List.of("active", "pending", "suspended"), wrapper.values());
        }

        @Test
        void shouldHandleEmptyValue() {
            // Given
            Map<String, String[]> params = new HashMap<>();
            params.put("name__isNull", new String[]{""});
            when(request.getParameterMap()).thenReturn(params);

            // When
            List<FilterWrapper> result = filterParser.parse();

            // Then
            assertEquals(1, result.size());
            FilterWrapper wrapper = result.get(0);
            assertEquals("name", wrapper.field());
            assertEquals(Operator.IS_NULL, wrapper.operator());
            assertEquals(List.of(""), wrapper.values());
        }
    }

    @Nested
    class EdgeCases {

        @Test
        void shouldHandleInvalidOperator() {
            // Given
            Map<String, String[]> params = new HashMap<>();
            params.put("age__invalid", new String[]{"25"});
            when(request.getParameterMap()).thenReturn(params);

            // When
            List<FilterWrapper> result = filterParser.parse();

            // Then
            assertEquals(1, result.size());
            FilterWrapper wrapper = result.get(0);
            assertEquals("age__invalid", wrapper.field());
            assertEquals(Operator.EQ, wrapper.operator());
            assertEquals(List.of("25"), wrapper.values());
        }

        @Test
        void shouldHandleMultipleDelimiters() {
            // Given
            Map<String, String[]> params = new HashMap<>();
            params.put("user__profile____bio", new String[]{"text"});
            when(request.getParameterMap()).thenReturn(params);

            // When
            List<FilterWrapper> result = filterParser.parse();

            // Then
            assertEquals(1, result.size());
            FilterWrapper wrapper = result.get(0);
            assertEquals("user__profile____bio", wrapper.field());
            assertEquals(Operator.EQ, wrapper.operator());
            assertEquals(List.of("text"), wrapper.values());
        }

        @Test
        void shouldHandleFieldDelimiterChange() {
            ReflectionTestUtils.setField(filterParser, "FIELD_DELIMITER", "##");

            // Given
            Map<String, String[]> params = new HashMap<>();
            params.put("user##profile##bio", new String[]{"text"});
            when(request.getParameterMap()).thenReturn(params);

            // When
            List<FilterWrapper> result = filterParser.parse();

            // Then
            assertEquals(1, result.size());
            FilterWrapper wrapper = result.get(0);
            assertEquals("user##profile##bio", wrapper.field());
            assertEquals(Operator.EQ, wrapper.operator());
            assertEquals(List.of("text"), wrapper.values());
        }
    }
}