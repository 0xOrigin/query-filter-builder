package io.github._0xorigin.queryfilterbuilder.base.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Represents a request body for APIs that support filtering and sorting.
 * It contains lists of {@link FilterRequest} and {@link SortRequest} objects.
 *
 * @param filters A list of filter criteria to apply. Can be null or empty.
 * @param sorts   A list of sort criteria to apply. Can be null or empty.
 */
@Validated
public record ListAPIRequest(
    @Valid
    @JsonProperty("filters")
    List<FilterRequest> filters,

    @Valid
    @JsonProperty("sorts")
    List<SortRequest> sorts
) {}
