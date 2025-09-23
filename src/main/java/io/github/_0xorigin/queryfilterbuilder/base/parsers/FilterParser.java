package io.github._0xorigin.queryfilterbuilder.base.parsers;

import io.github._0xorigin.queryfilterbuilder.base.dtos.FilterRequest;
import io.github._0xorigin.queryfilterbuilder.base.wrappers.FilterWrapper;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * Defines the contract for parsing filter information from different sources.
 * Implementations are responsible for converting raw input into a standardized list of {@link FilterWrapper}s.
 */
public interface FilterParser {

    /**
     * Parses filter information from the query parameters of an {@link HttpServletRequest}.
     *
     * @param request The incoming HTTP request. Must not be null.
     * @return A list of {@link FilterWrapper}s representing the parsed filter criteria.
     */
    List<FilterWrapper> parse(HttpServletRequest request);

    /**
     * Parses filter information from a list of {@link FilterRequest} DTOs, typically from a request body.
     *
     * @param filterRequests The list of filter request objects. Must not be null.
     * @return A list of {@link FilterWrapper}s representing the parsed filter criteria.
     */
    List<FilterWrapper> parse(List<FilterRequest> filterRequests);

}
