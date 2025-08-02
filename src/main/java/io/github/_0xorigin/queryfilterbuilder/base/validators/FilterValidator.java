package io.github._0xorigin.queryfilterbuilder.base.validators;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractFilterField;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.FilterOperator;
import io.github._0xorigin.queryfilterbuilder.base.utils.FilterUtils;
import io.github._0xorigin.queryfilterbuilder.base.wrappers.FilterErrorWrapper;
import io.github._0xorigin.queryfilterbuilder.base.wrappers.FilterWrapper;

import java.util.ArrayList;
import java.util.List;

public final class FilterValidator {

    private FilterValidator () {
        throw new IllegalStateException("Utility class");
    }

    public static void validateFilterFieldAndOperator(
        final AbstractFilterField<?> filterField,
        final FilterOperator filterOperator,
        final FilterWrapper filterWrapper,
        final FilterErrorWrapper filterErrorWrapper
    ) {
        final List<String> errorMessages = new ArrayList<>();

        if (filterField == null)
            errorMessages.add("Data type for field '" + filterWrapper.field() + "' is not supported.");

        if (filterOperator == null)
            errorMessages.add("Operator '" + filterWrapper.operator().getValue() + "' is not a valid operator.");

        if (filterField != null && !filterField.getSupportedOperators().contains(filterWrapper.operator()))
            errorMessages.add(
                    "Operator '" + filterWrapper.operator().getValue() +
                    "' is not a valid operator for the type of field '" +
                    filterWrapper.field() + "'."
            );

        for (String errorMessage : errorMessages) {
            FilterUtils.addError(
                filterErrorWrapper.bindingResult(),
                FilterUtils.generateFieldError(
                    filterErrorWrapper.bindingResult(),
                    filterWrapper.originalFieldName(),
                    filterWrapper.values().toString(),
                    errorMessage
                )
            );
        }
    }
}
