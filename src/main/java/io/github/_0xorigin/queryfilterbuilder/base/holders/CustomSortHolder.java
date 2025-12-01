package io.github._0xorigin.queryfilterbuilder.base.holders;

import io.github._0xorigin.queryfilterbuilder.base.enums.SourceType;
import io.github._0xorigin.queryfilterbuilder.base.functions.CustomSortFunction;

import java.util.Set;

/**
 * A holder for the configuration of a custom sort.
 *
 * @param customSortFunction The function that implements the custom sort logic.
 * @param sourceTypes        The set of allowed sources (e.g., query parameter, request body) for this sort.
 * @param <T>                The type of the root entity.
 */
public record CustomSortHolder<T>(
    CustomSortFunction<T> customSortFunction,
    Set<SourceType> sourceTypes
) {}
