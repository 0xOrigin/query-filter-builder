package io.github._0xorigin.queryfilterbuilder;

import io.github._0xorigin.queryfilterbuilder.base.*;

import java.util.ArrayList;
import java.util.List;

public class FilterValidator {

    public void validateFilterFieldAndOperator(
            AbstractFilterField<?> filterClass,
            FilterOperator filterOperator,
            FilterWrapper filterWrapper,
            ErrorWrapper errorWrapper
    ) {
        List<String> errorMessages = new ArrayList<>();

        if (filterClass == null)
            errorMessages.add("Data type for field '" + filterWrapper.getField() + "' is not supported.");

        if (filterOperator == null)
            errorMessages.add("Operator '" + filterWrapper.getOperator().getValue() + "' is not a valid operator.");

        if (filterClass != null && !filterClass.getSupportedOperators().contains(filterWrapper.getOperator()))
            errorMessages.add(
                    "Operator '" + filterWrapper.getOperator().getValue() +
                    "' is not a valid operator for the type of field '" +
                    filterWrapper.getField() + "'."
            );

        for (String errorMessage : errorMessages) {
            FilterUtils.addError(
                errorWrapper,
                FilterUtils.generateFieldError(
                    errorWrapper,
                    filterWrapper.getValues().toString(),
                    errorMessage
                )
            );
        }
    }

}
