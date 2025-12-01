package io.github._0xorigin.queryfilterbuilder.base.builders;

import io.github._0xorigin.queryfilterbuilder.FilterContext;
import io.github._0xorigin.queryfilterbuilder.QueryFilterBuilderImp;
import io.github._0xorigin.queryfilterbuilder.base.dtos.FilterRequest;
import io.github._0xorigin.queryfilterbuilder.base.enumfield.AbstractEnumFilterField;
import io.github._0xorigin.queryfilterbuilder.base.enums.FilterType;
import io.github._0xorigin.queryfilterbuilder.base.enums.MessageKey;
import io.github._0xorigin.queryfilterbuilder.base.enums.SourceType;
import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractFilterField;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.FilterOperator;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;
import io.github._0xorigin.queryfilterbuilder.base.functions.CustomFilterFunction;
import io.github._0xorigin.queryfilterbuilder.base.generators.PathGenerator;
import io.github._0xorigin.queryfilterbuilder.base.holders.CustomFilterHolder;
import io.github._0xorigin.queryfilterbuilder.base.holders.ErrorHolder;
import io.github._0xorigin.queryfilterbuilder.base.holders.FilterHolder;
import io.github._0xorigin.queryfilterbuilder.base.parsers.FilterParser;
import io.github._0xorigin.queryfilterbuilder.base.services.LocalizationService;
import io.github._0xorigin.queryfilterbuilder.base.utils.FilterUtils;
import io.github._0xorigin.queryfilterbuilder.base.wrappers.FilterErrorWrapper;
import io.github._0xorigin.queryfilterbuilder.base.wrappers.FilterWrapper;
import io.github._0xorigin.queryfilterbuilder.entities.User;
import io.github._0xorigin.queryfilterbuilder.exceptions.QueryBuilderConfigurationException;
import io.github._0xorigin.queryfilterbuilder.registries.FilterFieldRegistry;
import io.github._0xorigin.queryfilterbuilder.registries.FilterOperatorRegistry;
import jakarta.persistence.criteria.*;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilterBuilderImpTest {

    @Mock
    private PathGenerator<User> fieldPathGenerator;

    @Mock
    private FilterParser filterParser;

    @Mock
    private FilterFieldRegistry filterFieldRegistry;

    @Mock
    private FilterOperatorRegistry filterOperatorRegistry;

    @Mock
    private AbstractEnumFilterField enumFilterField;

    @Mock
    private LocalizationService localizationService;

    @Mock
    private Root<User> root;

    @Mock
    private CriteriaQuery<?> criteriaQuery;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private FilterContext<User> filterContext;

    @Mock
    private ErrorHolder errorHolder;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private Expression<String> stringExpression;

    @Mock
    private Expression<OffsetDateTime> dateTimeExpression;

    @Mock
    private AbstractFilterField<? extends Comparable<?>> filterField;

    @Mock
    private FilterOperator filterOperator;

    @Mock
    private Predicate predicate;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private FilterBuilderImp<User> filterBuilder;

    private enum TestEnum {
        VALUE1, VALUE2
    }

    @Test
    void getDistinctFilterWrappers_WithHttpServletRequest_ReturnsParsedWrappers() {
        // Arrange
        FilterWrapper wrapper = new FilterWrapper("firstName", "firstName", Operator.EQ, List.of("John"), SourceType.QUERY_PARAM, Optional.empty());
        List<FilterWrapper> parsedWrappers = List.of(wrapper);
        when(filterContext.getRequest()).thenReturn(Optional.of(httpServletRequest));
        when(filterContext.getFilterRequests()).thenReturn(Optional.empty());
        when(filterParser.parse(httpServletRequest)).thenReturn(parsedWrappers);
        FilterHolder<User, String> filterHolder = mock(FilterHolder.class);
        when(filterContext.getFilters()).thenReturn(Map.of("firstName", filterHolder));
        when(filterHolder.operators()).thenReturn(Set.of(Operator.EQ));
        when(filterHolder.sourceTypes()).thenReturn(Set.of(SourceType.QUERY_PARAM));

        // Act
        Collection<FilterWrapper> result = filterBuilder.getDistinctFilterWrappers(filterContext);

        // Assert
        assertThat(result).hasSize(1).containsExactly(wrapper.withFilterType(FilterType.NORMAL));
        verify(filterParser).parse(httpServletRequest);
    }

    @Test
    void getDistinctFilterWrappers_WithFilterRequests_ReturnsParsedWrappers() {
        // Arrange
        List<FilterRequest> filterRequests = List.of(new FilterRequest("lastLogin", Operator.EQ.name(), "2025-09-08T12:00:00Z"));
        FilterWrapper wrapper = new FilterWrapper("lastLogin", "lastLogin", Operator.EQ, List.of("2025-09-08T12:00:00Z"), SourceType.REQUEST_BODY, Optional.empty());
        List<FilterWrapper> parsedWrappers = List.of(wrapper);
        when(filterContext.getRequest()).thenReturn(Optional.empty());
        when(filterContext.getFilterRequests()).thenReturn(Optional.of(filterRequests));
        when(filterParser.parse(filterRequests)).thenReturn(parsedWrappers);
        CustomFilterHolder<User, OffsetDateTime> customFilterHolder = mock(CustomFilterHolder.class);
        when(filterContext.getCustomFilters()).thenReturn(Map.of("lastLogin", customFilterHolder));
        when(customFilterHolder.sourceTypes()).thenReturn(Set.of(SourceType.REQUEST_BODY));

        // Act
        Collection<FilterWrapper> result = filterBuilder.getDistinctFilterWrappers(filterContext);

        // Assert
        assertThat(result).hasSize(1).containsExactly(wrapper.withFilterType(FilterType.CUSTOM));
        verify(filterParser).parse(filterRequests);
    }

    @Test
    void getDistinctFilterWrappers_NoRequestOrFilterRequests_ReturnsEmpty() {
        // Arrange
        when(filterContext.getRequest()).thenReturn(Optional.empty());
        when(filterContext.getFilterRequests()).thenReturn(Optional.empty());

        // Act
        Collection<FilterWrapper> result = filterBuilder.getDistinctFilterWrappers(filterContext);

        // Assert
        assertThat(result).isEmpty();
        verify(filterParser, never()).parse(any(HttpServletRequest.class));
        verify(filterParser, never()).parse(any(List.class));
    }

    @Test
    void buildPredicateForWrapper_NoFilterType_ReturnsEmpty() {
        // Arrange
        FilterWrapper wrapper = new FilterWrapper("firstName", "firstName", Operator.EQ, List.of("John"), SourceType.QUERY_PARAM, Optional.empty());

        // Act
        Optional<Predicate> result = filterBuilder.buildPredicateForWrapper(root, criteriaQuery, criteriaBuilder, filterContext, wrapper, errorHolder);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void buildPredicateForWrapper_NormalFilterType_ValidStringFilter_ReturnsPredicate() {
        // Arrange
        List<String> values = List.of("John");
        FilterWrapper wrapper = new FilterWrapper("firstName", "firstName", Operator.EQ, values, SourceType.QUERY_PARAM, Optional.of(FilterType.NORMAL));
        FilterHolder<User, String> filterHolder = mock(FilterHolder.class);
        when(filterContext.getFilters()).thenReturn(Map.of("firstName", filterHolder));
        when(filterHolder.operators()).thenReturn(Set.of(Operator.EQ));
        when(filterHolder.sourceTypes()).thenReturn(Set.of(SourceType.QUERY_PARAM));
        when(filterHolder.getExpression(root, criteriaQuery, criteriaBuilder)).thenAnswer(invocation -> Optional.of(stringExpression));
        when(stringExpression.getJavaType()).thenAnswer(invocation -> String.class);
        when(filterFieldRegistry.getFilterField(String.class)).thenAnswer(invocation -> filterField);
        when(filterOperatorRegistry.getOperator(Operator.EQ)).thenReturn(filterOperator);
        when(filterField.getSupportedOperators()).thenReturn(Set.of(Operator.EQ));
        when(filterField.safeCast(eq("John"), any(FilterErrorWrapper.class))).thenAnswer(invocation -> "John");
        when(filterOperator.apply(eq(stringExpression), eq(criteriaBuilder), eq(values), any(FilterErrorWrapper.class))).thenReturn(Optional.of(predicate));
        when(errorHolder.bindingResult()).thenReturn(bindingResult);

        // Act
        Optional<Predicate> result = filterBuilder.buildPredicateForWrapper(root, criteriaQuery, criteriaBuilder, filterContext, wrapper, errorHolder);

        // Assert
        assertThat(result).isPresent().contains(predicate);
        verify(filterOperator).apply(eq(stringExpression), eq(criteriaBuilder), eq(values), any(FilterErrorWrapper.class));
    }

    @Test
    void buildPredicateForWrapper_NormalFilterType_ValidDateTimeFilter_ReturnsPredicate() {
        // Arrange
        String dateStr = "2025-09-08T12:00:00Z";
        OffsetDateTime dateTime = OffsetDateTime.parse(dateStr);
        List<OffsetDateTime> values = List.of(dateTime);
        FilterWrapper wrapper = new FilterWrapper("lastLogin", "lastLogin", Operator.GT, List.of(dateStr), SourceType.QUERY_PARAM, Optional.of(FilterType.NORMAL));
        FilterHolder<User, OffsetDateTime> filterHolder = mock(FilterHolder.class);
        when(filterContext.getFilters()).thenReturn(Map.of("lastLogin", filterHolder));
        when(filterHolder.operators()).thenReturn(Set.of(Operator.GT));
        when(filterHolder.sourceTypes()).thenReturn(Set.of(SourceType.QUERY_PARAM));
        when(filterHolder.getExpression(root, criteriaQuery, criteriaBuilder)).thenAnswer(invocation -> Optional.of(dateTimeExpression));
        when(dateTimeExpression.getJavaType()).thenAnswer(invocation -> OffsetDateTime.class);
        when(filterFieldRegistry.getFilterField(OffsetDateTime.class)).thenAnswer(invocation -> filterField);
        when(filterOperatorRegistry.getOperator(Operator.GT)).thenReturn(filterOperator);
        when(filterField.getSupportedOperators()).thenReturn(Set.of(Operator.GT));
        when(filterField.safeCast(eq(dateStr), any())).thenAnswer(invocation -> dateTime);
        when(filterOperator.apply(eq(dateTimeExpression), eq(criteriaBuilder), eq(values), any())).thenReturn(Optional.of(predicate));
        when(errorHolder.bindingResult()).thenReturn(bindingResult);

        // Act
        Optional<Predicate> result = filterBuilder.buildPredicateForWrapper(root, criteriaQuery, criteriaBuilder, filterContext, wrapper, errorHolder);

        // Assert
        assertThat(result).isPresent().contains(predicate);
        verify(filterOperator).apply(eq(dateTimeExpression), eq(criteriaBuilder), eq(values), any());
    }

    @Test
    void buildPredicateForWrapper_NormalFilterType_ProviderFunctionPresent_ReturnsPredicate() {
        // Arrange
        List<String> values = List.of("John");
        FilterWrapper wrapper = new FilterWrapper("firstName", "firstName", Operator.EQ, values, SourceType.QUERY_PARAM, Optional.of(FilterType.NORMAL));
        FilterHolder<User, String> filterHolder = mock(FilterHolder.class);
        when(filterContext.getFilters()).thenReturn(Map.of("firstName", filterHolder));
        when(filterHolder.operators()).thenReturn(Set.of(Operator.EQ));
        when(filterHolder.sourceTypes()).thenReturn(Set.of(SourceType.QUERY_PARAM));
        when(filterHolder.getExpression(root, criteriaQuery, criteriaBuilder)).thenAnswer(invocation -> Optional.of(stringExpression));
        when(stringExpression.getJavaType()).thenAnswer(invocation -> String.class);
        when(filterFieldRegistry.getFilterField(String.class)).thenAnswer(invocation -> filterField);
        when(filterOperatorRegistry.getOperator(Operator.EQ)).thenReturn(filterOperator);
        when(filterField.getSupportedOperators()).thenReturn(Set.of(Operator.EQ));
        when(filterField.safeCast(eq("John"), any(FilterErrorWrapper.class))).thenAnswer(invocation -> "John");
        when(filterOperator.apply(eq(stringExpression), eq(criteriaBuilder), eq(values), any(FilterErrorWrapper.class))).thenReturn(Optional.of(predicate));
        when(errorHolder.bindingResult()).thenReturn(bindingResult);

        // Act
        Optional<Predicate> result = filterBuilder.buildPredicateForWrapper(root, criteriaQuery, criteriaBuilder, filterContext, wrapper, errorHolder);

        // Assert
        assertThat(result).isPresent().contains(predicate);
        verify(filterOperator).apply(eq(stringExpression), eq(criteriaBuilder), eq(values), any(FilterErrorWrapper.class));
        verify(fieldPathGenerator, never()).generate(any(), anyString(), anyString(), any());
    }

    @Test
    void buildPredicateForWrapper_NormalFilterType_NoProviderFunction_UsesPathGenerator() {
        // Arrange
        List<String> values = List.of("Doe");
        FilterWrapper wrapper = new FilterWrapper("lastName", "lastName", Operator.EQ, values, SourceType.QUERY_PARAM, Optional.of(FilterType.NORMAL));
        FilterHolder<User, String> filterHolder = mock(FilterHolder.class);
        when(filterContext.getFilters()).thenReturn(Map.of("lastName", filterHolder));
        when(filterHolder.operators()).thenReturn(Set.of(Operator.EQ));
        when(filterHolder.sourceTypes()).thenReturn(Set.of(SourceType.QUERY_PARAM));
        when(filterHolder.getExpression(root, criteriaQuery, criteriaBuilder)).thenReturn(Optional.empty());
        when(fieldPathGenerator.generate(root, "lastName", "lastName", bindingResult)).thenAnswer(invocation -> stringExpression);
        when(stringExpression.getJavaType()).thenAnswer(invocation -> String.class);
        when(filterFieldRegistry.getFilterField(String.class)).thenAnswer(invocation -> filterField);
        when(filterOperatorRegistry.getOperator(Operator.EQ)).thenReturn(filterOperator);
        when(filterField.getSupportedOperators()).thenReturn(Set.of(Operator.EQ));
        when(filterField.safeCast(eq("Doe"), any())).thenAnswer(invocation -> "Doe");
        when(filterOperator.apply(eq(stringExpression), eq(criteriaBuilder), eq(values), any())).thenReturn(Optional.of(predicate));
        when(errorHolder.bindingResult()).thenReturn(bindingResult);

        // Act
        Optional<Predicate> result = filterBuilder.buildPredicateForWrapper(root, criteriaQuery, criteriaBuilder, filterContext, wrapper, errorHolder);

        // Assert
        assertThat(result).isPresent().contains(predicate);
        verify(fieldPathGenerator).generate(root, "lastName", "lastName", bindingResult);
        verify(filterOperator).apply(eq(stringExpression), eq(criteriaBuilder), eq(values), any());
    }

    @Test
    void buildPredicateForWrapper_NormalFilterType_InvalidFilter_ReturnsEmpty() {
        // Arrange
        FilterWrapper wrapper = new FilterWrapper("firstName", "firstName", Operator.EQ, List.of("John"), SourceType.QUERY_PARAM, Optional.of(FilterType.NORMAL));
        when(filterContext.getFilters()).thenReturn(Map.of());

        // Act
        Optional<Predicate> result = filterBuilder.buildPredicateForWrapper(root, criteriaQuery, criteriaBuilder, filterContext, wrapper, errorHolder);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void buildPredicateForWrapper_CustomFilterType_ValidFilter_ReturnsPredicate() {
        // Arrange
        List<String> values = List.of("Doe");
        FilterWrapper wrapper = new FilterWrapper("lastName", "lastName", Operator.EQ, values, SourceType.REQUEST_BODY, Optional.of(FilterType.CUSTOM));
        CustomFilterHolder<User, String> customFilterHolder = mock(CustomFilterHolder.class);
        when(filterContext.getCustomFilters()).thenReturn(Map.of("lastName", customFilterHolder));
        when(customFilterHolder.sourceTypes()).thenReturn(Set.of(SourceType.REQUEST_BODY));
        when(customFilterHolder.dataType()).thenReturn(String.class);
        when(filterFieldRegistry.getFilterField(String.class)).thenAnswer(invocation -> filterField);
        when(filterOperatorRegistry.getOperator(Operator.EQ)).thenReturn(filterOperator);
        when(filterField.getSupportedOperators()).thenReturn(Set.of(Operator.EQ));
        when(filterField.safeCast(eq("Doe"), any())).thenAnswer(invocation -> "Doe");
        when(customFilterHolder.customFilterFunction()).thenReturn((r, q, cb, vals, error) -> Optional.of(predicate));
        when(errorHolder.bindingResult()).thenReturn(bindingResult);

        // Act
        Optional<Predicate> result = filterBuilder.buildPredicateForWrapper(root, criteriaQuery, criteriaBuilder, filterContext, wrapper, errorHolder);

        // Assert
        assertThat(result).isPresent().contains(predicate);
    }

    @Test
    void buildPredicateForWrapper_CustomFilterType_InvalidFilter_ReturnsEmpty() {
        // Arrange
        FilterWrapper wrapper = new FilterWrapper("lastName", "lastName", Operator.EQ, List.of("Doe"), SourceType.REQUEST_BODY, Optional.of(FilterType.CUSTOM));
        when(filterContext.getCustomFilters()).thenReturn(Map.of());

        // Act
        Optional<Predicate> result = filterBuilder.buildPredicateForWrapper(root, criteriaQuery, criteriaBuilder, filterContext, wrapper, errorHolder);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void buildPredicateForWrapper_NormalFilterType_HasError_ThrowsException() {
        // Arrange
        List<String> values = List.of("John");
        FilterWrapper wrapper = new FilterWrapper("firstName", "firstName", Operator.EQ, values, SourceType.QUERY_PARAM, Optional.of(FilterType.NORMAL));
        FilterHolder<User, String> filterHolder = mock(FilterHolder.class);
        when(filterContext.getFilters()).thenReturn(Map.of("firstName", filterHolder));
        when(filterHolder.operators()).thenReturn(Set.of(Operator.EQ));
        when(filterHolder.sourceTypes()).thenReturn(Set.of(SourceType.QUERY_PARAM));
        when(filterHolder.getExpression(root, criteriaQuery, criteriaBuilder)).thenAnswer(invocation -> Optional.of(stringExpression));
        when(errorHolder.bindingResult()).thenReturn(bindingResult);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(errorHolder.methodParameter()).thenReturn(getFilterMethodParameter());

        // Assert
        assertThatThrownBy(() -> filterBuilder.buildPredicateForWrapper(root, criteriaQuery, criteriaBuilder, filterContext, wrapper, errorHolder))
            .isInstanceOf(QueryBuilderConfigurationException.class);
    }

    @Test
    void buildPredicateForWrapper_NormalFilterType_UnsupportedOperator_AddsError() {
        // Arrange
        List<String> values = List.of("John");
        FilterWrapper wrapper = new FilterWrapper("firstName", "firstName", Operator.GT, values, SourceType.QUERY_PARAM, Optional.of(FilterType.NORMAL));
        FilterHolder<User, String> filterHolder = mock(FilterHolder.class);
        when(filterContext.getFilters()).thenReturn(Map.of("firstName", filterHolder));
        when(filterHolder.operators()).thenReturn(Set.of(Operator.GT));
        when(filterHolder.sourceTypes()).thenReturn(Set.of(SourceType.QUERY_PARAM));
        when(filterHolder.getExpression(root, criteriaQuery, criteriaBuilder)).thenAnswer(invocation -> Optional.of(stringExpression));
        when(stringExpression.getJavaType()).thenAnswer(invocation -> String.class);
        when(filterFieldRegistry.getFilterField(String.class)).thenAnswer(invocation -> filterField);
        when(filterOperatorRegistry.getOperator(Operator.GT)).thenReturn(filterOperator);
        when(filterField.getSupportedOperators()).thenReturn(Set.of(Operator.EQ));
        when(localizationService.getMessage(Operator.GT.getValue())).thenReturn("Greater than");
        when(localizationService.getMessage(MessageKey.OPERATOR_NOT_SUPPORTED.getCode(), "Greater than")).thenReturn("Operator GT not supported");
        when(errorHolder.bindingResult()).thenReturn(bindingResult);
        when(bindingResult.getObjectName()).thenReturn("filter");
        doNothing().when(bindingResult).addError(any(FieldError.class));

        // Act
        Optional<Predicate> result = filterBuilder.buildPredicateForWrapper(root, criteriaQuery, criteriaBuilder, filterContext, wrapper, errorHolder);

        // Assert
        assertThat(result).isEmpty();
        verify(localizationService).getMessage(MessageKey.OPERATOR_NOT_SUPPORTED.getCode(), "Greater than");
        verify(bindingResult).addError(any(FieldError.class));
    }

    @Test
    void buildPredicateForWrapper_ErrorInBindingResult_ThrowsException() {
        // Arrange
        List<String> values = List.of("true");
        FilterWrapper wrapper = new FilterWrapper("isActive", "isActive", Operator.EQ, values, SourceType.QUERY_PARAM, Optional.of(FilterType.NORMAL));
        FilterHolder<User, Boolean> filterHolder = mock(FilterHolder.class);
        when(filterContext.getFilters()).thenReturn(Map.of("isActive", filterHolder));
        when(filterHolder.operators()).thenReturn(Set.of(Operator.EQ));
        when(filterHolder.sourceTypes()).thenReturn(Set.of(SourceType.QUERY_PARAM));
        when(filterHolder.getExpression(root, criteriaQuery, criteriaBuilder)).thenReturn(Optional.of(mock(Expression.class)));
        when(errorHolder.bindingResult()).thenReturn(bindingResult);
        when(errorHolder.methodParameter()).thenReturn(getFilterMethodParameter());
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() ->
            filterBuilder.buildPredicateForWrapper(root, criteriaQuery, criteriaBuilder, filterContext, wrapper, errorHolder))
            .isInstanceOf(QueryBuilderConfigurationException.class);
    }

    @Test
    void buildPredicateForWrapper_CustomFilterType_ErrorInBindingResult_ThrowsException() {
        // Arrange
        List<String> values = List.of("2025-09-08T12:00:00Z");
        FilterWrapper wrapper = new FilterWrapper("createdAt", "createdAt", Operator.EQ, values, SourceType.REQUEST_BODY, Optional.of(FilterType.CUSTOM));
        CustomFilterHolder<User, Instant> customFilterHolder = mock(CustomFilterHolder.class);
        when(filterContext.getCustomFilters()).thenReturn(Map.of("createdAt", customFilterHolder));
        when(customFilterHolder.sourceTypes()).thenReturn(Set.of(SourceType.REQUEST_BODY));
        when(customFilterHolder.dataType()).thenReturn(Instant.class);
        when(filterFieldRegistry.getFilterField(Instant.class)).thenAnswer(invocation -> filterField);
        when(filterOperatorRegistry.getOperator(Operator.EQ)).thenReturn(filterOperator);
        when(filterField.getSupportedOperators()).thenReturn(Set.of(Operator.EQ));
        when(errorHolder.bindingResult()).thenReturn(bindingResult);
        when(errorHolder.methodParameter()).thenReturn(getFilterMethodParameter());
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() ->
            filterBuilder.buildPredicateForWrapper(root, criteriaQuery, criteriaBuilder, filterContext, wrapper, errorHolder))
            .isInstanceOf(QueryBuilderConfigurationException.class);
    }

    @Test
    void getDistinctFilterWrappers_WithBothSources_BodyOverridesQuery() {
        // Arrange
        when(filterContext.getRequest()).thenReturn(Optional.of(httpServletRequest));
        when(filterContext.getFilterRequests()).thenReturn(Optional.of(List.of(new FilterRequest("firstName", Operator.EQ.name(), "Jane"))));

        FilterWrapper queryWrapper = new FilterWrapper("firstName", "firstName", Operator.EQ, List.of("John"), SourceType.QUERY_PARAM, Optional.empty());
        when(filterParser.parse(httpServletRequest)).thenReturn(List.of(queryWrapper));

        FilterWrapper bodyWrapper = new FilterWrapper("firstName", "firstName", Operator.EQ, List.of("Jane"), SourceType.REQUEST_BODY, Optional.empty());
        when(filterParser.parse(anyList())).thenReturn(List.of(bodyWrapper));

        FilterHolder<User, String> filterHolder = mock(FilterHolder.class);
        when(filterContext.getFilters()).thenReturn(Map.of("firstName", filterHolder));
        when(filterHolder.operators()).thenReturn(Set.of(Operator.EQ));
        when(filterHolder.sourceTypes()).thenReturn(Set.of(SourceType.QUERY_PARAM, SourceType.REQUEST_BODY));

        // Act
        Collection<FilterWrapper> result = filterBuilder.getDistinctFilterWrappers(filterContext);

        // Assert
        assertThat(result).hasSize(1);
        FilterWrapper resWrapper = result.iterator().next();
        assertThat(resWrapper.values()).containsExactly("Jane");
        assertThat(resWrapper.sourceType()).isEqualTo(SourceType.REQUEST_BODY);
        assertThat(resWrapper.filterType()).contains(FilterType.NORMAL);
        verify(filterParser).parse(httpServletRequest);
        verify(filterParser).parse(anyList());
    }

    @Test
    void buildPredicateForWrapper_NormalFilterType_ValidEnumFilter_ReturnsPredicate() {
        // Arrange
        List<String> values = List.of("VALUE1");
        FilterWrapper wrapper = new FilterWrapper("enumField", "enumField", Operator.EQ, values, SourceType.QUERY_PARAM, Optional.of(FilterType.NORMAL));
        FilterHolder<User, TestEnum> filterHolder = mock(FilterHolder.class);
        when(filterContext.getFilters()).thenReturn(Map.of("enumField", filterHolder));
        when(filterHolder.operators()).thenReturn(Set.of(Operator.EQ));
        when(filterHolder.sourceTypes()).thenReturn(Set.of(SourceType.QUERY_PARAM));
        Expression<TestEnum> enumExpression = mock(Expression.class);
        when(filterHolder.getExpression(root, criteriaQuery, criteriaBuilder)).thenAnswer(invocation -> Optional.of(enumExpression));
        when(enumExpression.getJavaType()).thenAnswer(invocation -> TestEnum.class);
        when(filterOperatorRegistry.getOperator(Operator.EQ)).thenReturn(filterOperator);
        when(enumFilterField.getSupportedOperators()).thenReturn(Set.of(Operator.EQ));
        when(enumFilterField.safeCast(eq(TestEnum.class), eq("VALUE1"), any(FilterErrorWrapper.class))).thenReturn(TestEnum.VALUE1);
        List<TestEnum> enumValues = List.of(TestEnum.VALUE1);
        when(filterOperator.apply(eq(enumExpression), eq(criteriaBuilder), eq(enumValues), any(FilterErrorWrapper.class))).thenReturn(Optional.of(predicate));
        when(errorHolder.bindingResult()).thenReturn(bindingResult);

        // Act
        Optional<Predicate> result = filterBuilder.buildPredicateForWrapper(root, criteriaQuery, criteriaBuilder, filterContext, wrapper, errorHolder);

        // Assert
        assertThat(result).isPresent().contains(predicate);
        verify(enumFilterField).safeCast(eq(TestEnum.class), eq("VALUE1"), any());
        verify(filterOperator).apply(eq(enumExpression), eq(criteriaBuilder), eq(enumValues), any());
    }

    @Test
    void buildPredicateForWrapper_CustomFilterType_ValidEnumFilter_ReturnsPredicate() {
        // Arrange
        List<String> values = List.of("VALUE1");
        FilterWrapper wrapper = new FilterWrapper("enumField", "enumField", Operator.EQ, values, SourceType.REQUEST_BODY, Optional.of(FilterType.CUSTOM));
        CustomFilterHolder<User, TestEnum> customFilterHolder = mock(CustomFilterHolder.class);
        when(filterContext.getCustomFilters()).thenReturn(Map.of("enumField", customFilterHolder));
        when(customFilterHolder.sourceTypes()).thenReturn(Set.of(SourceType.REQUEST_BODY));
        when(customFilterHolder.dataType()).thenReturn(TestEnum.class);
        when(filterOperatorRegistry.getOperator(Operator.EQ)).thenReturn(filterOperator);
        when(enumFilterField.getSupportedOperators()).thenReturn(Set.of(Operator.EQ));
        when(enumFilterField.safeCast(eq(TestEnum.class), eq("VALUE1"), any(FilterErrorWrapper.class))).thenReturn(TestEnum.VALUE1);
        when(customFilterHolder.customFilterFunction()).thenReturn(mock(CustomFilterFunction.class));
        when(customFilterHolder.customFilterFunction().apply(any(), any(), any(), any(), any())).thenAnswer(invocation -> Optional.of(predicate));
        when(errorHolder.bindingResult()).thenReturn(bindingResult);

        // Act
        Optional<Predicate> result = filterBuilder.buildPredicateForWrapper(root, criteriaQuery, criteriaBuilder, filterContext, wrapper, errorHolder);

        // Assert
        assertThat(result).isPresent().contains(predicate);
        verify(enumFilterField).safeCast(eq(TestEnum.class), eq("VALUE1"), any());
        verify(customFilterHolder.customFilterFunction()).apply(any(), any(), any(), any(), any());
    }

    @Test
    void buildPredicateForWrapper_NormalFilterType_UnsupportedOperatorForEnum_ThrowsException() {
        // Arrange
        List<String> values = List.of("VALUE1");
        FilterWrapper wrapper = new FilterWrapper("enumField", "enumField", Operator.GT, values, SourceType.QUERY_PARAM, Optional.of(FilterType.NORMAL));
        FilterHolder<User, TestEnum> filterHolder = mock(FilterHolder.class);
        when(filterContext.getFilters()).thenReturn(Map.of("enumField", filterHolder));
        when(filterHolder.operators()).thenReturn(Set.of(Operator.GT));
        when(filterHolder.sourceTypes()).thenReturn(Set.of(SourceType.QUERY_PARAM));
        Expression<TestEnum> enumExpression = mock(Expression.class);
        when(filterHolder.getExpression(root, criteriaQuery, criteriaBuilder)).thenAnswer(invocation -> Optional.of(enumExpression));
        when(errorHolder.bindingResult()).thenReturn(bindingResult);
        when(errorHolder.methodParameter()).thenReturn(getFilterMethodParameter());
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> filterBuilder.buildPredicateForWrapper(root, criteriaQuery, criteriaBuilder, filterContext, wrapper, errorHolder))
            .isInstanceOf(QueryBuilderConfigurationException.class);
    }

    @Test
    void buildPredicateForWrapper_NormalFilterType_InvalidEnumValue_AddsError() {
        // Arrange
        List<String> values = List.of("INVALID");
        FilterWrapper wrapper = new FilterWrapper("enumField", "enumField", Operator.EQ, values, SourceType.QUERY_PARAM, Optional.of(FilterType.NORMAL));
        FilterHolder<User, TestEnum> filterHolder = mock(FilterHolder.class);
        when(filterContext.getFilters()).thenReturn(Map.of("enumField", filterHolder));
        when(filterHolder.operators()).thenReturn(Set.of(Operator.EQ));
        when(filterHolder.sourceTypes()).thenReturn(Set.of(SourceType.QUERY_PARAM));
        Expression<TestEnum> enumExpression = mock(Expression.class);
        when(filterHolder.getExpression(root, criteriaQuery, criteriaBuilder)).thenAnswer(invocation -> Optional.of(enumExpression));
        when(enumExpression.getJavaType()).thenAnswer(invocation -> TestEnum.class);
        when(filterOperatorRegistry.getOperator(Operator.EQ)).thenReturn(filterOperator);
        when(enumFilterField.getSupportedOperators()).thenReturn(Set.of(Operator.EQ));
        when(enumFilterField.safeCast(eq(TestEnum.class), eq("INVALID"), any(FilterErrorWrapper.class))).thenAnswer(invocation -> {
            FilterErrorWrapper err = invocation.getArgument(2);
            FilterUtils.addFieldError(
                err.bindingResult(),
                err.filterWrapper().originalFieldName(),
                "INVALID",
                "Not a valid enum value"
            );
            return null;
        });
        List<TestEnum> enumValues = Collections.singletonList(null);
        when(filterOperator.apply(eq(enumExpression), eq(criteriaBuilder), eq(enumValues), any(FilterErrorWrapper.class))).thenReturn(Optional.empty());
        when(errorHolder.bindingResult()).thenReturn(bindingResult);
        when(bindingResult.getObjectName()).thenReturn("filter");

        // Act
        Optional<Predicate> result = filterBuilder.buildPredicateForWrapper(root, criteriaQuery, criteriaBuilder, filterContext, wrapper, errorHolder);

        // Assert
        assertThat(result).isEmpty();
        verify(bindingResult).addError(any());
        verify(filterOperator).apply(eq(enumExpression), eq(criteriaBuilder), eq(enumValues), any());
    }

    private MethodParameter getFilterMethodParameter() {
        try {
            return new MethodParameter(
                QueryFilterBuilderImp.class
                    .getMethod(
                        "buildFilterSpecification",
                        FilterContext.class
                    ),
                0
            );
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
