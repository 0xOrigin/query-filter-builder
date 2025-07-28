package io.github._0xorigin.queryfilterbuilder.base.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public record ListAPIRequest(
    @Valid
    @JsonProperty("filters")
    List<FilterRequest> filters,

    @Valid
    @JsonProperty("sorts")
    List<SortRequest> sorts
) {}
