package io.github._0xorigin.queryfilterbuilder;

import io.github._0xorigin.queryfilterbuilder.base.builders.FilterBuilder;
import io.github._0xorigin.queryfilterbuilder.base.builders.SortBuilder;
import io.github._0xorigin.queryfilterbuilder.base.holders.ErrorHolder;
import io.github._0xorigin.queryfilterbuilder.base.utils.FilterUtils;
import io.github._0xorigin.queryfilterbuilder.base.wrappers.FilterWrapper;
import io.github._0xorigin.queryfilterbuilder.base.wrappers.SortWrapper;
import io.github._0xorigin.queryfilterbuilder.entities.User;
import io.github._0xorigin.queryfilterbuilder.exceptions.InvalidQueryParameterException;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QueryFilterBuilderImpTest {

    @Mock
    private FilterBuilder<User> filterBuilder;

    @Mock
    private SortBuilder<User> sortBuilder;

    @Mock
    private Root<User> root;

    @Mock
    private CriteriaQuery<User> criteriaQuery;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private Predicate predicate;

    @Mock
    private Order order;

    @InjectMocks
    private QueryFilterBuilderImp<User> queryFilterBuilder;

    private final FilterContext<User> filterContext = getFilterContext();
    private final SortContext<User> sortContext = getSortContext();

    private FilterContext<User> getFilterContext() {
        return FilterContext.buildTemplateForType(User.class)
            .buildTemplate()
            .newSourceBuilder()
            .buildFilterContext();
    }

    private SortContext<User> getSortContext() {
        return SortContext.buildTemplateForType(User.class)
            .buildTemplate()
            .newSourceBuilder()
            .buildSortContext();
    }

    @Test
    void buildFilterSpecification_WhenFilterContextIsNull_ThrowsNullPointerException() {
        assertThatThrownBy(() -> queryFilterBuilder.buildFilterSpecification(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("FilterContext must not be null");
    }

    @Test
    void buildSortSpecification_WhenSortContextIsNull_ThrowsNullPointerException() {
        assertThatThrownBy(() -> queryFilterBuilder.buildSortSpecification(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("SortContext must not be null");
    }

    @Test
    void buildFilterSpecification_WhenNoPredicates_ReturnsNullPredicate() {
        when(filterBuilder.getDistinctFilterWrappers(filterContext)).thenReturn(List.of());

        Specification<User> specification = queryFilterBuilder.buildFilterSpecification(filterContext);
        Predicate result = specification.toPredicate(root, criteriaQuery, criteriaBuilder);

        assertThat(result).isNull();
        verify(filterBuilder).getDistinctFilterWrappers(filterContext);
        verifyNoMoreInteractions(filterBuilder, sortBuilder, criteriaBuilder);
    }

    @Test
    void buildFilterSpecification_WhenPredicatesExist_ReturnsCombinedPredicate() {
        var filterWrapper = mock(FilterWrapper.class);
        when(filterBuilder.getDistinctFilterWrappers(filterContext)).thenReturn(List.of(filterWrapper));
        when(filterBuilder.buildPredicateForWrapper(any(), any(), any(), any(), any(), any())).thenReturn(Optional.of(predicate));
        when(criteriaBuilder.and(any(Predicate[].class))).thenReturn(predicate);

        Specification<User> specification = queryFilterBuilder.buildFilterSpecification(filterContext);
        Predicate result = specification.toPredicate(root, criteriaQuery, criteriaBuilder);

        assertThat(result).isEqualTo(predicate);
        verify(filterBuilder).getDistinctFilterWrappers(filterContext);
        verify(criteriaBuilder).and(predicate);
        verifyNoInteractions(sortBuilder);
    }

    @Test
    void buildFilterSpecification_WhenErrorHolderHasErrors_ThrowsClientSideException() {
        var filterWrapper = mock(FilterWrapper.class);
        when(filterBuilder.getDistinctFilterWrappers(filterContext)).thenReturn(List.of(filterWrapper));
        when(filterBuilder.buildPredicateForWrapper(any(), any(), any(), any(), any(), any())).thenAnswer(invocation -> {
           ErrorHolder errorHolder = invocation.getArgument(5);
           FilterUtils.addFieldError(errorHolder.bindingResult(), "isActive", "true", "Invalid value");
           return Optional.empty();
        });

        Specification<User> specification = queryFilterBuilder.buildFilterSpecification(filterContext);

        assertThatThrownBy(() -> specification.toPredicate(root, criteriaQuery, criteriaBuilder))
            .isInstanceOf(InvalidQueryParameterException.class);
        verify(filterBuilder).getDistinctFilterWrappers(filterContext);
        verifyNoInteractions(sortBuilder);
    }

    // Success scenarios for buildSortSpecification
    @Test
    void buildSortSpecification_WhenNoOrders_SetsEmptyOrderList() {
        when(sortBuilder.getDistinctSortWrappers(sortContext)).thenReturn(List.of());

        Specification<User> specification = queryFilterBuilder.buildSortSpecification(sortContext);

        Predicate result = specification.toPredicate(root, criteriaQuery, criteriaBuilder);

        assertThat(result).isNull();
        verify(sortBuilder).getDistinctSortWrappers(sortContext);
        verify(criteriaQuery).orderBy(List.of());
        verifyNoMoreInteractions(sortBuilder, filterBuilder, criteriaBuilder);
    }

    @Test
    void buildSortSpecification_WhenOrdersExist_SetsOrderList() {
        var sortWrapper = mock(SortWrapper.class);
        when(sortBuilder.getDistinctSortWrappers(sortContext)).thenReturn(List.of(sortWrapper));
        when(sortBuilder.buildOrderForWrapper(any(), any(), any(), any(), any(), any())).thenReturn(Optional.of(order));

        Specification<User> specification = queryFilterBuilder.buildSortSpecification(sortContext);

        Predicate result = specification.toPredicate(root, criteriaQuery, criteriaBuilder);

        assertThat(result).isNull();
        verify(sortBuilder).getDistinctSortWrappers(sortContext);
        verify(criteriaQuery).orderBy(List.of(order));
        verifyNoInteractions(filterBuilder);
    }

    @Test
    void buildSortSpecification_WhenErrorHolderHasErrors_ThrowsClientSideException() {
        SortWrapper sortWrapper = mock(SortWrapper.class);
        when(sortBuilder.getDistinctSortWrappers(sortContext)).thenReturn(List.of(sortWrapper));
        when(sortBuilder.buildOrderForWrapper(any(), any(), any(), any(), any(), any())).thenAnswer(invocation -> {
            ErrorHolder errorHolder = invocation.getArgument(5);
            FilterUtils.addFieldError(errorHolder.bindingResult(), "isActive", "true", "Invalid value");
            return Optional.empty();
        });

        Specification<User> specification = queryFilterBuilder.buildSortSpecification(sortContext);

        assertThatThrownBy(() -> specification.toPredicate(root, criteriaQuery, criteriaBuilder))
            .isInstanceOf(InvalidQueryParameterException.class);
        verify(sortBuilder).getDistinctSortWrappers(sortContext);
        verifyNoInteractions(filterBuilder);
    }
}