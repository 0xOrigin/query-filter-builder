package io.github._0xorigin.queryfilterbuilder.base.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;

@Validated
public record SortRequest(
    @NotBlank String field,
    @NotNull Sort.Direction direction
) {
    public SortRequest {
        if (direction == null) {
            direction = Sort.Direction.ASC;
        }
    }
}
