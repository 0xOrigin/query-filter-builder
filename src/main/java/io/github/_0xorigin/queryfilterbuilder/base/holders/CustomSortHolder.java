package io.github._0xorigin.queryfilterbuilder.base.holders;

import io.github._0xorigin.queryfilterbuilder.base.enums.SourceType;
import io.github._0xorigin.queryfilterbuilder.base.functions.CustomSortFunction;

import java.util.Set;

public record CustomSortHolder<T>(
    CustomSortFunction<T> customSortFunction,
    Set<SourceType> sourceTypes
) {}
