package io.github._0xorigin.queryfilterbuilder.base.wrappers;

import io.github._0xorigin.queryfilterbuilder.base.enums.FilterType;
import io.github._0xorigin.queryfilterbuilder.base.enums.SourceType;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;

import java.util.List;
import java.util.Optional;

public record FilterWrapper (
    String field,
    String originalFieldName,
    Operator operator,
    List<String> values,
    SourceType sourceType,
    Optional<FilterType> filterType
) {
    public FilterWrapper withFilterType(final FilterType filterType) {
        return new FilterWrapper(field, originalFieldName, operator, values, sourceType, Optional.ofNullable(filterType));
    }
}
