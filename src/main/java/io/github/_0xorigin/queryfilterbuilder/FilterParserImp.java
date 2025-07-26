package io.github._0xorigin.queryfilterbuilder;

import io.github._0xorigin.queryfilterbuilder.base.FilterParser;
import io.github._0xorigin.queryfilterbuilder.base.dto.FilterRequest;
import io.github._0xorigin.queryfilterbuilder.base.enums.SourceType;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;
import io.github._0xorigin.queryfilterbuilder.base.util.FilterUtils;
import io.github._0xorigin.queryfilterbuilder.base.wrapper.FilterWrapper;
import io.github._0xorigin.queryfilterbuilder.configs.QueryFilterBuilderProperties;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import java.util.*;

public final class FilterParserImp implements FilterParser {
    private final QueryFilterBuilderProperties properties;
    private final Logger log = LoggerFactory.getLogger(FilterParserImp.class);

    public FilterParserImp(QueryFilterBuilderProperties properties) {
        this.properties = properties;
    }

    public Map<String, String[]> getRequestQueryParams(HttpServletRequest request) {
        return request.getParameterMap();
    }

    @Override
    public List<FilterWrapper> parse(@NonNull final HttpServletRequest httpServletRequest) {
        final List<FilterWrapper> wrapperList = new ArrayList<>();

        for (Map.Entry<String, String[]> entry : getRequestQueryParams(httpServletRequest).entrySet()) {
//            log.debug("HttpServletRequest Entry[{}]: {}", entry.getKey(), Arrays.stream(entry.getValue()).toList());
            String paramName = entry.getKey(); // e.g., "user__manager__name__icontains" or "user__manager__name"
            String paramValue = entry.getValue().length > 0 ? entry.getValue()[0] : "";
            final String FIELD_DELIMITER = properties.queryParam().defaults().fieldDelimiter();

            final String[] parts = FilterUtils.splitWithEscapedDelimiter(paramName, FIELD_DELIMITER);
            final String operatorPart = parts[parts.length - 1]; // The last part might be the operator

            Operator operator;
            String fieldPath;

            try {
                operator = Operator.fromValue(operatorPart);
                fieldPath = String.join(FIELD_DELIMITER, Arrays.copyOf(parts, parts.length - 1));
            } catch (IllegalArgumentException e) {
                // If not a valid operator, treat the whole as a field with EQUAL as the default operator
                operator = Operator.EQ;
                fieldPath = paramName; // Entire paramName is treated as the field
            }

            wrapperList.add(new FilterWrapper(
                fieldPath,
                paramName,
                operator,
                Arrays.stream(paramValue.split(",")).toList(),
                SourceType.QUERY_PARAM
            ));
        }

        return wrapperList;
    }

    @Override
    public List<FilterWrapper> parse(@NonNull final List<FilterRequest> filterRequests) {
        final List<FilterWrapper> wrapperList = new ArrayList<>();

        for (FilterRequest filterRequest : filterRequests) {
            Operator operator;
            try {
                operator = Operator.fromValue(filterRequest.operator());
            } catch (IllegalArgumentException e) {
                // If not a valid operator, treat the whole as a field with EQUAL as the default operator
                operator = Operator.EQ;
            }
            wrapperList.add(new FilterWrapper(
                filterRequest.field(),
                filterRequest.field(),
                operator,
                Arrays.stream(String.valueOf(filterRequest.value()).split(",")).toList(),
                SourceType.REQUEST_BODY
            ));
        }
        return wrapperList;
    }
}
