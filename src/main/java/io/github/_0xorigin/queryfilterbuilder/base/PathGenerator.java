package io.github._0xorigin.queryfilterbuilder.base;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;

import java.io.Serializable;

public interface PathGenerator<T> {

    <K extends Comparable<? super K> & Serializable> Expression<K> generate(Root<T> root, String field, ErrorWrapper errorWrapper);

}
