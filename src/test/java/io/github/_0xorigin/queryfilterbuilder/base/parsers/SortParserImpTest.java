package io.github._0xorigin.queryfilterbuilder.base.parsers;

import io.github._0xorigin.queryfilterbuilder.base.dtos.SortRequest;
import io.github._0xorigin.queryfilterbuilder.base.enums.SourceType;
import io.github._0xorigin.queryfilterbuilder.base.wrappers.SortWrapper;
import io.github._0xorigin.queryfilterbuilder.configs.QueryFilterBuilderProperties;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SortParserImpTest {

    @Mock
    private QueryFilterBuilderProperties properties;

    @Mock
    private QueryFilterBuilderProperties.QueryParam queryParam;

    @Mock
    private QueryFilterBuilderProperties.QueryParamDefaults defaults;

    @Mock
    private HttpServletRequest mockRequest;

    @InjectMocks
    private SortParserImp sortParser;

    @Test
    void testParseQueryParamsWithAscendingSort() {
        Map<String, String[]> paramMap = new HashMap<>();
        paramMap.put("sort", new String[]{"name"});
        when(properties.queryParam()).thenReturn(queryParam);
        when(queryParam.defaults()).thenReturn(defaults);
        when(defaults.sortParameter()).thenReturn("sort");
        when(mockRequest.getParameterMap()).thenReturn(paramMap);

        List<SortWrapper> result = sortParser.parse(mockRequest);

        assertThat(result).hasSize(1);
        SortWrapper wrapper = result.get(0);
        assertThat(wrapper.field()).isEqualTo("name");
        assertThat(wrapper.originalFieldName()).isEqualTo("name");
        assertThat(wrapper.direction()).isEqualTo(Sort.Direction.ASC);
        assertThat(wrapper.sourceType()).isEqualTo(SourceType.QUERY_PARAM);
    }

    @Test
    void testParseQueryParamsWithDescendingSort() {
        Map<String, String[]> paramMap = new HashMap<>();
        paramMap.put("sort", new String[]{"-name"});
        when(properties.queryParam()).thenReturn(queryParam);
        when(queryParam.defaults()).thenReturn(defaults);
        when(defaults.sortParameter()).thenReturn("sort");
        when(mockRequest.getParameterMap()).thenReturn(paramMap);

        List<SortWrapper> result = sortParser.parse(mockRequest);

        assertThat(result).hasSize(1);
        SortWrapper wrapper = result.get(0);
        assertThat(wrapper.field()).isEqualTo("name");
        assertThat(wrapper.originalFieldName()).isEqualTo("-name");
        assertThat(wrapper.direction()).isEqualTo(Sort.Direction.DESC);
        assertThat(wrapper.sourceType()).isEqualTo(SourceType.QUERY_PARAM);
    }

    @Test
    void testParseQueryParamsWithMultipleSortFields() {
        Map<String, String[]> paramMap = new HashMap<>();
        paramMap.put("sort", new String[]{"name,-age"});
        when(properties.queryParam()).thenReturn(queryParam);
        when(queryParam.defaults()).thenReturn(defaults);
        when(defaults.sortParameter()).thenReturn("sort");
        when(mockRequest.getParameterMap()).thenReturn(paramMap);

        List<SortWrapper> result = sortParser.parse(mockRequest);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).field()).isEqualTo("name");
        assertThat(result.get(0).originalFieldName()).isEqualTo("name");
        assertThat(result.get(0).direction()).isEqualTo(Sort.Direction.ASC);
        assertThat(result.get(0).sourceType()).isEqualTo(SourceType.QUERY_PARAM);

        assertThat(result.get(1).field()).isEqualTo("age");
        assertThat(result.get(1).originalFieldName()).isEqualTo("-age");
        assertThat(result.get(1).direction()).isEqualTo(Sort.Direction.DESC);
        assertThat(result.get(1).sourceType()).isEqualTo(SourceType.QUERY_PARAM);
    }

    @Test
    void testParseQueryParamsWithEmptySortValue() {
        Map<String, String[]> paramMap = new HashMap<>();
        paramMap.put("sort", new String[]{""});
        when(properties.queryParam()).thenReturn(queryParam);
        when(queryParam.defaults()).thenReturn(defaults);
        when(defaults.sortParameter()).thenReturn("sort");
        when(mockRequest.getParameterMap()).thenReturn(paramMap);

        List<SortWrapper> result = sortParser.parse(mockRequest);

        assertThat(result).isEmpty();
    }

    @Test
    void testParseQueryParamsWithBlankSortValue() {
        Map<String, String[]> paramMap = new HashMap<>();
        paramMap.put("sort", new String[]{" , "});
        when(properties.queryParam()).thenReturn(queryParam);
        when(queryParam.defaults()).thenReturn(defaults);
        when(defaults.sortParameter()).thenReturn("sort");
        when(mockRequest.getParameterMap()).thenReturn(paramMap);

        List<SortWrapper> result = sortParser.parse(mockRequest);

        assertThat(result).isEmpty();
    }

    @Test
    void testParseQueryParamsWithMissingSortParameter() {
        when(properties.queryParam()).thenReturn(queryParam);
        when(queryParam.defaults()).thenReturn(defaults);
        when(defaults.sortParameter()).thenReturn("sort");
        when(mockRequest.getParameterMap()).thenReturn(new HashMap<>());

        List<SortWrapper> result = sortParser.parse(mockRequest);

        assertThat(result).isEmpty();
    }

    @Test
    void testParseQueryParamsWithNullRequest() {
        assertThatThrownBy(() -> sortParser.parse((HttpServletRequest) null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("request cannot be null");
    }

    @ParameterizedTest
    @ValueSource(strings = {"sort", "ordering", "orders"})
    void testParseQueryParamsWithDifferentSortParameters(String sortParam) {
        when(properties.queryParam()).thenReturn(queryParam);
        when(queryParam.defaults()).thenReturn(defaults);
        when(properties.queryParam().defaults().sortParameter()).thenReturn(sortParam);

        Map<String, String[]> paramMap = new HashMap<>();
        paramMap.put(sortParam, new String[]{"name,-age"});
        when(mockRequest.getParameterMap()).thenReturn(paramMap);

        List<SortWrapper> result = sortParser.parse(mockRequest);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).field()).isEqualTo("name");
        assertThat(result.get(0).originalFieldName()).isEqualTo("name");
        assertThat(result.get(0).direction()).isEqualTo(Sort.Direction.ASC);
        assertThat(result.get(0).sourceType()).isEqualTo(SourceType.QUERY_PARAM);

        assertThat(result.get(1).field()).isEqualTo("age");
        assertThat(result.get(1).originalFieldName()).isEqualTo("-age");
        assertThat(result.get(1).direction()).isEqualTo(Sort.Direction.DESC);
        assertThat(result.get(1).sourceType()).isEqualTo(SourceType.QUERY_PARAM);
    }

    @Test
    void testParseSortRequestsWithValidAscendingDirection() {
        SortRequest request = new SortRequest("name", Sort.Direction.ASC);
        List<SortWrapper> result = sortParser.parse(List.of(request));

        assertThat(result).hasSize(1);
        SortWrapper wrapper = result.get(0);
        assertThat(wrapper.field()).isEqualTo("name");
        assertThat(wrapper.originalFieldName()).isEqualTo("name");
        assertThat(wrapper.direction()).isEqualTo(Sort.Direction.ASC);
        assertThat(wrapper.sourceType()).isEqualTo(SourceType.REQUEST_BODY);
    }

    @Test
    void testParseSortRequestsWithValidDescendingDirection() {
        SortRequest request = new SortRequest("name", Sort.Direction.DESC);
        List<SortWrapper> result = sortParser.parse(List.of(request));

        assertThat(result).hasSize(1);
        SortWrapper wrapper = result.get(0);
        assertThat(wrapper.field()).isEqualTo("name");
        assertThat(wrapper.originalFieldName()).isEqualTo("name");
        assertThat(wrapper.direction()).isEqualTo(Sort.Direction.DESC);
        assertThat(wrapper.sourceType()).isEqualTo(SourceType.REQUEST_BODY);
    }

    @Test
    void testParseSortRequestsWithNullDirection() {
        SortRequest request = new SortRequest("name", null);
        List<SortWrapper> result = sortParser.parse(List.of(request));

        assertThat(result).hasSize(1);
        SortWrapper wrapper = result.get(0);
        assertThat(wrapper.field()).isEqualTo("name");
        assertThat(wrapper.originalFieldName()).isEqualTo("name");
        assertThat(wrapper.direction()).isEqualTo(Sort.Direction.ASC); // Defaults to ASC in SortRequest
        assertThat(wrapper.sourceType()).isEqualTo(SourceType.REQUEST_BODY);
    }

    @Test
    void testParseSortRequestsWithNullField() {
        SortRequest request = new SortRequest(null, Sort.Direction.ASC);
        List<SortWrapper> result = sortParser.parse(List.of(request));

        assertThat(result).isEmpty();
    }

    @Test
    void testParseSortRequestsWithBlankField() {
        SortRequest request = new SortRequest(" ", Sort.Direction.ASC);
        List<SortWrapper> result = sortParser.parse(List.of(request));

        assertThat(result).isEmpty();
    }

    @Test
    void testParseSortRequestsWithMultipleRequests() {
        List<SortRequest> requests = List.of(
            new SortRequest("name", Sort.Direction.ASC),
            new SortRequest("age", Sort.Direction.DESC)
        );
        List<SortWrapper> result = sortParser.parse(requests);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).field()).isEqualTo("name");
        assertThat(result.get(0).originalFieldName()).isEqualTo("name");
        assertThat(result.get(0).direction()).isEqualTo(Sort.Direction.ASC);
        assertThat(result.get(0).sourceType()).isEqualTo(SourceType.REQUEST_BODY);

        assertThat(result.get(1).field()).isEqualTo("age");
        assertThat(result.get(1).originalFieldName()).isEqualTo("age");
        assertThat(result.get(1).direction()).isEqualTo(Sort.Direction.DESC);
        assertThat(result.get(1).sourceType()).isEqualTo(SourceType.REQUEST_BODY);
    }

    @Test
    void testParseSortRequestsWithEmptyList() {
        List<SortWrapper> result = sortParser.parse(new ArrayList<>());
        assertThat(result).isEmpty();
    }

    @Test
    void testParseSortRequestsWithNullList() {
        assertThat(sortParser.parse((List<SortRequest>) null))
            .isEmpty();
    }
}
