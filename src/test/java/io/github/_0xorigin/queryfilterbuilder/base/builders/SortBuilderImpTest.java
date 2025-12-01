package io.github._0xorigin.queryfilterbuilder.base.builders;

import io.github._0xorigin.queryfilterbuilder.QueryFilterBuilderImp;
import io.github._0xorigin.queryfilterbuilder.SortContext;
import io.github._0xorigin.queryfilterbuilder.base.dtos.SortRequest;
import io.github._0xorigin.queryfilterbuilder.base.enums.SortType;
import io.github._0xorigin.queryfilterbuilder.base.enums.SourceType;
import io.github._0xorigin.queryfilterbuilder.base.generators.PathGenerator;
import io.github._0xorigin.queryfilterbuilder.base.holders.CustomSortHolder;
import io.github._0xorigin.queryfilterbuilder.base.holders.ErrorHolder;
import io.github._0xorigin.queryfilterbuilder.base.holders.SortHolder;
import io.github._0xorigin.queryfilterbuilder.base.parsers.SortParser;
import io.github._0xorigin.queryfilterbuilder.base.wrappers.SortWrapper;
import io.github._0xorigin.queryfilterbuilder.entities.User;
import io.github._0xorigin.queryfilterbuilder.exceptions.QueryBuilderConfigurationException;
import jakarta.persistence.criteria.*;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Sort;
import org.springframework.validation.BindingResult;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SortBuilderImpTest {

    @Mock
    private PathGenerator<User> fieldPathGenerator;

    @Mock
    private SortParser sortParser;

    @Mock
    private Root<User> root;

    @Mock
    private CriteriaQuery<?> criteriaQuery;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private SortContext<User> sortContext;

    @Mock
    private ErrorHolder errorHolder;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private Expression<String> stringExpression;

    @Mock
    private Order order;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private SortBuilderImp<User> sortBuilder;

    @Test
    void getDistinctSortWrappers_WithHttpServletRequest_ReturnsParsedWrappers() {
        // Arrange
        SortWrapper wrapper = new SortWrapper("firstName", "firstName", Sort.Direction.ASC, SourceType.QUERY_PARAM, Optional.empty());
        List<SortWrapper> parsedWrappers = List.of(wrapper);
        when(sortContext.getRequest()).thenReturn(Optional.of(httpServletRequest));
        when(sortContext.getSortRequests()).thenReturn(Optional.empty());
        when(sortParser.parse(httpServletRequest)).thenReturn(parsedWrappers);
        SortHolder<User, String> sortHolder = mock(SortHolder.class);
        when(sortContext.getSorts()).thenReturn(Map.of("firstName", sortHolder));
        when(sortHolder.directions()).thenReturn(Set.of(Sort.Direction.ASC));
        when(sortHolder.sourceTypes()).thenReturn(Set.of(SourceType.QUERY_PARAM));

        // Act
        Collection<SortWrapper> result = sortBuilder.getDistinctSortWrappers(sortContext);

        // Assert
        assertThat(result).hasSize(1).containsExactly(wrapper.withSortType(SortType.NORMAL));
        verify(sortParser).parse(httpServletRequest);
    }

    @Test
    void getDistinctSortWrappers_WithSortRequests_ReturnsParsedWrappers() {
        // Arrange
        List<SortRequest> sortRequests = List.of(new SortRequest("lastName", Sort.Direction.DESC));
        SortWrapper wrapper = new SortWrapper("lastName", "lastName", Sort.Direction.DESC, SourceType.REQUEST_BODY, Optional.empty());
        List<SortWrapper> parsedWrappers = List.of(wrapper);
        when(sortContext.getRequest()).thenReturn(Optional.empty());
        when(sortContext.getSortRequests()).thenReturn(Optional.of(sortRequests));
        when(sortParser.parse(sortRequests)).thenReturn(parsedWrappers);
        CustomSortHolder<User> customSortHolder = mock(CustomSortHolder.class);
        when(sortContext.getCustomSorts()).thenReturn(Map.of("lastName", customSortHolder));
        when(customSortHolder.sourceTypes()).thenReturn(Set.of(SourceType.REQUEST_BODY));

        // Act
        Collection<SortWrapper> result = sortBuilder.getDistinctSortWrappers(sortContext);

        // Assert
        assertThat(result).hasSize(1).containsExactly(wrapper.withSortType(SortType.CUSTOM));
        verify(sortParser).parse(sortRequests);
    }

    @Test
    void getDistinctSortWrappers_NoRequestOrSortRequests_ReturnsEmpty() {
        // Arrange
        when(sortContext.getRequest()).thenReturn(Optional.empty());
        when(sortContext.getSortRequests()).thenReturn(Optional.empty());

        // Act
        Collection<SortWrapper> result = sortBuilder.getDistinctSortWrappers(sortContext);

        // Assert
        assertThat(result).isEmpty();
        verify(sortParser, never()).parse(any(HttpServletRequest.class));
        verify(sortParser, never()).parse(any(List.class));
    }

    @Test
    void buildOrderForWrapper_NoSortType_ReturnsEmpty() {
        // Arrange
        SortWrapper wrapper = new SortWrapper("firstName", "firstName", Sort.Direction.ASC, SourceType.QUERY_PARAM, Optional.empty());

        // Act
        Optional<Order> result = sortBuilder.buildOrderForWrapper(root, criteriaQuery, criteriaBuilder, sortContext, wrapper, errorHolder);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void buildOrderForWrapper_NormalSortType_ValidSortAsc_ReturnsOrder() {
        // Arrange
        SortWrapper wrapper = new SortWrapper("firstName", "firstName", Sort.Direction.ASC, SourceType.QUERY_PARAM, Optional.of(SortType.NORMAL));
        SortHolder<User, String> sortHolder = mock(SortHolder.class);
        when(sortContext.getSorts()).thenReturn(Map.of("firstName", sortHolder));
        when(sortHolder.directions()).thenReturn(Set.of(Sort.Direction.ASC));
        when(sortHolder.sourceTypes()).thenReturn(Set.of(SourceType.QUERY_PARAM));
        when(sortHolder.getExpression(root, criteriaQuery, criteriaBuilder)).thenAnswer(invocation -> Optional.of(stringExpression));
        when(criteriaBuilder.asc(stringExpression)).thenReturn(order);
        when(errorHolder.bindingResult()).thenReturn(bindingResult);

        // Act
        Optional<Order> result = sortBuilder.buildOrderForWrapper(root, criteriaQuery, criteriaBuilder, sortContext, wrapper, errorHolder);

        // Assert
        assertThat(result).isPresent().contains(order);
        verify(criteriaBuilder).asc(stringExpression);
        verify(criteriaBuilder, never()).desc(any());
    }

    @Test
    void buildOrderForWrapper_NormalSortType_ValidSortDesc_ReturnsOrder() {
        // Arrange
        SortWrapper wrapper = new SortWrapper("lastName", "lastName", Sort.Direction.DESC, SourceType.QUERY_PARAM, Optional.of(SortType.NORMAL));
        SortHolder<User, String> sortHolder = mock(SortHolder.class);
        when(sortContext.getSorts()).thenReturn(Map.of("lastName", sortHolder));
        when(sortHolder.directions()).thenReturn(Set.of(Sort.Direction.DESC));
        when(sortHolder.sourceTypes()).thenReturn(Set.of(SourceType.QUERY_PARAM));
        when(sortHolder.getExpression(root, criteriaQuery, criteriaBuilder)).thenAnswer(invocation -> Optional.of(stringExpression));
        when(criteriaBuilder.desc(stringExpression)).thenReturn(order);
        when(errorHolder.bindingResult()).thenReturn(bindingResult);

        // Act
        Optional<Order> result = sortBuilder.buildOrderForWrapper(root, criteriaQuery, criteriaBuilder, sortContext, wrapper, errorHolder);

        // Assert
        assertThat(result).isPresent().contains(order);
        verify(criteriaBuilder).desc(stringExpression);
        verify(criteriaBuilder, never()).asc(any());
    }

    @Test
    void buildOrderForWrapper_NormalSortType_ProviderFunctionPresent_ReturnsOrder() {
        // Arrange
        SortWrapper wrapper = new SortWrapper("firstName", "firstName", Sort.Direction.ASC, SourceType.QUERY_PARAM, Optional.of(SortType.NORMAL));
        SortHolder<User, String> sortHolder = mock(SortHolder.class);
        when(sortContext.getSorts()).thenReturn(Map.of("firstName", sortHolder));
        when(sortHolder.directions()).thenReturn(Set.of(Sort.Direction.ASC));
        when(sortHolder.sourceTypes()).thenReturn(Set.of(SourceType.QUERY_PARAM));
        when(sortHolder.getExpression(root, criteriaQuery, criteriaBuilder)).thenAnswer(invocation -> Optional.of(stringExpression));
        when(criteriaBuilder.asc(stringExpression)).thenReturn(order);
        when(errorHolder.bindingResult()).thenReturn(bindingResult);

        // Act
        Optional<Order> result = sortBuilder.buildOrderForWrapper(root, criteriaQuery, criteriaBuilder, sortContext, wrapper, errorHolder);

        // Assert
        assertThat(result).isPresent().contains(order);
        verify(criteriaBuilder).asc(stringExpression);
        verify(fieldPathGenerator, never()).generate(any(), anyString(), anyString(), any());
    }

    @Test
    void buildOrderForWrapper_NormalSortType_NoProviderFunction_UsesPathGenerator() {
        // Arrange
        SortWrapper wrapper = new SortWrapper("lastName", "lastName", Sort.Direction.ASC, SourceType.QUERY_PARAM, Optional.of(SortType.NORMAL));
        SortHolder<User, String> sortHolder = mock(SortHolder.class);
        when(sortContext.getSorts()).thenReturn(Map.of("lastName", sortHolder));
        when(sortHolder.directions()).thenReturn(Set.of(Sort.Direction.ASC));
        when(sortHolder.sourceTypes()).thenReturn(Set.of(SourceType.QUERY_PARAM));
        when(sortHolder.getExpression(root, criteriaQuery, criteriaBuilder)).thenReturn(Optional.empty());
        when(fieldPathGenerator.generate(root, "lastName", "lastName", bindingResult)).thenAnswer(invocation -> stringExpression);
        when(criteriaBuilder.asc(stringExpression)).thenReturn(order);
        when(errorHolder.bindingResult()).thenReturn(bindingResult);

        // Act
        Optional<Order> result = sortBuilder.buildOrderForWrapper(root, criteriaQuery, criteriaBuilder, sortContext, wrapper, errorHolder);

        // Assert
        assertThat(result).isPresent().contains(order);
        verify(fieldPathGenerator).generate(root, "lastName", "lastName", bindingResult);
        verify(criteriaBuilder).asc(stringExpression);
    }

    @Test
    void buildOrderForWrapper_NormalSortType_InvalidSort_ReturnsEmpty() {
        // Arrange
        SortWrapper wrapper = new SortWrapper("firstName", "firstName", Sort.Direction.ASC, SourceType.QUERY_PARAM, Optional.of(SortType.NORMAL));
        when(sortContext.getSorts()).thenReturn(Map.of());

        // Act
        Optional<Order> result = sortBuilder.buildOrderForWrapper(root, criteriaQuery, criteriaBuilder, sortContext, wrapper, errorHolder);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void buildOrderForWrapper_CustomSortType_ValidSort_ReturnsOrder() {
        // Arrange
        SortWrapper wrapper = new SortWrapper("lastName", "lastName", Sort.Direction.ASC, SourceType.REQUEST_BODY, Optional.of(SortType.CUSTOM));
        CustomSortHolder<User> customSortHolder = mock(CustomSortHolder.class);
        when(sortContext.getCustomSorts()).thenReturn(Map.of("lastName", customSortHolder));
        when(customSortHolder.sourceTypes()).thenReturn(Set.of(SourceType.REQUEST_BODY));
        when(customSortHolder.customSortFunction()).thenReturn((r, q, cb, error) -> Optional.of(order));
        when(errorHolder.bindingResult()).thenReturn(bindingResult);

        // Act
        Optional<Order> result = sortBuilder.buildOrderForWrapper(root, criteriaQuery, criteriaBuilder, sortContext, wrapper, errorHolder);

        // Assert
        assertThat(result).isPresent().contains(order);
    }

    @Test
    void buildOrderForWrapper_CustomSortType_InvalidSort_ReturnsEmpty() {
        // Arrange
        SortWrapper wrapper = new SortWrapper("lastName", "lastName", Sort.Direction.ASC, SourceType.REQUEST_BODY, Optional.of(SortType.CUSTOM));
        when(sortContext.getCustomSorts()).thenReturn(Map.of());

        // Act
        Optional<Order> result = sortBuilder.buildOrderForWrapper(root, criteriaQuery, criteriaBuilder, sortContext, wrapper, errorHolder);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void buildOrderForWrapper_ErrorInBindingResult_ThrowsException() {
        // Arrange
        SortWrapper wrapper = new SortWrapper("firstName", "firstName", Sort.Direction.ASC, SourceType.QUERY_PARAM, Optional.of(SortType.NORMAL));
        SortHolder<User, String> sortHolder = mock(SortHolder.class);
        when(sortContext.getSorts()).thenReturn(Map.of("firstName", sortHolder));
        when(sortHolder.directions()).thenReturn(Set.of(Sort.Direction.ASC));
        when(sortHolder.sourceTypes()).thenReturn(Set.of(SourceType.QUERY_PARAM));
        when(sortHolder.getExpression(root, criteriaQuery, criteriaBuilder)).thenAnswer(invocation -> Optional.of(stringExpression));
        when(errorHolder.bindingResult()).thenReturn(bindingResult);
        when(errorHolder.methodParameter()).thenReturn(getSortMethodParameter());
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() ->
            sortBuilder.buildOrderForWrapper(root, criteriaQuery, criteriaBuilder, sortContext, wrapper, errorHolder))
            .isInstanceOf(QueryBuilderConfigurationException.class);
    }

    private MethodParameter getSortMethodParameter() {
        try {
            return new MethodParameter(
                QueryFilterBuilderImp.class
                    .getMethod(
                        "buildSortSpecification",
                        SortContext.class
                    ),
                0
            );
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
