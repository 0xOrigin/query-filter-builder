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
class NotInTest {

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private Predicate predicate;

    @Mock
    private FilterErrorWrapper filterErrorWrapper;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private Expression<Integer> expression;

    @InjectMocks
    private NotIn notIn;

    @Test
    void apply_validListWithElements_returnsPredicate() {
        List<Integer> values = List.of(1, 2, 3);
        Predicate inPredicate = mock(Predicate.class);
        when(expression.in(values)).thenReturn(inPredicate);
        when(criteriaBuilder.not(inPredicate)).thenReturn(predicate);

        Optional<Predicate> result = notIn.apply(expression, criteriaBuilder, values, filterErrorWrapper);

        assertThat(result).isPresent().contains(predicate);
        verify(expression).in(values);
        verify(criteriaBuilder).not(inPredicate);
        verifyNoInteractions(bindingResult);
    }

    @Test
    void apply_invalidList_returnsEmptyOptional() {
        List<String> values = List.of();
        Expression<String> expression = mock(Expression.class);

        Optional<Predicate> result = notIn.apply(expression, criteriaBuilder, values, filterErrorWrapper);

        assertThat(result).isEmpty();
        verifyNoInteractions(criteriaBuilder, bindingResult);
    }

    @Test
    void getOperatorConstant_returnsNotInOperator() {
        assertThat(notIn.getOperatorConstant()).isEqualTo(Operator.NOT_IN);
    }
}
