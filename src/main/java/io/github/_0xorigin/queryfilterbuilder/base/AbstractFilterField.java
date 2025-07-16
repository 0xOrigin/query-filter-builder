package io.github._0xorigin.queryfilterbuilder.base;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractFilterField<T extends Comparable<? super T> & Serializable> implements FilterField<T>, FieldCaster<T> {

    private final Set<Operator> supportedOperators = new HashSet<>();

    @Override
    public Set<Operator> getSupportedOperators() {
        return supportedOperators;
    }

    protected void setSupportedOperators(Set<Operator> operators) {
        supportedOperators.addAll(operators);
    }
}
