package io.github._0xorigin.queryfilterbuilder.base.parsers;

import io.github._0xorigin.queryfilterbuilder.base.dtos.FilterRequest;
import io.github._0xorigin.queryfilterbuilder.base.enums.SourceType;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;
import io.github._0xorigin.queryfilterbuilder.base.wrappers.FilterWrapper;
import io.github._0xorigin.queryfilterbuilder.configs.QueryFilterBuilderProperties;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FilterParserImpTest {

    @Mock
    private QueryFilterBuilderProperties properties;

    @Mock
    private QueryFilterBuilderProperties.Defaults defaults;

    @Mock
    private HttpServletRequest mockRequest;

    @InjectMocks
    private FilterParserImp filterParser;

    @ParameterizedTest
    @ValueSource(strings = {"icontains", "iContains", "ICONTAINS", "iCONTAINS", "Icontains", "IContains", "iCONTAINS", "ICONTAINS"})
    void testParseQueryParamsWithValidOperator(String operator) {
        Map<String, String[]> paramMap = new HashMap<>();
        paramMap.put("user.name." + operator, new String[]{"john"});
        when(properties.defaults()).thenReturn(defaults);
        when(defaults.fieldDelimiter()).thenReturn(".");
        when(mockRequest.getParameterMap()).thenReturn(paramMap);

        List<FilterWrapper> result = filterParser.parse(mockRequest);

        assertThat(result).hasSize(1);
        FilterWrapper wrapper = result.get(0);
        assertThat(wrapper.field()).isEqualTo("user.name");
        assertThat(wrapper.originalFieldName()).isEqualTo("user.name." + operator);
        assertThat(wrapper.operator()).isEqualTo(Operator.ICONTAINS);
        assertThat(wrapper.values()).containsExactly("john");
        assertThat(wrapper.sourceType()).isEqualTo(SourceType.QUERY_PARAM);
    }

    @Test
    void testParseQueryParamsWithNoOperator() {
        Map<String, String[]> paramMap = new HashMap<>();
        paramMap.put("user.name", new String[]{"john"});
        when(properties.defaults()).thenReturn(defaults);
        when(defaults.fieldDelimiter()).thenReturn(".");
        when(mockRequest.getParameterMap()).thenReturn(paramMap);

        List<FilterWrapper> result = filterParser.parse(mockRequest);

        assertThat(result).hasSize(1);
        FilterWrapper wrapper = result.get(0);
        assertThat(wrapper.field()).isEqualTo("user.name");
        assertThat(wrapper.originalFieldName()).isEqualTo("user.name");
        assertThat(wrapper.operator()).isEqualTo(Operator.EQ);
        assertThat(wrapper.values()).containsExactly("john");
        assertThat(wrapper.sourceType()).isEqualTo(SourceType.QUERY_PARAM);
    }

    @Test
    void testParseQueryParamsWithInvalidOperator() {
        Map<String, String[]> paramMap = new HashMap<>();
        paramMap.put("user.name.invalid", new String[]{"john"});
        when(properties.defaults()).thenReturn(defaults);
        when(defaults.fieldDelimiter()).thenReturn(".");
        when(mockRequest.getParameterMap()).thenReturn(paramMap);

        List<FilterWrapper> result = filterParser.parse(mockRequest);

        assertThat(result).hasSize(1);
        FilterWrapper wrapper = result.get(0);
        assertThat(wrapper.field()).isEqualTo("user.name.invalid");
        assertThat(wrapper.originalFieldName()).isEqualTo("user.name.invalid");
        assertThat(wrapper.operator()).isEqualTo(Operator.EQ);
        assertThat(wrapper.values()).containsExactly("john");
    }

    @Test
    void testParseQueryParamsWithEmptyValue() {
        Map<String, String[]> paramMap = new HashMap<>();
        paramMap.put("user.name.eq", new String[]{});
        when(properties.defaults()).thenReturn(defaults);
        when(defaults.fieldDelimiter()).thenReturn(".");
        when(mockRequest.getParameterMap()).thenReturn(paramMap);

        List<FilterWrapper> result = filterParser.parse(mockRequest);

        assertThat(result).hasSize(1);
        FilterWrapper wrapper = result.get(0);
        assertThat(wrapper.field()).isEqualTo("user.name");
        assertThat(wrapper.originalFieldName()).isEqualTo("user.name.eq");
        assertThat(wrapper.operator()).isEqualTo(Operator.EQ);
        assertThat(wrapper.values()).containsExactly("");
    }

    @Test
    void testParseQueryParamsWithMultipleValues() {
        Map<String, String[]> paramMap = new HashMap<>();
        paramMap.put("user.name.in", new String[]{"john,jane"});
        when(properties.defaults()).thenReturn(defaults);
        when(defaults.fieldDelimiter()).thenReturn(".");
        when(mockRequest.getParameterMap()).thenReturn(paramMap);

        List<FilterWrapper> result = filterParser.parse(mockRequest);

        assertThat(result).hasSize(1);
        FilterWrapper wrapper = result.get(0);
        assertThat(wrapper.field()).isEqualTo("user.name");
        assertThat(wrapper.originalFieldName()).isEqualTo("user.name.in");
        assertThat(wrapper.operator()).isEqualTo(Operator.IN);
        assertThat(wrapper.values()).containsExactly("john", "jane");
    }

    @Test
    void testParseQueryParamsWithEmptyParamMap() {
        when(properties.defaults()).thenReturn(defaults);
        when(defaults.fieldDelimiter()).thenReturn(".");
        when(mockRequest.getParameterMap()).thenReturn(new HashMap<>());

        List<FilterWrapper> result = filterParser.parse(mockRequest);

        assertThat(result).isEmpty();
    }

    @Test
    void testParseQueryParamsWithNullRequest() {
        assertThatThrownBy(() -> filterParser.parse((HttpServletRequest) null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("httpServletRequest cannot be null");
    }

    @ParameterizedTest
    @ValueSource(strings = {".", "__", ":"})
    void testParseQueryParamsWithDifferentDelimiters(String delimiter) {
        when(properties.defaults()).thenReturn(defaults);
        when(properties.defaults().fieldDelimiter()).thenReturn(delimiter);

        String paramName = "user" + delimiter + "name" + delimiter + "icontains";
        Map<String, String[]> paramMap = new HashMap<>();
        paramMap.put(paramName, new String[]{"john"});
        when(mockRequest.getParameterMap()).thenReturn(paramMap);

        List<FilterWrapper> result = filterParser.parse(mockRequest);

        assertThat(result).hasSize(1);
        FilterWrapper wrapper = result.get(0);
        assertThat(wrapper.field()).isEqualTo("user" + delimiter + "name");
        assertThat(wrapper.originalFieldName()).isEqualTo(paramName);
        assertThat(wrapper.operator()).isEqualTo(Operator.ICONTAINS);
        assertThat(wrapper.values()).containsExactly("john");
    }

    @Test
    void testParseFilterRequestsWithValidOperator() {
        FilterRequest request = new FilterRequest("user.name", "eq", "john");
        List<FilterWrapper> result = filterParser.parse(List.of(request));

        assertThat(result).hasSize(1);
        FilterWrapper wrapper = result.get(0);
        assertThat(wrapper.field()).isEqualTo("user.name");
        assertThat(wrapper.originalFieldName()).isEqualTo("user.name");
        assertThat(wrapper.operator()).isEqualTo(Operator.EQ);
        assertThat(wrapper.values()).containsExactly("john");
        assertThat(wrapper.sourceType()).isEqualTo(SourceType.REQUEST_BODY);
    }

    @Test
    void testParseFilterRequestsWithInvalidOperator() {
        FilterRequest request = new FilterRequest("user.name", "invalid", "john");
        List<FilterWrapper> result = filterParser.parse(List.of(request));

        assertThat(result).hasSize(1);
        FilterWrapper wrapper = result.get(0);
        assertThat(wrapper.field()).isEqualTo("user.name");
        assertThat(wrapper.originalFieldName()).isEqualTo("user.name");
        assertThat(wrapper.operator()).isEqualTo(Operator.EQ);
        assertThat(wrapper.values()).containsExactly("john");
    }

    @Test
    void testParseFilterRequestsWithNullField() {
        FilterRequest request = new FilterRequest(null, "eq", "john");
        List<FilterWrapper> result = filterParser.parse(List.of(request));

        assertThat(result).isEmpty();
    }

    @Test
    void testParseFilterRequestsWithBlankField() {
        FilterRequest request = new FilterRequest(" ", "eq", "john");
        List<FilterWrapper> result = filterParser.parse(List.of(request));

        assertThat(result).isEmpty();
    }

    @Test
    void testParseFilterRequestsWithMultipleValues() {
        FilterRequest request = new FilterRequest("user.name", "in", "john,jane");
        List<FilterWrapper> result = filterParser.parse(List.of(request));

        assertThat(result).hasSize(1);
        FilterWrapper wrapper = result.get(0);
        assertThat(wrapper.field()).isEqualTo("user.name");
        assertThat(wrapper.originalFieldName()).isEqualTo("user.name");
        assertThat(wrapper.operator()).isEqualTo(Operator.IN);
        assertThat(wrapper.values()).containsExactly("john", "jane");
    }

    @Test
    void testParseFilterRequestsWithEmptyList() {
        List<FilterWrapper> result = filterParser.parse(new ArrayList<>());
        assertThat(result).isEmpty();
    }

    @Test
    void testParseFilterRequestsWithNullList() {
        assertThatThrownBy(() -> filterParser.parse((List<FilterRequest>) null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("filterRequests cannot be null");
    }
}
