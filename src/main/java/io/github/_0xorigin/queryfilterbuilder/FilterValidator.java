package io.github._0xorigin.queryfilterbuilder;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractFilterField;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.FilterOperator;
import io.github._0xorigin.queryfilterbuilder.base.utils.FilterUtils;
import io.github._0xorigin.queryfilterbuilder.base.wrappers.ErrorWrapper;
import io.github._0xorigin.queryfilterbuilder.base.wrappers.FilterWrapper;

import java.util.ArrayList;
import java.util.List;

public final class FilterValidator {

    private FilterValidator () {
        throw new IllegalStateException("Utility class");
    }

    public static void validateFilterFieldAndOperator(
        final AbstractFilterField<?> filterClass,
        final FilterOperator filterOperator,
        final FilterWrapper filterWrapper,
        final ErrorWrapper errorWrapper
    ) {
        final List<String> errorMessages = new ArrayList<>();

        if (filterClass == null)
            errorMessages.add("Data type for field '" + filterWrapper.field() + "' is not supported.");

        if (filterOperator == null)
            errorMessages.add("Operator '" + filterWrapper.operator().getValue() + "' is not a valid operator.");

        if (filterClass != null && !filterClass.getSupportedOperators().contains(filterWrapper.operator()))
            errorMessages.add(
                    "Operator '" + filterWrapper.operator().getValue() +
                    "' is not a valid operator for the type of field '" +
                    filterWrapper.field() + "'."
            );

        for (String errorMessage : errorMessages) {
            FilterUtils.addError(
                errorWrapper,
                FilterUtils.generateFieldError(
                    errorWrapper,
                    filterWrapper.values().toString(),
                    errorMessage
                )
            );
        }
    }
}
