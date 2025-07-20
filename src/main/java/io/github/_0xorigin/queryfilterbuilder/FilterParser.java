package io.github._0xorigin.queryfilterbuilder;

import io.github._0xorigin.queryfilterbuilder.base.Parser;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;
import io.github._0xorigin.queryfilterbuilder.base.util.FilterUtils;
import io.github._0xorigin.queryfilterbuilder.base.wrapper.FilterWrapper;
import io.github._0xorigin.queryfilterbuilder.configs.QueryFilterBuilderProperties;
import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class FilterParser implements Parser {

    private final QueryFilterBuilderProperties properties;

    public FilterParser(QueryFilterBuilderProperties properties) {
        this.properties = properties;
    }

    public Map<String, String[]> getRequestQueryParams(HttpServletRequest request) {
        return request.getParameterMap();
    }

    @Override
    public List<FilterWrapper> parse(HttpServletRequest request) {
        List<FilterWrapper> wrapperList = new ArrayList<>();

        for (Map.Entry<String, String[]> entry : getRequestQueryParams(request).entrySet()) {
            String paramName = entry.getKey(); // e.g., "user__manager__name__icontains" or "user__manager__name"
            String paramValue = Arrays.stream(entry.getValue()).toList().get(0);
            final String FIELD_DELIMITER = properties.queryParam().defaults().fieldDelimiter();

            String[] parts = FilterUtils.splitWithEscapedDelimiter(paramName, FIELD_DELIMITER);
            String operatorPart = parts[parts.length - 1]; // The last part might be the operator

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

            wrapperList.add(new FilterWrapper(fieldPath, paramName, operator, Arrays.stream(paramValue.split(",")).toList()));
        }

        return wrapperList;
    }
}
