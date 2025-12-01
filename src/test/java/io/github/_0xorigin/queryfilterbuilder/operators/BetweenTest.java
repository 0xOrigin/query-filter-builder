package io.github._0xorigin.queryfilterbuilder.operators;

import io.github._0xorigin.queryfilterbuilder.base.enums.MessageKey;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;
import io.github._0xorigin.queryfilterbuilder.base.services.LocalizationService;
import io.github._0xorigin.queryfilterbuilder.base.wrappers.FilterErrorWrapper;
import io.github._0xorigin.queryfilterbuilder.base.wrappers.FilterWrapper;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BetweenTest {

    @Mock
    private LocalizationService localizationService;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private Predicate predicate;

    @Mock
    private FilterErrorWrapper filterErrorWrapper;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private Between between;

    @Test
    void apply_validListWithTwoElements_returnsPredicate() {
        List<Integer> values = List.of(1, 10);
        Expression<Integer> expression = mock(Expression.class);
        when(criteriaBuilder.between(expression, values.get(0), values.get(1))).thenReturn(predicate);

        Optional<Predicate> result = between.apply(expression, criteriaBuilder, values, filterErrorWrapper);

        assertThat(result).isPresent().contains(predicate);
        verify(criteriaBuilder).between(expression, values.get(0), values.get(1));
        verifyNoInteractions(bindingResult);
    }

    @Test
    void apply_invalidList_returnsEmptyOptional() {
        List<String> values = List.of();
        Expression<String> expression = mock(Expression.class);

        Optional<Predicate> result = between.apply(expression, criteriaBuilder, values, filterErrorWrapper);

        assertThat(result).isEmpty();
        verifyNoInteractions(criteriaBuilder, bindingResult);
    }

    @Test
    void apply_listWithWrongSize_addsErrorAndReturnsEmptyOptional() {
        var values = List.of(1, 10, 100);
        Expression<Integer> expression = mock(Expression.class);
        when(filterErrorWrapper.bindingResult()).thenReturn(bindingResult);
        when(bindingResult.getObjectName()).thenReturn("test");
        when(filterErrorWrapper.filterWrapper()).thenReturn(mock(FilterWrapper.class));
        when(filterErrorWrapper.filterWrapper().originalFieldName()).thenReturn("fieldName");
        when(localizationService.getMessage(Operator.BETWEEN.getValue())).thenReturn("between");
        when(localizationService.getMessage(MessageKey.VALUE_MUST_EXACTLY_TWO_ELEMENTS.getCode(), "between"))
            .thenReturn("Value must have exactly two elements for between operator");

        Optional<Predicate> result = between.apply(expression, criteriaBuilder, values, filterErrorWrapper);

        assertThat(result).isEmpty();
        verify(filterErrorWrapper.bindingResult()).addError(any());
        verify(localizationService).getMessage(MessageKey.VALUE_MUST_EXACTLY_TWO_ELEMENTS.getCode(), "between");
        verifyNoInteractions(criteriaBuilder);
    }

    @Test
    void getOperatorConstant_returnsBetweenOperator() {
        assertThat(between.getOperatorConstant()).isEqualTo(Operator.BETWEEN);
    }
}
