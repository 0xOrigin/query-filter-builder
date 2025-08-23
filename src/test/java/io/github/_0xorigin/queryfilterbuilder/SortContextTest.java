package io.github._0xorigin.queryfilterbuilder;

import io.github._0xorigin.queryfilterbuilder.base.dtos.SortRequest;
import io.github._0xorigin.queryfilterbuilder.base.enums.SourceType;
import io.github._0xorigin.queryfilterbuilder.base.functions.CustomSortFunction;
import io.github._0xorigin.queryfilterbuilder.base.functions.ExpressionProviderFunction;
import io.github._0xorigin.queryfilterbuilder.entities.FakeUser;
import io.github._0xorigin.queryfilterbuilder.entities.User;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class SortContextTest {

    @Mock
    private HttpServletRequest request;

    @Test
    void buildTemplateForType_nullType_throwsNullPointerException() {
        assertThatThrownBy(() -> SortContext.buildTemplateForType(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Type must not be null");
    }

    @Test
    void buildTemplateForType_nonEntityClass_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> SortContext.buildTemplateForType(FakeUser.class))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Class " + FakeUser.class.getName() + " is not a JPA Entity");
    }

    @Test
    void templateBuilder_queryParam_nullConsumer_throwsNullPointerException() {
        SortContext.TemplateBuilder<User> builder = SortContext.buildTemplateForType(User.class);
        assertThatThrownBy(() -> builder.queryParam(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Consumer for SortConfigurer must not be null");
    }

    @Test
    void templateBuilder_requestBody_nullConsumer_throwsNullPointerException() {
        SortContext.TemplateBuilder<User> builder = SortContext.buildTemplateForType(User.class);
        assertThatThrownBy(() -> builder.requestBody(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Consumer for SortConfigurer must not be null");
    }

    @Test
    void sourceBuilder_withQuerySource_nullRequest_throwsNullPointerException() {
        SortContext.Template<User> template = SortContext.buildTemplateForType(User.class).buildTemplate();
        SortContext.SourceBuilder<User> sourceBuilder = template.newSourceBuilder();
        assertThatThrownBy(() -> sourceBuilder.withQuerySource(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("HttpServletRequest must not be null");
    }

    @Test
    void sourceBuilder_withBodySource_nullSortRequests_throwsNullPointerException() {
        SortContext.Template<User> template = SortContext.buildTemplateForType(User.class).buildTemplate();
        SortContext.SourceBuilder<User> sourceBuilder = template.newSourceBuilder();
        assertThatThrownBy(() -> sourceBuilder.withBodySource(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("SortRequests must not be null");
    }

    @Test
    void sortConfigurer_addAscSort_nullFieldName_throwsNullPointerException() {
        SortContext.TemplateBuilder<User> builder = SortContext.buildTemplateForType(User.class);
        assertThatThrownBy(() -> builder.queryParam(c -> c.addAscSort(null)))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Field name must not be null");
    }

    @Test
    void sortConfigurer_addDescSort_nullFieldName_throwsNullPointerException() {
        SortContext.TemplateBuilder<User> builder = SortContext.buildTemplateForType(User.class);
        assertThatThrownBy(() -> builder.queryParam(c -> c.addDescSort(null)))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Field name must not be null");
    }

    @Test
    void sortConfigurer_addSorts_nullFieldName_throwsNullPointerException() {
        SortContext.TemplateBuilder<User> builder = SortContext.buildTemplateForType(User.class);
        assertThatThrownBy(() -> builder.queryParam(c -> c.addSorts(null)))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Field name must not be null");
    }

    @Test
    void sortConfigurer_addAscSortWithExpression_nullFieldName_throwsNullPointerException() {
        ExpressionProviderFunction<User, String> expressionProvider = (root, cq, cb) -> root.get("firstName");
        SortContext.TemplateBuilder<User> builder = SortContext.buildTemplateForType(User.class);
        assertThatThrownBy(() -> builder.queryParam(c -> c.addAscSort(null, expressionProvider)))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Field name must not be null");
    }

    @Test
    void sortConfigurer_addAscSortWithExpression_nullExpressionProvider_throwsNullPointerException() {
        SortContext.TemplateBuilder<User> builder = SortContext.buildTemplateForType(User.class);
        assertThatThrownBy(() -> builder.queryParam(c -> c.addAscSort("field", null)))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Expression provider function must not be null");
    }

    @Test
    void sortConfigurer_addDescSortWithExpression_nullFieldName_throwsNullPointerException() {
        ExpressionProviderFunction<User, String> expressionProvider = (root, cq, cb) -> root.get("firstName");
        SortContext.TemplateBuilder<User> builder = SortContext.buildTemplateForType(User.class);
        assertThatThrownBy(() -> builder.queryParam(c -> c.addDescSort(null, expressionProvider)))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Field name must not be null");
    }

    @Test
    void sortConfigurer_addDescSortWithExpression_nullExpressionProvider_throwsNullPointerException() {
        SortContext.TemplateBuilder<User> builder = SortContext.buildTemplateForType(User.class);
        assertThatThrownBy(() -> builder.queryParam(c -> c.addDescSort("field", null)))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Expression provider function must not be null");
    }

    @Test
    void sortConfigurer_addSortsWithExpression_nullFieldName_throwsNullPointerException() {
        ExpressionProviderFunction<User, String> expressionProvider = (root, cq, cb) -> root.get("firstName");
        SortContext.TemplateBuilder<User> builder = SortContext.buildTemplateForType(User.class);
        assertThatThrownBy(() -> builder.queryParam(c -> c.addSorts(null, expressionProvider)))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Field name must not be null");
    }

    @Test
    void sortConfigurer_addSortsWithExpression_nullExpressionProvider_throwsNullPointerException() {
        SortContext.TemplateBuilder<User> builder = SortContext.buildTemplateForType(User.class);
        assertThatThrownBy(() -> builder.queryParam(c -> c.addSorts("field", null)))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Expression provider function must not be null");
    }

    @Test
    void sortConfigurer_addCustomSort_nullSortName_throwsNullPointerException() {
        CustomSortFunction<User> sortFunction = (root, cq, cb, errorWrapper)
            -> Optional.of(cb.asc(root.get("firstName")));
        SortContext.TemplateBuilder<User> builder = SortContext.buildTemplateForType(User.class);
        assertThatThrownBy(() -> builder.queryParam(c -> c.addCustomSort(null, sortFunction)))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Sort name must not be null");
    }

    @Test
    void sortConfigurer_addCustomSort_nullSortFunction_throwsNullPointerException() {
        SortContext.TemplateBuilder<User> builder = SortContext.buildTemplateForType(User.class);
        assertThatThrownBy(() -> builder.queryParam(c -> c.addCustomSort("cust", null)))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Sort function must not be null");
    }

    @Test
    void buildTemplateForType_validEntity_returnsTemplateBuilder() {
        SortContext.TemplateBuilder<User> builder = SortContext.buildTemplateForType(User.class);
        assertThat(builder).isNotNull().isInstanceOf(SortContext.TemplateBuilder.class);
    }

    @Test
    void templateBuilder_buildTemplate_returnsTemplate() {
        SortContext.Template<User> template = SortContext.buildTemplateForType(User.class).buildTemplate();
        assertThat(template).isNotNull().isInstanceOf(SortContext.Template.class);

        SortContext<User> context = template.newSourceBuilder().buildSortContext();
        assertThat(context.getSorts()).isEmpty();
        assertThat(context.getCustomSorts()).isEmpty();
    }

    @Test
    void template_newSourceBuilder_returnsSourceBuilder() {
        SortContext.Template<User> template = SortContext.buildTemplateForType(User.class).buildTemplate();
        SortContext.SourceBuilder<User> sourceBuilder = template.newSourceBuilder();
        assertThat(sourceBuilder).isNotNull().isInstanceOf(SortContext.SourceBuilder.class);
    }

    @Test
    void sourceBuilder_buildSortContext_withoutSources_returnsContextWithEmptyCollections() {
        SortContext.Template<User> template = SortContext.buildTemplateForType(User.class).buildTemplate();
        SortContext.SourceBuilder<User> sourceBuilder = template.newSourceBuilder();
        SortContext<User> context = sourceBuilder.buildSortContext();

        assertThat(context).isNotNull();
        assertThat(context.getRequest()).isEmpty();
        assertThat(context.getSortRequests()).isEmpty();
        assertThat(context.getSorts()).isEmpty();
        assertThat(context.getCustomSorts()).isEmpty();
    }

    @Test
    void sourceBuilder_withQuerySource_success_setsRequest() {
        SortContext.Template<User> template = SortContext.buildTemplateForType(User.class).buildTemplate();
        SortContext.SourceBuilder<User> sourceBuilder = template.newSourceBuilder();
        sourceBuilder.withQuerySource(request);
        SortContext<User> context = sourceBuilder.buildSortContext();
        assertThat(context.getRequest()).isPresent().contains(request);
    }

    @Test
    void sourceBuilder_withBodySource_success_setsSortRequests() {
        List<SortRequest> sortRequests = List.of(new SortRequest("firstName", Sort.Direction.ASC));
        SortContext.Template<User> template = SortContext.buildTemplateForType(User.class).buildTemplate();
        SortContext.SourceBuilder<User> sourceBuilder = template.newSourceBuilder();
        sourceBuilder.withBodySource(sortRequests);
        SortContext<User> context = sourceBuilder.buildSortContext();
        assertThat(context.getSortRequests()).isPresent().contains(sortRequests);
    }

    @Test
    void sourceBuilder_withBothSources_success_setsBoth() {
        List<SortRequest> sortRequests = List.of(new SortRequest("firstName", Sort.Direction.ASC));
        SortContext.Template<User> template = SortContext.buildTemplateForType(User.class).buildTemplate();

        SortContext.SourceBuilder<User> sourceBuilder = template.newSourceBuilder();
        sourceBuilder.withQuerySource(request).withBodySource(sortRequests);
        SortContext<User> context = sourceBuilder.buildSortContext();

        assertThat(context.getRequest()).isPresent().contains(request);
        assertThat(context.getSortRequests()).isPresent().contains(sortRequests);
    }

    @Test
    void sortConfigurer_addAscSort_success_addsAscDirection() {
        SortContext.TemplateBuilder<User> builder = SortContext.buildTemplateForType(User.class);
        builder.queryParam(c -> c.addAscSort("field"));
        SortContext<User> context = builder.buildTemplate().newSourceBuilder().buildSortContext();
        var holder = context.getSorts().get("field");

        assertThat(holder).isNotNull();
        assertThat(holder.directions()).containsExactly(Sort.Direction.ASC);
        assertThat(holder.sourceTypes()).containsExactly(SourceType.QUERY_PARAM);
        assertThat(holder.expressionProviderFunction()).isEmpty();
    }

    @Test
    void sortConfigurer_addDescSort_success_addsDescDirection() {
        SortContext.TemplateBuilder<User> builder = SortContext.buildTemplateForType(User.class);
        builder.queryParam(c -> c.addDescSort("field"));
        SortContext<User> context = builder.buildTemplate().newSourceBuilder().buildSortContext();
        var holder = context.getSorts().get("field");

        assertThat(holder).isNotNull();
        assertThat(holder.directions()).containsExactly(Sort.Direction.DESC);
        assertThat(holder.sourceTypes()).containsExactly(SourceType.QUERY_PARAM);
        assertThat(holder.expressionProviderFunction()).isEmpty();
    }

    @Test
    void sortConfigurer_addSorts_success_addsAscAndDescDirections() {
        SortContext.TemplateBuilder<User> builder = SortContext.buildTemplateForType(User.class);
        builder.queryParam(c -> c.addSorts("field"));
        SortContext<User> context = builder.buildTemplate().newSourceBuilder().buildSortContext();
        var holder = context.getSorts().get("field");

        assertThat(holder).isNotNull();
        assertThat(holder.directions()).containsExactlyInAnyOrder(Sort.Direction.ASC, Sort.Direction.DESC);
        assertThat(holder.sourceTypes()).containsExactly(SourceType.QUERY_PARAM);
        assertThat(holder.expressionProviderFunction()).isEmpty();
    }

    @Test
    void sortConfigurer_addSort_multipleCalls_accumulatesDirectionsAndSources() {
        SortContext.TemplateBuilder<User> builder = SortContext.buildTemplateForType(User.class);
        builder.queryParam(c -> {
            c.addAscSort("field");
            c.addDescSort("field");
        });
        builder.requestBody(c -> c.addSorts("field"));
        SortContext<User> context = builder.buildTemplate().newSourceBuilder().buildSortContext();
        var holder = context.getSorts().get("field");

        assertThat(holder).isNotNull();
        assertThat(holder.directions()).containsExactlyInAnyOrder(Sort.Direction.ASC, Sort.Direction.DESC);
        assertThat(holder.sourceTypes()).containsExactlyInAnyOrder(SourceType.QUERY_PARAM, SourceType.REQUEST_BODY);
        assertThat(holder.expressionProviderFunction()).isEmpty();
    }

    @Test
    void sortConfigurer_addAscSortWithExpression_success_setsExpressionAndAsc() {
        ExpressionProviderFunction<User, String> expr = (root, cq, cb) -> root.get("firstName");
        SortContext.TemplateBuilder<User> builder = SortContext.buildTemplateForType(User.class);
        builder.queryParam(c -> c.addAscSort("field", expr));
        SortContext<User> context = builder.buildTemplate().newSourceBuilder().buildSortContext();
        var holder = context.getSorts().get("field");

        assertThat(holder).isNotNull();
        assertThat(holder.directions()).containsExactly(Sort.Direction.ASC);
        assertThat(holder.sourceTypes()).containsExactly(SourceType.QUERY_PARAM);
        assertThat(holder.expressionProviderFunction()).isPresent().get().isEqualTo(expr);
    }

    @Test
    void sortConfigurer_addDescSortWithExpression_success_setsExpressionAndDesc() {
        ExpressionProviderFunction<User, String> expr = (root, cq, cb) -> root.get("firstName");
        SortContext.TemplateBuilder<User> builder = SortContext.buildTemplateForType(User.class);
        builder.queryParam(c -> c.addDescSort("field", expr));
        SortContext<User> context = builder.buildTemplate().newSourceBuilder().buildSortContext();
        var holder = context.getSorts().get("field");

        assertThat(holder).isNotNull();
        assertThat(holder.directions()).containsExactly(Sort.Direction.DESC);
        assertThat(holder.sourceTypes()).containsExactly(SourceType.QUERY_PARAM);
        assertThat(holder.expressionProviderFunction()).isPresent().get().isEqualTo(expr);
    }

    @Test
    void sortConfigurer_addSortsWithExpression_success_setsExpressionAndAscDesc() {
        ExpressionProviderFunction<User, String> expr = (root, cq, cb) -> root.get("firstName");
        SortContext.TemplateBuilder<User> builder = SortContext.buildTemplateForType(User.class);
        builder.queryParam(c -> c.addSorts("field", expr));
        SortContext<User> context = builder.buildTemplate().newSourceBuilder().buildSortContext();
        var holder = context.getSorts().get("field");

        assertThat(holder).isNotNull();
        assertThat(holder.directions()).containsExactlyInAnyOrder(Sort.Direction.ASC, Sort.Direction.DESC);
        assertThat(holder.sourceTypes()).containsExactly(SourceType.QUERY_PARAM);
        assertThat(holder.expressionProviderFunction()).isPresent().get().isEqualTo(expr);
    }

    @Test
    void sortConfigurer_addSort_mixedWithAndWithoutExpression_overwritesExpressionWhenWith() {
        ExpressionProviderFunction<User, String> expr1 = (root, cq, cb) -> root.get("firstName");
        ExpressionProviderFunction<User, String> expr2 = (root, cq, cb) -> root.get("lastName");
        SortContext.TemplateBuilder<User> builder = SortContext.buildTemplateForType(User.class);
        builder.queryParam(c -> {
            c.addAscSort("field"); // no expr
            c.addAscSort("field", expr1); // sets expr1
            c.addDescSort("field"); // keeps expr1
            c.addDescSort("field", expr2); // overwrites to expr2
        });
        SortContext<User> context = builder.buildTemplate().newSourceBuilder().buildSortContext();
        var holder = context.getSorts().get("field");

        assertThat(holder.directions()).containsExactlyInAnyOrder(Sort.Direction.ASC, Sort.Direction.DESC);
        assertThat(holder.expressionProviderFunction()).isPresent().get().isEqualTo(expr2);
    }

    @Test
    void sortConfigurer_addCustomSort_success_addsHolder() {
        CustomSortFunction<User> sortFunction = (root, cq, cb, errorWrapper)
            -> Optional.of(cb.asc(root.get("firstName")));
        SortContext.TemplateBuilder<User> builder = SortContext.buildTemplateForType(User.class);
        builder.queryParam(c -> c.addCustomSort("cust", sortFunction));
        SortContext<User> context = builder.buildTemplate().newSourceBuilder().buildSortContext();
        var holder = context.getCustomSorts().get("cust");

        assertThat(holder).isNotNull();
        assertThat(holder.customSortFunction()).isEqualTo(sortFunction);
        assertThat(holder.sourceTypes()).containsExactly(SourceType.QUERY_PARAM);
    }

    @Test
    void sortConfigurer_addCustomSort_multipleCalls_accumulatesSourcesKeepsFirstFunction() {
        CustomSortFunction<User> func1 = (root, cq, cb, errorWrapper)
            -> Optional.of(cb.asc(root.get("firstName")));
        CustomSortFunction<User> func2 = (root, cq, cb, errorWrapper)
            -> Optional.of(cb.desc(root.get("firstName")));
        SortContext.TemplateBuilder<User> builder = SortContext.buildTemplateForType(User.class);

        builder.queryParam(c -> c.addCustomSort("cust", func1));
        builder.requestBody(c -> c.addCustomSort("cust", func2));
        SortContext<User> context = builder.buildTemplate().newSourceBuilder().buildSortContext();
        var holder = context.getCustomSorts().get("cust");

        assertThat(holder.customSortFunction()).isEqualTo(func1); // keeps first
        assertThat(holder.sourceTypes()).containsExactlyInAnyOrder(SourceType.QUERY_PARAM, SourceType.REQUEST_BODY);
    }

    @Test
    void sortContext_getSorts_returnsUnmodifiableCopy() {
        SortContext.TemplateBuilder<User> builder = SortContext.buildTemplateForType(User.class);
        builder.queryParam(c -> c.addAscSort("field"));
        SortContext<User> context = builder.buildTemplate().newSourceBuilder().buildSortContext();
        var sorts = context.getSorts();

        assertThat(sorts).hasSize(1);
        assertThatThrownBy(() -> sorts.put("new", null))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void sortContext_getCustomSorts_returnsUnmodifiableCopy() {
        CustomSortFunction<User> sortFunction = (root, cq, cb, errorWrapper)
            -> Optional.of(cb.asc(root.get("firstName")));
        SortContext.TemplateBuilder<User> builder = SortContext.buildTemplateForType(User.class);
        builder.queryParam(c -> c.addCustomSort("cust", sortFunction));
        SortContext<User> context = builder.buildTemplate().newSourceBuilder().buildSortContext();
        var customSorts = context.getCustomSorts();

        assertThat(customSorts).hasSize(1);
        assertThatThrownBy(() -> customSorts.put("new", null))
            .isInstanceOf(UnsupportedOperationException.class);
    }
}