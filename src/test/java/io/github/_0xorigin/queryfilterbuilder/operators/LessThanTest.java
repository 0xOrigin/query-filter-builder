package io.github._0xorigin.queryfilterbuilder.operators;

import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;
import io.github._0xorigin.queryfilterbuilder.base.wrappers.FilterErrorWrapper;
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
class LessThanTest {

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private Predicate predicate;

    @Mock
    private FilterErrorWrapper filterErrorWrapper;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private LessThan lessThan;

    @Test
    void apply_validListWithSingleElement_returnsPredicate() {
        List<Integer> values = List.of(42);
        Expression<Integer> expression = mock(Expression.class);
        when(criteriaBuilder.lessThan(expression, values.get(0))).thenReturn(predicate);

        Optional<Predicate> result = lessThan.apply(expression, criteriaBuilder, values, filterErrorWrapper);

        assertThat(result).isPresent().contains(predicate);
        verify(criteriaBuilder).lessThan(expression, values.get(0));
        verifyNoInteractions(bindingResult);
    }

    @Test
    void apply_invalidList_returnsEmptyOptional() {
        List<String> values = List.of();
        Expression<String> expression = mock(Expression.class);

        Optional<Predicate> result = lessThan.apply(expression, criteriaBuilder, values, filterErrorWrapper);

        assertThat(result).isEmpty();
        verifyNoInteractions(criteriaBuilder, bindingResult);
    }

    @Test
    void getOperatorConstant_returnsLessThanOperator() {
        assertThat(lessThan.getOperatorConstant()).isEqualTo(Operator.LT);
    }
}
