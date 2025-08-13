package io.github._0xorigin.queryfilterbuilder.base.filteroperator;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum Operator {

    EQ("eq"),
    NEQ("neq"),
    GT("gt"),
    LT("lt"),
    GTE("gte"),
    LTE("lte"),
    IS_NULL("isNull"),
    IS_NOT_NULL("isNotNull"),
    IN("in"),
    NOT_IN("notIn"),
    BETWEEN("between"),
    NOT_BETWEEN("notBetween"),
    CONTAINS("contains"),
    ICONTAINS("icontains"),
    STARTS_WITH("startsWith"),
    ISTARTS_WITH("istartsWith"),
    ENDS_WITH("endsWith"),
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

    public String getValue() {
        return value;
    }

    public static Optional<Operator> fromValue(String value){
        return Optional.ofNullable(SYMBOL_MAP.get(transformValue(value)));
    }

    private static String transformValue(String value) {
        return String.valueOf(value).toLowerCase();
    }
}
