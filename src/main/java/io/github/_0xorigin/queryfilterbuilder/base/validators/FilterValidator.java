package io.github._0xorigin.queryfilterbuilder.base.validators;

import io.github._0xorigin.queryfilterbuilder.base.enums.MessageKey;
import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractFilterField;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.FilterOperator;
import io.github._0xorigin.queryfilterbuilder.base.utils.FilterUtils;
import io.github._0xorigin.queryfilterbuilder.base.wrappers.FilterErrorWrapper;
import io.github._0xorigin.queryfilterbuilder.base.wrappers.FilterWrapper;
import io.github._0xorigin.queryfilterbuilder.base.services.LocalizationService;

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
        final FilterErrorWrapper filterErrorWrapper,
        final LocalizationService localizationService
    ) {
        final List<String> errorMessages = new ArrayList<>();

        if (filterField == null)
            errorMessages.add(
                localizationService.getMessage(MessageKey.DATA_TYPE_NOT_SUPPORTED.getCode())
            );

        if (filterOperator == null)
            errorMessages.add(
                localizationService.getMessage(
                    MessageKey.OPERATOR_NOT_VALID.getCode(),
                    null,
                    localizationService.getMessage(filterWrapper.operator().getValue())
                )
            );

        if (filterField != null && !filterField.getSupportedOperators().contains(filterWrapper.operator()))
            errorMessages.add(
                localizationService.getMessage(
                    MessageKey.OPERATOR_NOT_SUPPORTED.getCode(),
                    null,
                    localizationService.getMessage(filterWrapper.operator().getValue())
                )
            );

        for (String errorMessage : errorMessages) {
            FilterUtils.addFieldError(
                filterErrorWrapper.bindingResult(),
                filterWrapper.originalFieldName(),
                filterWrapper.values().toString(),
                errorMessage
            );
        }
    }
}
