package io.github._0xorigin.queryfilterbuilder.base.holders;

import io.github._0xorigin.queryfilterbuilder.base.enums.SourceType;
import io.github._0xorigin.queryfilterbuilder.base.functions.CustomFilterFunction;

import java.io.Serializable;
import java.util.Set;

public record CustomFilterHolder<T, K extends Comparable<? super K> & Serializable> (
    Class<K> dataType,
    CustomFilterFunction<T> customFilterFunction,
    Set<SourceType> sourceTypes
) {}
