package com.github._0xorigin.base;

import java.util.Set;

public interface FilterField<T> {

    Set<Operator> getSupportedOperators();

    T cast(Object value, ErrorWrapper errorWrapper);

}
