package io.github._0xorigin.queryfilterbuilder.base;

import java.io.Serializable;
import java.util.Set;

public interface FilterField<T extends Comparable<? super T> & Serializable> {

    Class<T> getDataType();

    Set<Operator> getSupportedOperators();
}
