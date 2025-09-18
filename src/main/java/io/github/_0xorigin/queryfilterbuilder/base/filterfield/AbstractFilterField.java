package io.github._0xorigin.queryfilterbuilder.base.filterfield;

import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;

import java.io.Serializable;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * An abstract base class for implementing {@link FilterField} and {@link FieldCaster}.
 * It provides a common structure for defining supported operators.
 *
 * @param <T> The Java type that this filter field represents.
 */
public abstract class AbstractFilterField<T extends Comparable<? super T> & Serializable> implements FilterField<T>, FieldCaster<T> {

    private final Set<Operator> supportedOperators = EnumSet.noneOf(Operator.class);

    /**
     * {@inheritDoc}
     * <p>
     * This implementation returns an unmodifiable view of the supported operators set.
     */
    @Override
    public Set<Operator> getSupportedOperators() {
        return Collections.unmodifiableSet(supportedOperators);
    }

    /**
     * Sets the supported operators for this filter field.
     * This method is intended to be called by the constructors of subclasses to configure the allowed operators.
     *
     * @param operators A set of operators to be supported.
     */
    protected void setSupportedOperators(Set<Operator> operators) {
        supportedOperators.addAll(operators);
    }
}
