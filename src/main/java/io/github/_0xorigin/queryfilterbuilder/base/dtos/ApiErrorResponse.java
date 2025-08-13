package io.github._0xorigin.queryfilterbuilder.base.dtos;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public record ApiErrorResponse(
    OffsetDateTime timestamp,
    String message,
    String path,
    Map<String, List<String>> errors
) {
    public ApiErrorResponse(String message, String path, Map<String, List<String>> errors) {
        this(OffsetDateTime.now(), message, path, errors);
    }
}
