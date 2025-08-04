package io.github._0xorigin.queryfilterbuilder.base.enums;

public enum MessageKey {
    DATA_TYPE_NOT_SUPPORTED("error.data.type.not.supported"),
    OPERATOR_NOT_VALID("error.operator.not.valid"),
    OPERATOR_NOT_SUPPORTED("error.operator.not.supported");

    private final String code;

    MessageKey(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
