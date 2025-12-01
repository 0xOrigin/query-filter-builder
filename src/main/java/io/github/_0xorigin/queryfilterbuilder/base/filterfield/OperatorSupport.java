package io.github._0xorigin.queryfilterbuilder.base.filterfield;

import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * An abstract base class that provides support for managing a set of operators.
 */
public abstract class OperatorSupport {

    private final Set<Operator> supportedOperators = EnumSet.noneOf(Operator.class);

    /**
     * Returns an unmodifiable view of the supported operators set.
     *
     * @return A set of supported operators.
     */
    public Set<Operator> getSupportedOperators() {
        return Collections.unmodifiableSet(supportedOperators);
    }

    /**
     * Sets the supported operators for this instance.
     * This method is intended to be called by the constructors of subclasses to configure the allowed operators.
     *
     * @param operators A set of operators to be supported.
     */
    protected void setSupportedOperators(Set<Operator> operators) {
        supportedOperators.addAll(operators);
    }
}
