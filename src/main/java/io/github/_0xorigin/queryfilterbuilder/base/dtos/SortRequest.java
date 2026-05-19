package io.github._0xorigin.queryfilterbuilder.base.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;

/**
 * Represents a single sort criterion, typically sent in the body of a request.
 *
 * @param field     The name of the field to sort by. Must not be blank.
 * @param direction The direction of the sort (ASC or DESC). Defaults to ASC if null.
 */
@Validated
public record SortRequest(
    @NotBlank String field,
    @NotNull Sort.Direction direction
) {
    /**
     * Compact constructor for {@link SortRequest}.
     * Ensures that if the direction is not provided, it defaults to ascending.
     */
    public SortRequest {
        if (direction == null) {
            direction = Sort.Direction.ASC;
        }
    }

    /**
     * Factory method for JSON deserialization (via @JsonCreator).
     * Accepts case-insensitive strings ("asc"/"ASC", "desc"/"DESC") and maps to the enum.
     * This overrides the default canonical constructor for Jackson.
     */
    @JsonCreator
    public static SortRequest fromJson(
        @JsonProperty("field") String field,
        @JsonProperty("direction") String directionStr
    ) {
        Sort.Direction direction = Sort.Direction.ASC;
        if (directionStr != null && !directionStr.isBlank()) {
            try {
                direction = Sort.Direction.valueOf(directionStr.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                /* no-op */
            }
        }
        return new SortRequest(field, direction);
    }
}
