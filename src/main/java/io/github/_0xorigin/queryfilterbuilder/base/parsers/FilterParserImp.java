package io.github._0xorigin.queryfilterbuilder.base.parsers;

import io.github._0xorigin.queryfilterbuilder.base.dtos.FilterRequest;
import io.github._0xorigin.queryfilterbuilder.base.enums.SourceType;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;
import io.github._0xorigin.queryfilterbuilder.base.utils.FilterUtils;
import io.github._0xorigin.queryfilterbuilder.base.wrappers.FilterWrapper;
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

    @Override
    public List<FilterWrapper> parse(@NonNull final HttpServletRequest httpServletRequest) {
        Objects.requireNonNull(httpServletRequest, "httpServletRequest cannot be null");
        final String FIELD_DELIMITER = properties.defaults().fieldDelimiter();
        return getRequestQueryParams(httpServletRequest)
                .entrySet()
                .stream()
                .map(entry -> {
                    String paramName = entry.getKey(); // e.g., "user.manager.name.icontains" or "user.manager.name"
                    String paramValue = getParamValue(entry.getValue());

                    String[] parts = FilterUtils.splitWithEscapedDelimiter(paramName, FIELD_DELIMITER);
                    String operatorPart = parts[parts.length - 1];

                    Operator operator;
                    String fieldPath;

                    Optional<Operator> operatorOptional = Operator.fromValue(operatorPart);
                    if (operatorOptional.isPresent()) {
                        operator = operatorOptional.get();
                        fieldPath = String.join(FIELD_DELIMITER, Arrays.copyOf(parts, parts.length - 1));
                    } else {
                        // If not a valid operator, treat the whole as a field with EQUAL as the default operator
                        operator = Operator.EQ;
                        fieldPath = paramName; // Entire paramName is treated as the field
                    }

                    return new FilterWrapper(
                        fieldPath,
                        paramName,
                        operator,
                        Arrays.stream(paramValue.split(",")).toList(),
                        SourceType.QUERY_PARAM,
                        Optional.empty()
                    );
                })
                .toList();
    }

    @Override
    public List<FilterWrapper> parse(@NonNull final List<FilterRequest> filterRequests) {
        Objects.requireNonNull(filterRequests, "filterRequests cannot be null");
        return filterRequests
                .stream()
                .filter(filterRequest -> filterRequest.field() != null && !filterRequest.field().isBlank())
                .map(filterRequest -> {
                    Operator operator = Operator.fromValue(filterRequest.operator())
                            .orElse(Operator.EQ); // If not a valid operator, treat the whole as a field with EQUAL as the default operator
                    return new FilterWrapper(
                        filterRequest.field(),
                        filterRequest.field(),
                        operator,
                        Arrays.stream(String.valueOf(filterRequest.value()).split(",")).toList(),
                        SourceType.REQUEST_BODY,
                        Optional.empty()
                    );
                })
                .toList();
    }

    private Map<String, String[]> getRequestQueryParams(HttpServletRequest request) {
        return request.getParameterMap();
    }

    private String getParamValue(String[] values) {
        return values.length > 0 ? values[0] : "";
    }
}
