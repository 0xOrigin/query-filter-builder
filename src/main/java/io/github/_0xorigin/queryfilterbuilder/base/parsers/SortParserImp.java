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

/**
 * The default implementation of {@link SortParser}.
 */
public final class SortParserImp implements SortParser {

    private final QueryFilterBuilderProperties properties;
    private final Logger log = LoggerFactory.getLogger(SortParserImp.class);

    /**
     * Constructs a new SortParserImp.
     *
     * @param properties Configuration properties for the query builder, used to determine the sort parameter name.
     */
    public SortParserImp(QueryFilterBuilderProperties properties) {
        this.properties = properties;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation parses a specific query parameter (defined in properties, e.g., "sort")
     * from the {@link HttpServletRequest}. It expects a comma-separated list of fields.
     * A field prefixed with a hyphen (e.g., "-name") is treated as a descending sort, otherwise it is ascending.
     */
    @Override
    public List<SortWrapper> parse(@NonNull final HttpServletRequest request) {
        Objects.requireNonNull(request, "request cannot be null");
        final String sort = getSortParamValue(request);

        return Arrays.stream(sort.split(","))
                .filter(sortItem -> !sortItem.isBlank())
                .map(sortItem -> {
                    boolean isDescending = sortItem.startsWith("-");
                    Sort.Direction direction = isDescending ? Sort.Direction.DESC : Sort.Direction.ASC;
                    String field = isDescending ? sortItem.substring(1) : sortItem;
                    return new SortWrapper(field, sortItem, direction, SourceType.QUERY_PARAM, Optional.empty());
                })
                .toList();
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation iterates through the provided list of {@link SortRequest} objects.
     * It creates a {@link SortWrapper} for each valid request, defaulting to ascending order if the
     * provided direction is invalid.
     */
    @Override
    public List<SortWrapper> parse(@NonNull final List<SortRequest> sortRequests) {
        Objects.requireNonNull(sortRequests, "sortRequests cannot be null");
        return sortRequests
                .stream()
                .filter(sortRequest -> sortRequest.field() != null && !sortRequest.field().isBlank())
                .map(sortRequest -> {
                    Sort.Direction direction;
                    try {
                        direction = Sort.Direction.valueOf(String.valueOf(sortRequest.direction()).toUpperCase(Locale.US));
                    } catch (IllegalArgumentException e) {
                        // If the direction is not valid, default to ASC
                        direction = Sort.Direction.ASC;
                    }
                    return new SortWrapper(
                        sortRequest.field(),
                        sortRequest.field(),
                        direction,
                        SourceType.REQUEST_BODY,
                        Optional.empty()
                    );
                })
                .toList();
    }

    private Map<String, String[]> getRequestQueryParams(HttpServletRequest request) {
        return request.getParameterMap();
    }

    private String getSortParamValue(HttpServletRequest request) {
        final String SORT_PARAMETER = properties.queryParam().defaults().sortParameter();
        final String[] sortValues = getRequestQueryParams(request).getOrDefault(SORT_PARAMETER, new String[0]);
        return sortValues.length > 0 ? sortValues[0] : "";
    }
}
