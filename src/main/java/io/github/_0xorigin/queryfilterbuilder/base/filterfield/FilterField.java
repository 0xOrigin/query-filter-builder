package io.github._0xorigin.queryfilterbuilder.base.filterfield;

import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;

import java.io.Serializable;
import java.util.Set;

/**
 * Defines the contract for a filterable field type.
 * Each implementation represents a specific data type (e.g., String, Integer) and specifies which filter operators are supported for it.
 *
 * @param <T> The Java type that this filter field represents (e.g., String, BigDecimal).
 */
public interface FilterField<T extends Comparable<? super T> & Serializable> {

    /**
     * Gets the class of the data type handled by this filter field.
     *
     * @return The {@link Class} object for the data type.
     */
    Class<T> getDataType();

    /**
     * Gets the set of operators that are supported for this data type.
     *
     * @return A {@link Set} of supported {@link Operator}s.
     */
    Set<Operator> getSupportedOperators();
}
