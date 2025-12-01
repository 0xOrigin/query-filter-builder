package io.github._0xorigin.queryfilterbuilder.filters;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractFilterField;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;

import java.util.Set;

/**
 * A filter implementation for handling {@link Character} fields.
 */
public final class CharacterFilter extends AbstractFilterField<Character> {

    /**
     * Constructs the filter and sets the supported operators for character types.
     */
    public CharacterFilter() {
        setSupportedOperators(
            Set.of(
                Operator.EQ, Operator.NEQ, Operator.GT, Operator.LT, Operator.GTE, Operator.LTE,
                Operator.IS_NULL, Operator.IS_NOT_NULL, Operator.IN, Operator.NOT_IN, Operator.BETWEEN, Operator.NOT_BETWEEN
            )
        );
    }

    /**
     * {@inheritDoc}
     * <p>
     * Casts the string to a character by taking the first character of the string.
     * @throws IndexOutOfBoundsException if the input string is empty.
     */
    @Override
    public Character cast(String value) {
        return value.charAt(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<Character> getDataType() {
        return Character.class;
    }
}
