package io.github._0xorigin.fields;

import io.github._0xorigin.base.AbstractNumberFilterField;
import io.github._0xorigin.base.ErrorWrapper;

public class FloatFilter extends AbstractNumberFilterField<Float> {

    @Override
    public Float cast(Object value, ErrorWrapper errorWrapper) {
        try {
            return Float.parseFloat(value.toString());
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
