package io.github._0xorigin.queryfilterbuilder.base.wrappers;

import io.github._0xorigin.queryfilterbuilder.base.enums.SortType;
import io.github._0xorigin.queryfilterbuilder.base.enums.SourceType;
import org.springframework.data.domain.Sort;

import java.util.Optional;

public record SortWrapper(
    String field,
    String originalFieldName,
    Sort.Direction direction,
    SourceType sourceType,
    Optional<SortType> sortType
) {
    public SortWrapper withSortType(final SortType sortType) {
        return new SortWrapper(field, originalFieldName, direction, sourceType, Optional.ofNullable(sortType));
    }
}
