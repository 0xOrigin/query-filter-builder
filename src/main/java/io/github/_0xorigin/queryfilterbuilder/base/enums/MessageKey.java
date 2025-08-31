package io.github._0xorigin.queryfilterbuilder.base.enums;

public enum MessageKey {
    DATA_TYPE_NOT_SUPPORTED("error.data.type.not.supported"),
    OPERATOR_NOT_VALID("error.operator.not.valid"),
    OPERATOR_NOT_SUPPORTED("error.operator.not.supported"),
    VALUE_MUST_EXACTLY_TWO_ELEMENTS("error.operator.value.must.exactly.two"),
    INVALID_QUERY_PARAMETER_EXCEPTION_MESSAGE("error.invalid.query.parameter.exception.message"),
    QUERY_BUILDER_CONFIGURATION_EXCEPTION_MESSAGE("error.query.builder.configuration.exception.message"),
    NON_ASSOCIATION_IN_INTERMEDIATE_PATH("error.path.generator.non.association.in.intermediate.path");

    private final String code;

    MessageKey(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
