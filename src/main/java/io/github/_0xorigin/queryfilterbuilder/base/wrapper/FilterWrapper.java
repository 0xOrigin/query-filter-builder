package io.github._0xorigin.queryfilterbuilder.base.wrapper;

import io.github._0xorigin.queryfilterbuilder.base.enums.SourceType;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;

import java.util.List;

public record FilterWrapper (
    String field,
    String originalFieldName,
    Operator operator,
    List<String> values,
    SourceType sourceType
) {}
