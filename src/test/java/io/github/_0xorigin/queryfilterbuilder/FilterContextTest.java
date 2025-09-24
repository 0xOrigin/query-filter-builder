package io.github._0xorigin.queryfilterbuilder;

import io.github._0xorigin.queryfilterbuilder.base.dtos.FilterRequest;
import io.github._0xorigin.queryfilterbuilder.base.enums.SourceType;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;
import io.github._0xorigin.queryfilterbuilder.base.functions.CustomFilterFunction;
import io.github._0xorigin.queryfilterbuilder.base.functions.ExpressionProviderFunction;
import io.github._0xorigin.queryfilterbuilder.entities.FakeUser;
import io.github._0xorigin.queryfilterbuilder.entities.User;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class FilterContextTest {

    @Mock
    private HttpServletRequest request;

    @Test
    void buildTemplateForType_nullType_throwsNullPointerException() {
        assertThatThrownBy(() -> FilterContext.buildTemplateForType(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Type must not be null");
    }

    @Test
    void buildTemplateForType_nonEntityClass_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> FilterContext.buildTemplateForType(FakeUser.class))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Class " + FakeUser.class.getName() + " is not a JPA Entity");
    }

    @Test
    void templateBuilder_queryParam_nullConsumer_throwsNullPointerException() {
        FilterContext.TemplateBuilder<User> builder = FilterContext.buildTemplateForType(User.class);
        assertThatThrownBy(() -> builder.queryParam(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Consumer for FilterConfigurer must not be null");
    }

    @Test
    void templateBuilder_requestBody_nullConsumer_throwsNullPointerException() {
        FilterContext.TemplateBuilder<User> builder = FilterContext.buildTemplateForType(User.class);
        assertThatThrownBy(() -> builder.requestBody(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Consumer for FilterConfigurer must not be null");
    }

    @Test
    void sourceBuilder_withQuerySource_nullRequest_throwsNullPointerException() {
        FilterContext.Template<User> template = FilterContext.buildTemplateForType(User.class).buildTemplate();
        FilterContext.SourceBuilder<User> sourceBuilder = template.newSourceBuilder();
        assertThatThrownBy(() -> sourceBuilder.withQuerySource(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("HttpServletRequest must not be null");
    }

    @Test
    void sourceBuilder_withBodySource_nullFilterRequests_throwsNullPointerException() {
        FilterContext.Template<User> template = FilterContext.buildTemplateForType(User.class).buildTemplate();
        FilterContext.SourceBuilder<User> sourceBuilder = template.newSourceBuilder();
        assertThatThrownBy(() -> sourceBuilder.withBodySource(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("FilterRequests must not be null");
    }

    @Test
    void filterConfigurer_addFilter_nullFieldName_throwsNullPointerException() {
        FilterContext.TemplateBuilder<User> builder = FilterContext.buildTemplateForType(User.class);
        assertThatThrownBy(() -> builder.queryParam(c -> c.addFilter(null, Operator.EQ)))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Field name must not be null");
    }

    @Test
    void filterConfigurer_addFilter_nullOperators_throwsNullPointerException() {
        FilterContext.TemplateBuilder<User> builder = FilterContext.buildTemplateForType(User.class);
        assertThatThrownBy(() -> builder.queryParam(c -> c.addFilter("field", (Operator[]) null)))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Operators must not be null");
    }

    @Test
    void filterConfigurer_addFilterWithExpression_nullFieldName_throwsNullPointerException() {
        ExpressionProviderFunction<User, String> expressionProvider = (root, cq, cb) -> root.get("firstName");
        FilterContext.TemplateBuilder<User> builder = FilterContext.buildTemplateForType(User.class);
        assertThatThrownBy(() -> builder.queryParam(c -> c.addFilter(null, expressionProvider, Operator.EQ)))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Field name must not be null");
    }

    @Test
    void filterConfigurer_addFilterWithExpression_nullExpressionProvider_throwsNullPointerException() {
        FilterContext.TemplateBuilder<User> builder = FilterContext.buildTemplateForType(User.class);
        assertThatThrownBy(() -> builder.queryParam(c -> c.addFilter("field", (ExpressionProviderFunction<User, ?>) null, Operator.EQ)))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Expression provider function must not be null");
    }

    @Test
    void filterConfigurer_addFilterWithExpression_nullOperators_throwsNullPointerException() {
        ExpressionProviderFunction<User, String> expressionProvider = (root, cq, cb) -> root.get("firstName");
        FilterContext.TemplateBuilder<User> builder = FilterContext.buildTemplateForType(User.class);
        assertThatThrownBy(() -> builder.queryParam(c -> c.addFilter("field", expressionProvider, (Operator[]) null)))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Operators must not be null");
    }

    @Test
    void filterConfigurer_addCustomFilter_nullFilterName_throwsNullPointerException() {
        CustomFilterFunction<User> customFilterFunction = (root, cq, cb, values, errorWrapper)
            -> Optional.ofNullable(cb.equal(root.get("firstName"), values.get(0)));
        FilterContext.TemplateBuilder<User> builder = FilterContext.buildTemplateForType(User.class);
        assertThatThrownBy(() -> builder.queryParam(c -> c.addCustomFilter(null, String.class, customFilterFunction)))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Filter name must not be null");
    }

    @Test
    void filterConfigurer_addCustomFilter_nullDataType_throwsNullPointerException() {
        CustomFilterFunction<User> customFilterFunction = (root, cq, cb, values, errorWrapper)
            -> Optional.ofNullable(cb.equal(root.get("firstName"), values.get(0)));
        FilterContext.TemplateBuilder<User> builder = FilterContext.buildTemplateForType(User.class);
        assertThatThrownBy(() -> builder.queryParam(c -> c.addCustomFilter("cust", null, customFilterFunction)))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Data type for input must not be null");
    }

    @Test
    void filterConfigurer_addCustomFilter_nullFilterFunction_throwsNullPointerException() {
        FilterContext.TemplateBuilder<User> builder = FilterContext.buildTemplateForType(User.class);
        assertThatThrownBy(() -> builder.queryParam(c -> c.addCustomFilter("cust", String.class, null)))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Filter function must not be null");
    }

    @Test
    void filterConfigurer_addCustomFilter_differentDataType_throwsIllegalArgumentException() {
        CustomFilterFunction<User> customFilterFunction = (root, cq, cb, values, errorWrapper)
            -> Optional.ofNullable(cb.equal(root.get("firstName"), values.get(0)));
        FilterContext.TemplateBuilder<User> builder = FilterContext.buildTemplateForType(User.class);
        builder.queryParam(c -> c.addCustomFilter("cust", String.class, customFilterFunction));
        assertThatThrownBy(() -> builder.queryParam(c -> c.addCustomFilter("cust", Integer.class, customFilterFunction)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Changing data type or filter function for existing custom filter is not allowed: cust");
    }

    @Test
    void filterConfigurer_addFilter_emptyOperators_throwsIllegalArgumentException() {
        FilterContext.TemplateBuilder<User> builder = FilterContext.buildTemplateForType(User.class);
        assertThatThrownBy(() -> builder.queryParam(c -> c.addFilter("field")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Operators must not be empty");
    }

    @Test
    void filterConfigurer_addFilterWithExpression_emptyOperators_throwsIllegalArgumentException() {
        ExpressionProviderFunction<User, String> expr = (root, cq, cb) -> root.get("firstName");
        FilterContext.TemplateBuilder<User> builder = FilterContext.buildTemplateForType(User.class);
        assertThatThrownBy(() -> builder.queryParam(c -> c.addFilter("field", expr)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Operators must not be empty");
    }

    @Test
    void buildTemplateForType_validEntity_returnsTemplateBuilder() {
        FilterContext.TemplateBuilder<User> builder = FilterContext.buildTemplateForType(User.class);
        assertThat(builder).isNotNull().isInstanceOf(FilterContext.TemplateBuilder.class);
    }

    @Test
    void templateBuilder_buildTemplate_returnsTemplate() {
        FilterContext.Template<User> template = FilterContext.buildTemplateForType(User.class).buildTemplate();
        assertThat(template).isNotNull().isInstanceOf(FilterContext.Template.class);

        FilterContext<User> context = template.newSourceBuilder().buildFilterContext();
        assertThat(context.getFilters()).isEmpty();
        assertThat(context.getCustomFilters()).isEmpty();
    }

    @Test
    void template_newSourceBuilder_returnsSourceBuilder() {
        FilterContext.Template<User> template = FilterContext.buildTemplateForType(User.class).buildTemplate();
        FilterContext.SourceBuilder<User> sourceBuilder = template.newSourceBuilder();
        assertThat(sourceBuilder).isNotNull().isInstanceOf(FilterContext.SourceBuilder.class);
    }

    @Test
    void sourceBuilder_buildFilterContext_withoutSources_returnsContextWithEmptyCollections() {
        FilterContext.Template<User> template = FilterContext.buildTemplateForType(User.class).buildTemplate();
        FilterContext.SourceBuilder<User> sourceBuilder = template.newSourceBuilder();
        FilterContext<User> context = sourceBuilder.buildFilterContext();
        assertThat(context).isNotNull();
        assertThat(context.getRequest()).isEmpty();
        assertThat(context.getFilterRequests()).isEmpty();
        assertThat(context.getFilters()).isEmpty();
        assertThat(context.getCustomFilters()).isEmpty();
    }

    @Test
    void sourceBuilder_withQuerySource_success_setsRequest() {
        FilterContext.Template<User> template = FilterContext.buildTemplateForType(User.class).buildTemplate();
        FilterContext.SourceBuilder<User> sourceBuilder = template.newSourceBuilder();
        sourceBuilder.withQuerySource(request);
        FilterContext<User> context = sourceBuilder.buildFilterContext();
        assertThat(context.getRequest()).isPresent().contains(request);
    }

    @Test
    void sourceBuilder_withBodySource_success_setsFilterRequests() {
        List<FilterRequest> filterRequests = List.of(new FilterRequest("firstName", Operator.EQ.getValue(), "Ahmed"));
        FilterContext.Template<User> template = FilterContext.buildTemplateForType(User.class).buildTemplate();
        FilterContext.SourceBuilder<User> sourceBuilder = template.newSourceBuilder();
        sourceBuilder.withBodySource(filterRequests);
        FilterContext<User> context = sourceBuilder.buildFilterContext();
        assertThat(context.getFilterRequests()).isPresent().contains(filterRequests);
    }

    @Test
    void sourceBuilder_withBothSources_success_setsBoth() {
        List<FilterRequest> filterRequests = List.of(new FilterRequest("firstName", Operator.EQ.getValue(), "Ahmed"));
        FilterContext.Template<User> template = FilterContext.buildTemplateForType(User.class).buildTemplate();

        FilterContext.SourceBuilder<User> sourceBuilder = template.newSourceBuilder();
        sourceBuilder.withQuerySource(request).withBodySource(filterRequests);
        FilterContext<User> context = sourceBuilder.buildFilterContext();

        assertThat(context.getRequest()).isPresent().contains(request);
        assertThat(context.getFilterRequests()).isPresent().contains(filterRequests);
    }

    @Test
    void filterConfigurer_addFilter_nullsInOperators_filtersOutNulls() {
        FilterContext.TemplateBuilder<User> builder = FilterContext.buildTemplateForType(User.class);
        builder.queryParam(c -> c.addFilter("field", Operator.EQ, null, Operator.NEQ));
        FilterContext<User> context = builder.buildTemplate().newSourceBuilder().buildFilterContext();
        var holder = context.getFilters().get("field");

        assertThat(holder).isNotNull();
        assertThat(holder.operators()).containsExactlyInAnyOrder(Operator.EQ, Operator.NEQ);
        assertThat(holder.sourceTypes()).containsExactly(SourceType.QUERY_PARAM);
        assertThat(holder.expressionProviderFunction()).isEmpty();
    }

    @Test
    void filterConfigurer_addFilter_multipleCalls_accumulatesOperatorsAndSources() {
        FilterContext.TemplateBuilder<User> builder = FilterContext.buildTemplateForType(User.class);
        builder.queryParam(c -> c.addFilter("field", Operator.EQ, Operator.NEQ));
        builder.requestBody(c -> c.addFilter("field", Operator.GT));
        FilterContext<User> context = builder.buildTemplate().newSourceBuilder().buildFilterContext();
        var holder = context.getFilters().get("field");

        assertThat(holder).isNotNull();
        assertThat(holder.operators()).containsExactlyInAnyOrder(Operator.EQ, Operator.NEQ, Operator.GT);
        assertThat(holder.sourceTypes()).containsExactlyInAnyOrder(SourceType.QUERY_PARAM, SourceType.REQUEST_BODY);
        assertThat(holder.expressionProviderFunction()).isEmpty();
    }

    @Test
    void filterConfigurer_addFilterWithExpression_success_setsExpression() {
        ExpressionProviderFunction<User, String> expr = (root, cq, cb) -> root.get("firstName");
        FilterContext.TemplateBuilder<User> builder = FilterContext.buildTemplateForType(User.class);
        builder.queryParam(c -> c.addFilter("field", expr, Operator.CONTAINS));
        FilterContext<User> context = builder.buildTemplate().newSourceBuilder().buildFilterContext();
        var holder = context.getFilters().get("field");

        assertThat(holder).isNotNull();
        assertThat(holder.operators()).containsExactly(Operator.CONTAINS);
        assertThat(holder.sourceTypes()).containsExactly(SourceType.QUERY_PARAM);
        assertThat(holder.expressionProviderFunction()).isPresent().get().isEqualTo(expr);
    }

    @Test
    void filterConfigurer_addFilterWithExpression_nullsInOperators_filtersOutNulls() {
        ExpressionProviderFunction<User, String> expr = (root, cq, cb) -> root.get("firstName");
        FilterContext.TemplateBuilder<User> builder = FilterContext.buildTemplateForType(User.class);
        builder.queryParam(c -> c.addFilter("field", expr, Operator.CONTAINS, null));
        FilterContext<User> context = builder.buildTemplate().newSourceBuilder().buildFilterContext();
        var holder = context.getFilters().get("field");

        assertThat(holder.operators()).containsExactly(Operator.CONTAINS);
    }

    @Test
    void filterConfigurer_addFilter_mixedWithAndWithoutExpression_overwritesExpressionWhenWith() {
        ExpressionProviderFunction<User, String> expr1 = (root, cq, cb) -> root.get("firstName");
        ExpressionProviderFunction<User, String> expr2 = (root, cq, cb) -> root.get("lastName");
        FilterContext.TemplateBuilder<User> builder = FilterContext.buildTemplateForType(User.class);
        builder.queryParam(c -> {
            c.addFilter("field", Operator.EQ); // no expr
            c.addFilter("field", expr1, Operator.NEQ); // sets expr1
            c.addFilter("field", Operator.GT); // keeps expr1
            c.addFilter("field", expr2, Operator.LT); // overwrites to expr2
        });
        FilterContext<User> context = builder.buildTemplate().newSourceBuilder().buildFilterContext();
        var holder = context.getFilters().get("field");
        assertThat(holder.operators()).containsExactlyInAnyOrder(Operator.EQ, Operator.NEQ, Operator.GT, Operator.LT);
        assertThat(holder.expressionProviderFunction()).isPresent().get().isEqualTo(expr2);
    }

    @Test
    void filterConfigurer_addCustomFilter_success_addsHolder() {
        CustomFilterFunction<User> customFilterFunction = (root, cq, cb, values, errorWrapper)
            -> Optional.ofNullable(cb.equal(root.get("firstName"), values.get(0)));
        FilterContext.TemplateBuilder<User> builder = FilterContext.buildTemplateForType(User.class);
        builder.queryParam(c -> c.addCustomFilter("cust", String.class, customFilterFunction));
        FilterContext<User> context = builder.buildTemplate().newSourceBuilder().buildFilterContext();
        var holder = context.getCustomFilters().get("cust");

        assertThat(holder).isNotNull();
        assertThat(holder.dataType()).isEqualTo(String.class);
        assertThat(holder.customFilterFunction()).isEqualTo(customFilterFunction);
        assertThat(holder.sourceTypes()).containsExactly(SourceType.QUERY_PARAM);
    }

    @Test
    void filterConfigurer_addCustomFilter_multipleCallsSameDataType_accumulatesSourcesKeepsFirstFunction() {
        CustomFilterFunction<User> func1 = (root, cq, cb, values, errorWrapper)
            -> Optional.ofNullable(cb.equal(root.get("firstName"), values.get(0)));
        CustomFilterFunction<User> func2 = (root, cq, cb, values, errorWrapper)
            -> Optional.ofNullable(cb.equal(root.get("lastName"), values.get(0)));
        FilterContext.TemplateBuilder<User> builder = FilterContext.buildTemplateForType(User.class);

        builder.queryParam(c -> c.addCustomFilter("cust", String.class, func1));
        builder.requestBody(c -> c.addCustomFilter("cust", String.class, func2));
        FilterContext<User> context = builder.buildTemplate().newSourceBuilder().buildFilterContext();
        var holder = context.getCustomFilters().get("cust");

        assertThat(holder.dataType()).isEqualTo(String.class);
        assertThat(holder.customFilterFunction()).isEqualTo(func1); // keeps first
        assertThat(holder.sourceTypes()).containsExactlyInAnyOrder(SourceType.QUERY_PARAM, SourceType.REQUEST_BODY);
    }

    @Test
    void filterContext_getFilters_returnsUnmodifiableCopy() {
        FilterContext.TemplateBuilder<User> builder = FilterContext.buildTemplateForType(User.class);
        builder.queryParam(c -> c.addFilter("field", Operator.EQ));
        FilterContext<User> context = builder.buildTemplate().newSourceBuilder().buildFilterContext();
        var filters = context.getFilters();

        assertThat(filters).hasSize(1);
        assertThatThrownBy(() -> filters.put("new", null))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void filterContext_getCustomFilters_returnsUnmodifiableCopy() {
        CustomFilterFunction<User> customFilterFunction = (root, cq, cb, values, errorWrapper)
            -> Optional.ofNullable(cb.equal(root.get("firstName"), values.get(0)));
        FilterContext.TemplateBuilder<User> builder = FilterContext.buildTemplateForType(User.class);
        builder.queryParam(c -> c.addCustomFilter("cust", String.class, customFilterFunction));
        FilterContext<User> context = builder.buildTemplate().newSourceBuilder().buildFilterContext();
        var customFilters = context.getCustomFilters();

        assertThat(customFilters).hasSize(1);
        assertThatThrownBy(() -> customFilters.put("new", null))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void filterConfigurer_maximumOperatorsPerField_success() {
        Operator[] allOperators = Operator.values();
        FilterContext.TemplateBuilder<User> builder = FilterContext.buildTemplateForType(User.class);
        builder.queryParam(c -> c.addFilter("field", allOperators));

        FilterContext<User> context = builder.buildTemplate().newSourceBuilder().buildFilterContext();
        var holder = context.getFilters().get("field");

        assertThat(holder.operators()).hasSize(allOperators.length);
    }

    @Test
    void filterConfigurer_addFilter_blankFieldName_throwsIllegalArgumentException() {
        FilterContext.TemplateBuilder<User> builder = FilterContext.buildTemplateForType(User.class);
        assertThatThrownBy(() -> builder.queryParam(c -> c.addFilter("   ", Operator.EQ)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Field name must not be blank");
    }

    @Test
    void filterConfigurer_addFilter_emptyFieldName_throwsIllegalArgumentException() {
        FilterContext.TemplateBuilder<User> builder = FilterContext.buildTemplateForType(User.class);
        assertThatThrownBy(() -> builder.queryParam(c -> c.addFilter("", Operator.EQ)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Field name must not be blank");
    }

    @Test
    void filterConfigurer_addFilterWithExpression_blankFieldName_throwsIllegalArgumentException() {
        ExpressionProviderFunction<User, String> expressionProvider = (root, cq, cb) -> root.get("firstName");
        FilterContext.TemplateBuilder<User> builder = FilterContext.buildTemplateForType(User.class);
        assertThatThrownBy(() -> builder.queryParam(c -> c.addFilter("   ", expressionProvider, Operator.EQ)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Field name must not be blank");
    }

    @Test
    void filterConfigurer_addFilterWithExpression_emptyFieldName_throwsIllegalArgumentException() {
        ExpressionProviderFunction<User, String> expressionProvider = (root, cq, cb) -> root.get("firstName");
        FilterContext.TemplateBuilder<User> builder = FilterContext.buildTemplateForType(User.class);
        assertThatThrownBy(() -> builder.queryParam(c -> c.addFilter("", expressionProvider, Operator.EQ)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Field name must not be blank");
    }

    @Test
    void filterConfigurer_addCustomFilter_blankFilterName_throwsIllegalArgumentException() {
        FilterContext.TemplateBuilder<User> builder = FilterContext.buildTemplateForType(User.class);
        assertThatThrownBy(() -> builder.queryParam(c -> c.addCustomFilter("   ", String.class, (root, cq, cb, values, errorWrapper) -> Optional.ofNullable(cb.equal(root.get("firstName"), values.get(0))))))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Filter name must not be blank");
    }
}