package io.github._0xorigin.queryfilterbuilder.base;

import io.github._0xorigin.queryfilterbuilder.base.wrappers.ErrorWrapper;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;

import java.io.Serializable;

@FunctionalInterface
public interface PathGenerator<T> {

    <K extends Comparable<? super K> & Serializable> Expression<K> generate(Root<T> root, String field, ErrorWrapper errorWrapper);

}
