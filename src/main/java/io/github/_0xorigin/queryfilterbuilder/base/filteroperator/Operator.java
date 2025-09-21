package io.github._0xorigin.queryfilterbuilder.base.filteroperator;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Represents the set of available filter operations.
 * Each operator corresponds to a specific type of comparison or check.
 */
public enum Operator {

    /**
     * Equals.
     */
    EQ("eq"),
    /**
     * Not equals.
     */
    NEQ("neq"),
    /**
     * Greater than.
     */
    GT("gt"),
    /**
     * Less than.
     */
    LT("lt"),
    /**
     * Greater than or equal to.
     */
    GTE("gte"),
    /**
     * Less than or equal to.
     */
    LTE("lte"),
    /**
     * Is null.
     */
    IS_NULL("isNull"),
    /**
     * Is not null.
     */
    IS_NOT_NULL("isNotNull"),
    /**
     * In a list of values.
     */
    IN("in"),
    /**
     * Not in a list of values.
     */
    NOT_IN("notIn"),
    /**
     * Between two values.
     */
    BETWEEN("between"),
    /**
     * Not between two values.
     */
    NOT_BETWEEN("notBetween"),
    /**
     * Contains the given substring (case-sensitive).
     */
    CONTAINS("contains"),
    /**
     * Contains the given substring (case-insensitive).
     */
    ICONTAINS("icontains"),
    /**
     * Starts with the given substring (case-sensitive).
     */
    STARTS_WITH("startsWith"),
    /**
     * Starts with the given substring (case-insensitive).
     */
    ISTARTS_WITH("istartsWith"),
    /**
     * Ends with the given substring (case-sensitive).
     */
    ENDS_WITH("endsWith"),
    /**
     * Ends with the given substring (case-insensitive).
     */
    IENDS_WITH("iendsWith");

    private final String value;
    private static final Map<String, Operator> SYMBOL_MAP = new HashMap<>();

    static {
        for (Operator operator : Operator.values()) {
            SYMBOL_MAP.put(transformValue(operator.getValue()), operator);
        }
    }

    Operator(String value) {
        this.value = value;
    }

    /**
     * Gets the string representation of the operator (e.g., "eq", "gt").
     *
     * @return The string value of the operator.
     */
    public String getValue() {
        return value;
    }

    /**
     * Gets an {@code Operator} from its string representation.
     * The lookup is case-insensitive.
     *
     * @param value The string value of the operator (e.g., "eq", "EQ", "Eq").
     * @return An {@link Optional} containing the corresponding {@code Operator}, or empty if no match is found.
     */
    public static Optional<Operator> fromValue(String value){
        return Optional.ofNullable(SYMBOL_MAP.get(transformValue(value)));
    }

    private static String transformValue(String value) {
        return String.valueOf(value).toLowerCase();
    }
}
