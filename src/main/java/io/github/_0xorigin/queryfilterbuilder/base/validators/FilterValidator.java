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

/**
 * A utility class for performing validation checks related to filters.
 * This class cannot be instantiated.
 */
public final class FilterValidator {

    private FilterValidator () {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Validates the compatibility between a filter field and a filter operator.
     * It checks for three conditions:
     * <ol>
     *     <li>Whether the filter field type is supported (i.e., not null).</li>
     *     <li>Whether the filter operator is valid (i.e., not null).</li>
     *     <li>Whether the operator is supported by the given filter field.</li>
     * </ol>
     * If any validation fails, a corresponding {@link org.springframework.validation.FieldError} is added to the {@link org.springframework.validation.BindingResult}.
     *
     * @param filterField         The filter field implementation for the target data type.
     * @param filterOperator      The filter operator implementation.
     * @param filterWrapper       The wrapper containing the raw filter request data.
     * @param filterErrorWrapper  The wrapper for collecting validation errors.
     * @param localizationService The service for retrieving localized error messages.
     */
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
                    localizationService.getMessage(filterWrapper.operator().getValue())
                )
            );

        if (filterField != null && !filterField.getSupportedOperators().contains(filterWrapper.operator()))
            errorMessages.add(
                localizationService.getMessage(
                    MessageKey.OPERATOR_NOT_SUPPORTED.getCode(),
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
