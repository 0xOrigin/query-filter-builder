package io.github._0xorigin.queryfilterbuilder.base.dtos;

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
}
