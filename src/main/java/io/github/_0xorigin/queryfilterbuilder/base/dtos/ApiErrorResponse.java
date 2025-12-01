package io.github._0xorigin.queryfilterbuilder.base.dtos;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

/**
 * Represents a standardized error response for the API.
 *
 * @param timestamp The date and time when the error occurred.
 * @param message   A general message describing the error.
 * @param path      The API path that was requested.
 * @param errors    A map containing detailed error messages, typically keyed by field name.
 */
public record ApiErrorResponse(
    OffsetDateTime timestamp,
    String message,
    String path,
    Map<String, List<String>> errors
) {
    /**
     * Constructs an ApiErrorResponse with the current timestamp.
     *
     * @param message A general message describing the error.
     * @param path    The API path that was requested.
     * @param errors  A map containing detailed error messages.
     */
    public ApiErrorResponse(String message, String path, Map<String, List<String>> errors) {
        this(OffsetDateTime.now(), message, path, errors);
    }
}
