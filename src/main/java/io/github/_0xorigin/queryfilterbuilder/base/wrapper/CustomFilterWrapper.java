package io.github._0xorigin.queryfilterbuilder.base.wrapper;

import io.github._0xorigin.queryfilterbuilder.base.function.CustomFilterFunction;

import java.io.Serializable;

public record CustomFilterWrapper<T, K extends Comparable<? super K> & Serializable> (
    Class<K> dataType,
    CustomFilterFunction<T> customFilterFunction
) {}
