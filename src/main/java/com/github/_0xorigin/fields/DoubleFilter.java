package com.github._0xorigin.fields;

import com.github._0xorigin.base.AbstractNumberFilterField;
import com.github._0xorigin.base.ErrorWrapper;

public class DoubleFilter extends AbstractNumberFilterField<Double> {

    @Override
    public Double cast(Object value, ErrorWrapper errorWrapper) {
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            addError(
                errorWrapper,
                generateFieldError(
                    errorWrapper,
                    value.toString(),
                    e.getLocalizedMessage()
                )
            );
        }

        return null;
    }

}
