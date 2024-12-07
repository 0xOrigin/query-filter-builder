package io.github._0xorigin;

import io.github._0xorigin.base.FilterWrapper;
import io.github._0xorigin.base.Operator;
import io.github._0xorigin.base.Parser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FilterParser implements Parser {

    private final HttpServletRequest request;

    @Value("${query-filter-builder.defaults.field-delimiter:__}")
    private String FIELD_DELIMITER;

    public FilterParser(
        HttpServletRequest request
    ) {
        this.request = request;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public Map<String, String[]> getRequestQueryParams() {
        return getRequest().getParameterMap();
    }

    @Override
    public List<FilterWrapper> parse() {
        List<FilterWrapper> wrapperList = new ArrayList<>();

        for (Map.Entry<String, String[]> entry : getRequestQueryParams().entrySet()) {
            String paramName = entry.getKey(); // e.g., "user__manager__name__icontains" or "user__manager__name"
            String paramValue = Arrays.stream(entry.getValue()).toList().get(0);

            String[] parts = paramName.split(FIELD_DELIMITER);
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
