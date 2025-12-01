package io.github._0xorigin.queryfilterbuilder.operators;

import io.github._0xorigin.queryfilterbuilder.base.enums.MessageKey;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.FilterOperator;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;
import io.github._0xorigin.queryfilterbuilder.base.services.LocalizationService;
import io.github._0xorigin.queryfilterbuilder.base.utils.FilterUtils;
import io.github._0xorigin.queryfilterbuilder.base.wrappers.FilterErrorWrapper;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * A {@link FilterOperator} implementation that handles the 'notBetween' operation.
 * This operator checks if an expression's value is not between two specified values.
 */
public final class NotBetween implements FilterOperator {

    private final LocalizationService localizationService;

    /**
     * Constructs a new NotBetween operator.
     *
     * @param localizationService Service for retrieving localized error messages.
     */
    public NotBetween(LocalizationService localizationService) {
        this.localizationService = localizationService;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation requires the {@code values} list to contain exactly two non-null elements.
     * If the list size is not 2, an error is added to the {@code filterErrorWrapper} and an empty optional is returned.
     */
    @Override
    public <T extends Comparable<? super T> & Serializable> Optional<Predicate> apply(Expression<T> expression, CriteriaBuilder cb, List<T> values, FilterErrorWrapper filterErrorWrapper) {
        if (FilterUtils.isNotValidList(values))
            return Optional.empty();

        if (values.size() != 2) {
            FilterUtils.addFieldError(
                filterErrorWrapper.bindingResult(),
                filterErrorWrapper.filterWrapper().originalFieldName(),
                values.toString(),
                localizationService.getMessage(
                    MessageKey.VALUE_MUST_EXACTLY_TWO_ELEMENTS.getCode(),
                    localizationService.getMessage(Operator.NOT_BETWEEN.getValue())
                )
            );

            return Optional.empty();
        }

        return Optional.ofNullable(cb.not(cb.between(expression, values.get(0), values.get(values.size() - 1))));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Operator getOperatorConstant() {
        return Operator.NOT_BETWEEN;
    }
}
