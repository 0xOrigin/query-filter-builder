package io.github._0xorigin.queryfilterbuilder.base.parsers;

import io.github._0xorigin.queryfilterbuilder.base.dtos.SortRequest;
import io.github._0xorigin.queryfilterbuilder.base.enums.SourceType;
import io.github._0xorigin.queryfilterbuilder.base.wrappers.SortWrapper;
import io.github._0xorigin.queryfilterbuilder.configs.QueryFilterBuilderProperties;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;

import java.util.*;

public final class SortParserImp implements SortParser {

    private final QueryFilterBuilderProperties properties;
    private final Logger log = LoggerFactory.getLogger(SortParserImp.class);

    public SortParserImp(QueryFilterBuilderProperties properties) {
        this.properties = properties;
    }

    public Map<String, String[]> getRequestQueryParams(HttpServletRequest request) {
        return request.getParameterMap();
    }

    @Override
    public List<SortWrapper> parse(@NonNull final HttpServletRequest request) {
        final String sortParameterName = "sort";
        final Map<String, String[]> requestQueryParams = getRequestQueryParams(request);
        final String sort = requestQueryParams.getOrDefault(sortParameterName, new String[0])[0];

        return Arrays.stream(sort.split(","))
                .map(String::trim)
                .map(sortItem -> {
                    boolean isDescending = sortItem.startsWith("-");
                    Sort.Direction direction = isDescending ? Sort.Direction.DESC : Sort.Direction.ASC;
                    String field = isDescending ? sortItem.substring(1) : sortItem;
                    return new SortWrapper(field, sortItem, direction, SourceType.QUERY_PARAM, Optional.empty());
                })
                .toList();
    }

    @Override
    public List<SortWrapper> parse(@NonNull final List<SortRequest> sortRequests) {
        return List.of();
    }
}
