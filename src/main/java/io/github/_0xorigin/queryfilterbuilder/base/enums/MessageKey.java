package io.github._0xorigin.queryfilterbuilder.base.enums;

/**
 * Defines keys for localized messages, typically used for error reporting.
 * Each key maps to a code that can be resolved to a message in a resource bundle.
 */
public enum MessageKey {
    /**
     * Error when the data type of filter value is not supported.
     */
    DATA_TYPE_NOT_SUPPORTED("error.data.type.not.supported"),
    /**
     * Error when the provided filter operator is not valid or recognized.
     */
    OPERATOR_NOT_VALID("error.operator.not.valid"),
    /**
     * Error when a valid operator is not supported for a specific field.
     */
    OPERATOR_NOT_SUPPORTED("error.operator.not.supported"),
    /**
     * Error for operators (like 'between') that require exactly two values.
     */
    VALUE_MUST_EXACTLY_TWO_ELEMENTS("error.operator.value.must.exactly.two"),
    /**
     * General message for exceptions related to invalid query parameters.
     */
    INVALID_QUERY_PARAMETER_EXCEPTION_MESSAGE("error.invalid.query.parameter.exception.message"),
    /**
     * General message for exceptions related to configuration errors in the query builder.
     */
    QUERY_BUILDER_CONFIGURATION_EXCEPTION_MESSAGE("error.query.builder.configuration.exception.message"),
    /**
     * Error when generating a field path and an intermediate part of the path is not an association.
     */
    NON_ASSOCIATION_IN_INTERMEDIATE_PATH("error.path.generator.non.association.in.intermediate.path");

    private final String code;

    MessageKey(String code) {
        this.code = code;
    }

    /**
     * Gets the message code associated with the key.
     *
     * @return The message code string.
     */
    public String getCode() {
        return code;
    }
}
