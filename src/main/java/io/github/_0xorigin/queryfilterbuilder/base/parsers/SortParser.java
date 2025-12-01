package io.github._0xorigin.queryfilterbuilder.base.parsers;

import io.github._0xorigin.queryfilterbuilder.base.dtos.SortRequest;
import io.github._0xorigin.queryfilterbuilder.base.wrappers.SortWrapper;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * Defines the contract for parsing sort information from different sources.
 * Implementations are responsible for converting raw input into a standardized list of {@link SortWrapper}s.
 */
public interface SortParser {

    /**
     * Parses sort information from the query parameters of an {@link HttpServletRequest}.
     *
     * @param request The incoming HTTP request. Must not be null.
     * @return A list of {@link SortWrapper}s representing the parsed sort criteria.
     */
    List<SortWrapper> parse(HttpServletRequest request);

    /**
     * Parses sort information from a list of {@link SortRequest} DTOs, typically from a request body.
     *
     * @param sortRequests The list of sort request objects. Must not be null.
     * @return A list of {@link SortWrapper}s representing the parsed sort criteria.
     */
    List<SortWrapper> parse(List<SortRequest> sortRequests);

}
