package io.github._0xorigin.queryfilterbuilder.base.enumfield;

import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;

import java.util.Set;

/**
 * Defines the contract for a filterable enum type.
 * This interface is used to specify which filter operators are supported for a specific enum type.
 */
public interface EnumFilterField {
    /**
     * Gets the set of operators that are supported for the enum type.
     *
     * @return A {@link Set} of supported {@link Operator}s.
     */
    Set<Operator> getSupportedOperators();
}
