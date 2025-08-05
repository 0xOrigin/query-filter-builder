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

public final class Between implements FilterOperator {

    private final LocalizationService localizationService;

    public Between(LocalizationService localizationService) {
        this.localizationService = localizationService;
    }

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
                    null,
                    localizationService.getMessage(Operator.BETWEEN.getValue())
                )
            );

            return Optional.empty();
        }

        return Optional.ofNullable(cb.between(expression, values.get(0), values.get(1)));
    }

    @Override
    public Operator getOperatorConstant() {
        return Operator.BETWEEN;
    }
}
