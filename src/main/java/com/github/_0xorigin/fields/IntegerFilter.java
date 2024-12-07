package com.github._0xorigin.fields;

import com.github._0xorigin.base.AbstractNumberFilterField;
import com.github._0xorigin.base.ErrorWrapper;

public class IntegerFilter extends AbstractNumberFilterField<Integer> {

    @Override
    public Integer cast(Object value, ErrorWrapper errorWrapper) {
        try {
            return Integer.parseInt(value.toString());
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