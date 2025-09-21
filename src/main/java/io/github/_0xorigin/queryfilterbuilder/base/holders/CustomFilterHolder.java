package io.github._0xorigin.queryfilterbuilder.base.holders;

import io.github._0xorigin.queryfilterbuilder.base.enums.SourceType;
import io.github._0xorigin.queryfilterbuilder.base.functions.CustomFilterFunction;

import java.io.Serializable;
import java.util.Set;

/**
 * A holder for the configuration of a custom filter.
 *
 * @param dataType             The expected data type of the input value for this filter.
 * @param customFilterFunction The function that implements the custom filter logic.
 * @param sourceTypes          The set of allowed sources (e.g., query parameter, request body) for this filter.
 * @param <T>                  The type of the root entity.
 * @param <K>                  The expected data type of the input value.
 */
public record CustomFilterHolder<T, K extends Comparable<? super K> & Serializable> (
    Class<K> dataType,
    CustomFilterFunction<T> customFilterFunction,
    Set<SourceType> sourceTypes
) {}
