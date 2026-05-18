package io.github._0xorigin.queryfilterbuilder;

import io.github._0xorigin.queryfilterbuilder.base.dtos.FilterRequest;
import io.github._0xorigin.queryfilterbuilder.base.dtos.SortRequest;
import io.github._0xorigin.queryfilterbuilder.base.enums.SourceType;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;
import io.github._0xorigin.queryfilterbuilder.configs.*;
import io.github._0xorigin.queryfilterbuilder.entities.User;
import io.github._0xorigin.queryfilterbuilder.entities.UserRepository;
import io.github._0xorigin.queryfilterbuilder.exceptions.InvalidQueryParameterException;
import jakarta.persistence.criteria.Expression;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@EnableAutoConfiguration
@ContextConfiguration(classes = {
    DataSourceAutoConfiguration.class, FilterFieldConfig.class, FilterFieldRegistryConfig.class,
    FilterOperatorConfig.class, FilterOperatorRegistryConfig.class,
    QueryFilterBuilderConfig.class, QueryFilterBuilderEnvironmentPostProcessor.class,
    LocalizationConfig.class
})
class QueryFilterBuilderIntegrationTest {

    @Autowired
    private QueryFilterBuilder<User> queryFilterBuilder;

    @Autowired
    private UserRepository userRepository;

    private FilterContext.Template<User> userFilterTemplate;
    private SortContext.Template<User> userSortTemplate;

    @BeforeEach
    void setUp() {
        // Setup filter template
        userFilterTemplate = FilterContext.buildTemplateForType(User.class)
            .queryParam(configurer -> configurer
                .addFilter("role", Operator.EQ, Operator.NEQ, Operator.IN)
                .addFilter("firstName", Operator.EQ, Operator.CONTAINS, Operator.ICONTAINS)
                .addFilter("lastName", Operator.EQ, Operator.CONTAINS, Operator.ICONTAINS)
                .addFilter("isActive", Operator.EQ)
                .addFilter("createdAt", Operator.GT, Operator.LT, Operator.GTE, Operator.LTE, Operator.BETWEEN)
            )
            .requestBody(configurer -> configurer
                .addFilter("role", Operator.EQ, Operator.NEQ, Operator.IN)
                .addFilter("firstName", Operator.EQ, Operator.CONTAINS, Operator.ICONTAINS)
                .addFilter("lastName", Operator.EQ, Operator.CONTAINS, Operator.ICONTAINS)
                .addFilter("isActive", Operator.EQ)
                .addFilter("lastLogin", Operator.GT, Operator.LT, Operator.GTE, Operator.LTE, Operator.BETWEEN)
                .addFilter("createdAt", Operator.GT, Operator.LT, Operator.GTE, Operator.LTE, Operator.BETWEEN)
                .addFilter("createdBy.firstName", Operator.EQ)
                .addFilter(Set.of("parent-user", "parent.User"), "createdBy", Operator.EQ, Operator.LT)
                .addCustomFilter("customRoleFilter", User.Role.class,
                    (root, criteriaQuery, cb, values, filterErrorWrapper) ->
                        Optional.of(cb.equal(root.get("role"), values.get(0))))
            )
            .buildTemplate();

        // Setup sort template with supported fields
        userSortTemplate = SortContext.buildTemplateForType(User.class)
            .queryParam(configurer -> configurer
                .addSorts("firstName")
                .addSorts("lastName")
                .addSorts("createdAt")
                .addSorts("role")
                .addDescSort("createdBy.firstName")
                .addAscSort("createdBy.lastName")
            )
            .requestBody(configurer -> configurer
                .addSorts("firstName")
                .addSorts("lastName")
                .addAscSort("parentUser", "createdBy")
                .addSorts("createdAt")
                .addSorts("role"))
            .buildTemplate();

        // Setup test data
        setupTestData();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    private void setupTestData() {
        Instant now = Instant.now();

        User adminUser = new User();
        adminUser.setId(UUID.fromString("c0a86433-9e3b-198a-819e-3be990bf0000"));
        adminUser.setFirstName("Admin");
        adminUser.setLastName(null);
        adminUser.setRole(User.Role.ADMIN);
        adminUser.setIsActive(true);
        adminUser.setCreatedAt(now.minus(3, ChronoUnit.HOURS));
        adminUser.setLastLogin(OffsetDateTime.now());
        adminUser = userRepository.saveAndFlush(adminUser);

        User regularUser = new User();
        regularUser.setId(UUID.fromString("c0a86433-9e3b-198a-819e-3be990d40001"));
        regularUser.setFirstName("Regular");
        regularUser.setLastName("User1");
        regularUser.setRole(User.Role.USER);
        regularUser.setIsActive(true);
        regularUser.setCreatedAt(now.minus(1, ChronoUnit.HOURS));
        regularUser.setLastLogin(OffsetDateTime.now().minusDays(1));
        regularUser.setCreatedBy(adminUser);
        regularUser = userRepository.saveAndFlush(regularUser);

        User inactiveUser = new User();
        inactiveUser.setId(UUID.fromString("c0a86433-9e3b-198a-819e-3be990d60002"));
        inactiveUser.setFirstName("Inactive");
        inactiveUser.setLastName("User1");
        inactiveUser.setRole(User.Role.USER);
        inactiveUser.setIsActive(false);
        inactiveUser.setCreatedAt(now);
        inactiveUser.setLastLogin(OffsetDateTime.now().minusDays(30));
        inactiveUser.setCreatedBy(regularUser);
        userRepository.saveAndFlush(inactiveUser);

        System.out.println("Test data setup complete: " + userRepository.findAll().stream().map(User::getId).toList());
    }

    @Test
    @DisplayName("Filters by role using query parameter")
    void testFilterByRole_WithQueryParam() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("role", User.Role.ADMIN.name());
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        FilterContext<User> filterContext = userFilterTemplate
            .newSourceBuilder()
            .withQuerySource(request)
            .buildFilterContext();

        Specification<User> specification = queryFilterBuilder.buildFilterSpecification(filterContext);
        List<User> results = userRepository.findAll(specification);

        assertThat(results)
            .hasSize(1)
            .first()
            .extracting(User::getRole)
            .isEqualTo(User.Role.ADMIN);
    }

    @Test
    @DisplayName("Applies complex filters with multiple conditions")
    void testComplexFilter_MultipleConditions() {
        List<FilterRequest> filterRequests = List.of(
            new FilterRequest("isActive", Operator.EQ.getValue(), "true"),
            new FilterRequest("role", Operator.NEQ.getValue(), User.Role.ADMIN.name())
        );

        FilterContext<User> filterContext = userFilterTemplate
            .newSourceBuilder()
            .withBodySource(filterRequests)
            .buildFilterContext();

        Specification<User> specification = queryFilterBuilder.buildFilterSpecification(filterContext);
        List<User> results = userRepository.findAll(specification);

        assertThat(results)
            .hasSize(1)
            .first()
            .extracting(User::getFirstName)
            .isEqualTo("Regular");
    }

    @Test
    @DisplayName("Sorts by multiple fields and verifies full order")
    void testSorting_MultipleFields() {
        List<SortRequest> sortRequests = List.of(
            new SortRequest("role", Sort.Direction.ASC),
            new SortRequest("firstName", Sort.Direction.DESC)
        );

        SortContext<User> sortContext = userSortTemplate
            .newSourceBuilder()
            .withBodySource(sortRequests)
            .buildSortContext();

        Specification<User> specification = queryFilterBuilder.buildSortSpecification(sortContext);
        List<User> results = userRepository.findAll(specification);

        assertThat(results)
            .hasSize(3)
            .extracting(User::getRole, User::getFirstName)
            .containsExactly(
                tuple(User.Role.ADMIN, "Admin"),
                tuple(User.Role.USER, "Regular"),
                tuple(User.Role.USER, "Inactive")
            );
    }

    @Test
    @DisplayName("Applies custom filter for role")
    void testCustomFilter_RoleFilter() {
        List<FilterRequest> filterRequests = List.of(
            new FilterRequest("customRoleFilter", Operator.EQ.getValue(), User.Role.USER.name())
        );

        FilterContext<User> filterContext = userFilterTemplate
            .newSourceBuilder()
            .withBodySource(filterRequests)
            .buildFilterContext();

        Specification<User> specification = queryFilterBuilder.buildFilterSpecification(filterContext);
        List<User> results = userRepository.findAll(specification);

        assertThat(results)
            .hasSize(2)
            .allMatch(user -> user.getRole() == User.Role.USER);
    }

    @Test
    @DisplayName("Defaults invalid operator to EQ")
    void testFilter_InvalidOperator_DefaultsToEQ() {
        List<FilterRequest> filterRequests = List.of(
            new FilterRequest("role", "INVALID_OPERATOR", User.Role.ADMIN.name())
        );

        FilterContext<User> filterContext = userFilterTemplate
            .newSourceBuilder()
            .withBodySource(filterRequests)
            .buildFilterContext();

        Specification<User> specification = queryFilterBuilder.buildFilterSpecification(filterContext);
        List<User> results = userRepository.findAll(specification);

        assertThat(results).hasSize(1).allMatch(user -> user.getRole() == User.Role.ADMIN);
    }

    @Test
    @DisplayName("Handles multi-value IN operator")
    void testFilter_MultiValueOperator() {
        List<FilterRequest> filterRequests = List.of(
            new FilterRequest("role", Operator.IN.getValue(),
                String.format("%s,%s", User.Role.ADMIN.name(), User.Role.USER.name()))
        );

        FilterContext<User> filterContext = userFilterTemplate
            .newSourceBuilder()
            .withBodySource(filterRequests)
            .buildFilterContext();

        Specification<User> specification = queryFilterBuilder.buildFilterSpecification(filterContext);
        List<User> results = userRepository.findAll(specification);

        assertThat(results).hasSize(3);
    }

    @Test
    @DisplayName("Returns empty results for non-matching filter")
    void testFilter_EmptyResults() {
        List<FilterRequest> filterRequests = List.of(
            new FilterRequest("firstName", Operator.EQ.getValue(), "NonExistentUser")
        );

        FilterContext<User> filterContext = userFilterTemplate
            .newSourceBuilder()
            .withBodySource(filterRequests)
            .buildFilterContext();

        Specification<User> specification = queryFilterBuilder.buildFilterSpecification(filterContext);
        List<User> results = userRepository.findAll(specification);

        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("Performs case-insensitive search")
    void testFilter_CaseInsensitiveSearch() {
        List<FilterRequest> filterRequests = List.of(
            new FilterRequest("firstName", Operator.ICONTAINS.getValue(), "admin")
        );

        FilterContext<User> filterContext = userFilterTemplate
            .newSourceBuilder()
            .withBodySource(filterRequests)
            .buildFilterContext();

        Specification<User> specification = queryFilterBuilder.buildFilterSpecification(filterContext);
        List<User> results = userRepository.findAll(specification);

        assertThat(results)
            .hasSize(1)
            .first()
            .extracting(User::getFirstName)
            .isEqualTo("Admin");
    }

    @Test
    @DisplayName("Applies BETWEEN operator on date fields")
    void testFilter_BetweenOperator() {
        OffsetDateTime start = OffsetDateTime.now().minusDays(2);
        OffsetDateTime end = OffsetDateTime.now();

        List<FilterRequest> filterRequests = List.of(
            new FilterRequest("lastLogin", Operator.BETWEEN.getValue(),
                String.format("%s,%s", start, end))
        );

        FilterContext<User> filterContext = userFilterTemplate
            .newSourceBuilder()
            .withBodySource(filterRequests)
            .buildFilterContext();

        Specification<User> specification = queryFilterBuilder.buildFilterSpecification(filterContext);
        List<User> results = userRepository.findAll(specification);

        assertThat(results)
            .hasSize(2)
            .allSatisfy(user ->
                assertThat(user.getLastLogin())
                    .isBetween(start, end)
            );
    }

    @Test
    @DisplayName("Handles sorting with null values")
    void testSorting_MultipleFieldsWithNulls() {
        // Create a user with null firstName
        User nullNameUser = new User();
        nullNameUser.setId(UUID.fromString("c0a86433-9e3b-198a-819e-3be990d60003"));
        nullNameUser.setFirstName(null);
        nullNameUser.setLastName("NullFirst");
        nullNameUser.setRole(User.Role.USER);
        nullNameUser.setIsActive(true);
        nullNameUser.setCreatedAt(Instant.now());
        nullNameUser.setLastLogin(OffsetDateTime.now());
        userRepository.save(nullNameUser);

        List<SortRequest> sortRequests = List.of(
            new SortRequest("firstName", Sort.Direction.ASC),
            new SortRequest("lastName", Sort.Direction.ASC)
        );

        SortContext<User> sortContext = userSortTemplate
            .newSourceBuilder()
            .withBodySource(sortRequests)
            .buildSortContext();

        Specification<User> specification = queryFilterBuilder.buildSortSpecification(sortContext);
        List<User> results = userRepository.findAll(specification);

        assertThat(results).hasSize(4);

        long nullCount = results.stream()
            .map(User::getFirstName)
            .filter(Objects::isNull)
            .count();
        assertThat(nullCount).isEqualTo(1);
    }

    @Test
    @DisplayName("Combines multiple operators in complex filter")
    void testComplexFilter_CombiningOperators() {
        List<FilterRequest> filterRequests = List.of(
            new FilterRequest("isActive", Operator.EQ.getValue(), "true"),
            new FilterRequest("role", Operator.IN.getValue(),
                String.format("%s,%s", User.Role.ADMIN.name(), User.Role.USER.name())),
            new FilterRequest("firstName", Operator.CONTAINS.getValue(), "r")
        );

        FilterContext<User> filterContext = userFilterTemplate
            .newSourceBuilder()
            .withBodySource(filterRequests)
            .buildFilterContext();

        Specification<User> specification = queryFilterBuilder.buildFilterSpecification(filterContext);
        List<User> results = userRepository.findAll(specification);

        assertThat(results)
            .hasSize(1)
            .allSatisfy(user -> {
                assertThat(user.getIsActive()).isTrue();
                assertThat(user.getFirstName()).contains("r");
                assertThat(user.getRole()).isIn(User.Role.ADMIN, User.Role.USER);
            });
    }

    @ParameterizedTest
    @DisplayName("Tests various operators via query params and body")
    @MethodSource("allOperatorsTestData")
    void testAllOperators(String field, Operator operator, String value, int expectedSize, SourceType sourceType) {
        FilterContext<User> filterContext;
        if (sourceType == SourceType.QUERY_PARAM) {
            MockHttpServletRequest request = new MockHttpServletRequest();
            String paramName = field + (operator != Operator.EQ ? "." + operator.getValue() : "");
            request.addParameter(paramName, value);
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
            filterContext = userFilterTemplate.newSourceBuilder().withQuerySource(request).buildFilterContext();
        } else {
            List<FilterRequest> filterRequests = List.of(new FilterRequest(field, operator.getValue(), value));
            filterContext = userFilterTemplate.newSourceBuilder().withBodySource(filterRequests).buildFilterContext();
        }

        Specification<User> specification = queryFilterBuilder.buildFilterSpecification(filterContext);
        List<User> results = userRepository.findAll(specification);

        assertThat(results).hasSize(expectedSize);
    }

    static Stream<Arguments> allOperatorsTestData() {
        Instant now = Instant.now();
        return Stream.of(
            Arguments.of("firstName", Operator.EQ, "Admin", 1, SourceType.QUERY_PARAM),
            Arguments.of("lastName", Operator.CONTAINS, "User", 2, SourceType.QUERY_PARAM),
            Arguments.of("role", Operator.IN, "ADMIN,USER", 3, SourceType.QUERY_PARAM),
            Arguments.of("isActive", Operator.EQ, "true", 2, SourceType.QUERY_PARAM),
            Arguments.of("createdAt", Operator.GT, now.minus(2, ChronoUnit.HOURS).toString(), 2, SourceType.QUERY_PARAM),
            Arguments.of("lastName", Operator.ICONTAINS, "ser", 2, SourceType.REQUEST_BODY),
            Arguments.of("role", Operator.NEQ, "ADMIN", 2, SourceType.REQUEST_BODY),
            Arguments.of("lastLogin", Operator.BETWEEN, String.format("%s,%s", OffsetDateTime.now().minusDays(2), OffsetDateTime.now().plusDays(1)), 2, SourceType.REQUEST_BODY)
        );
    }

    @Test
    @DisplayName("Throws on type mismatch and invalid input")
    void testTypeMismatchAndInvalidInput() {
        // Invalid enum value
        final List<FilterRequest> invalidEnumRequests = List.of(
            new FilterRequest("role", Operator.EQ.getValue(), "NOT_A_ROLE")
        );
        FilterContext<User> filterContext = userFilterTemplate
            .newSourceBuilder()
            .withBodySource(invalidEnumRequests)
            .buildFilterContext();
        Specification<User> specification = queryFilterBuilder.buildFilterSpecification(filterContext);
        assertThatThrownBy(() -> userRepository.findAll(specification))
            .hasMessageContaining("No enum constant");

        // Malformed date
        final List<FilterRequest> invalidDateRequests = List.of(
            new FilterRequest("createdAt", Operator.GT.getValue(), "notadate")
        );
        FilterContext<User> filterContextDate = userFilterTemplate
            .newSourceBuilder()
            .withBodySource(invalidDateRequests)
            .buildFilterContext();
        Specification<User> specification1 = queryFilterBuilder.buildFilterSpecification(filterContextDate);
        assertThatThrownBy(() -> userRepository.findAll(specification1))
            .isInstanceOf(InvalidQueryParameterException.class)
            .hasMessageContaining("could not be parsed");
    }

    @Test
    @DisplayName("Handles custom filter error cases")
    void testCustomFilter_ErrorCase() {
        // Custom filter with missing value
        List<FilterRequest> filterRequests = List.of(
            new FilterRequest("customRoleFilter", Operator.EQ.getValue(), "")
        );
        FilterContext<User> filterContext = userFilterTemplate
            .newSourceBuilder()
            .withBodySource(filterRequests)
            .buildFilterContext();
        Specification<User> specification = queryFilterBuilder.buildFilterSpecification(filterContext);
        assertThatThrownBy(() -> userRepository.findAll(specification))
            .hasMessageContaining("No enum constant");
    }

    @Test
    @DisplayName("Handles multi-value with invalid entries")
    void testMultiValueAndLargeInputs() {
        // IN with many values, some invalid
        String values = String.join(",", User.Role.ADMIN.name(), User.Role.USER.name(), "INVALID");
        List<FilterRequest> filterRequests = List.of(
            new FilterRequest("role", Operator.IN.getValue(), values)
        );
        FilterContext<User> filterContext = userFilterTemplate
            .newSourceBuilder()
            .withBodySource(filterRequests)
            .buildFilterContext();
        Specification<User> specification = queryFilterBuilder.buildFilterSpecification(filterContext);
        assertThatThrownBy(() -> userRepository.findAll(specification))
            .hasMessageContaining("No enum constant");
    }

    @ParameterizedTest
    @DisplayName("Handles null, empty, and blank values appropriately")
    @MethodSource("nullEmptyBlankTestData")
    void testNullEmptyBlankValues(String value, int expectedSize) {
        List<FilterRequest> filterRequests = List.of(
            new FilterRequest("firstName", Operator.EQ.getValue(), value)
        );
        FilterContext<User> filterContext = userFilterTemplate
            .newSourceBuilder()
            .withBodySource(filterRequests)
            .buildFilterContext();
        Specification<User> specification = queryFilterBuilder.buildFilterSpecification(filterContext);
        List<User> results = userRepository.findAll(specification);
        assertThat(results).hasSize(expectedSize);
    }

    static Stream<Arguments> nullEmptyBlankTestData() {
        return Stream.of(
            Arguments.of((String) null, 0),
            Arguments.of("", 0),
            Arguments.of("   ", 0)
        );
    }

    @Test
    @DisplayName("Defaults unsupported operator to EQ")
    void testFilter_UnsupportedOperator() {
        List<FilterRequest> filterRequests = List.of(
            new FilterRequest("role", "like", User.Role.ADMIN.name())
        );
        FilterContext<User> filterContext = userFilterTemplate
            .newSourceBuilder()
            .withBodySource(filterRequests)
            .buildFilterContext();

        Specification<User> specification = queryFilterBuilder.buildFilterSpecification(filterContext);
        List<User> results = userRepository.findAll(specification);
        assertThat(results).hasSize(1).allMatch(user -> user.getRole() == User.Role.ADMIN);
    }

    @Test
    @DisplayName("Combines query param and body sources, body takes precedence")
    void testMixedSources_QueryAndBody() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("isActive", "true");
        request.addParameter("role", User.Role.ADMIN.name());
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        List<FilterRequest> bodyRequests = List.of(
            new FilterRequest("role", Operator.NEQ.getValue(), User.Role.ADMIN.name())
        );

        FilterContext<User> filterContext = userFilterTemplate
            .newSourceBuilder()
            .withQuerySource(request)
            .withBodySource(bodyRequests)
            .buildFilterContext();

        Specification<User> specification = queryFilterBuilder.buildFilterSpecification(filterContext);
        List<User> results = userRepository.findAll(specification);

        var user = assertThat(results)
            .hasSize(1)
            .first();
        user.extracting(User::getFirstName).isEqualTo("Regular");
        user.extracting(User::getRole).isNotEqualTo(User.Role.ADMIN);
    }

    @Test
    @DisplayName("Handles IN with empty list")
    void testFilter_InEmptyList() {
        List<FilterRequest> filterRequests = List.of(
            new FilterRequest("role", Operator.IN.getValue(), "")
        );
        FilterContext<User> filterContext = userFilterTemplate
            .newSourceBuilder()
            .withBodySource(filterRequests)
            .buildFilterContext();

        Specification<User> specification = queryFilterBuilder.buildFilterSpecification(filterContext);
        assertThatThrownBy(() -> userRepository.findAll(specification))
            .isInstanceOf(InvalidQueryParameterException.class)
            .hasMessageContaining("No enum constant");
    }

    @Test
    @DisplayName("Handles nested field lookup")
    void testFilter_NestedFieldLookup() {
        List<FilterRequest> filterRequests = List.of(
            new FilterRequest("createdBy.firstName", Operator.EQ.getValue(), "Admin")
        );
        FilterContext<User> filterContext = userFilterTemplate
            .newSourceBuilder()
            .withBodySource(filterRequests)
            .buildFilterContext();

        Specification<User> specification = queryFilterBuilder.buildFilterSpecification(filterContext);
        List<User> results = userRepository.findAll(specification);

        assertThat(results).hasSize(1).allMatch(user -> user.getCreatedBy().getFirstName().equals("Admin"));
    }

    @Test
    @DisplayName("Handles multiple/nested field sorting")
    void testSort_MultipleFields_NestedField() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("sort", "lastName,-createdBy.firstName");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        SortContext<User> sortContext = userSortTemplate
            .newSourceBuilder()
            .withQuerySource(request)
            .buildSortContext();

        Specification<User> specification = queryFilterBuilder.buildSortSpecification(sortContext);
        List<User> results = userRepository.findAll(specification);

        var users = assertThat(results).hasSize(2);

        users.extracting(User::getLastName)
            .containsSequence("User1", "User1");
        users.extracting(User::getFirstName)
            .containsSequence("Inactive", "Regular");
    }

    @Test
    @DisplayName("Filters by alias using query parameter")
    void testFilter_ByAlias_QueryParam() {
        // Build a template that registers an alias 'fn' for 'firstName'
        FilterContext.Template<User> aliasTemplate = FilterContext.buildTemplateForType(User.class)
            .queryParam(configurer -> configurer.addFilter("fn", "firstName", Operator.EQ))
            .buildTemplate();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("fn", "Admin");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        FilterContext<User> filterContext = aliasTemplate
            .newSourceBuilder()
            .withQuerySource(request)
            .buildFilterContext();

        Specification<User> specification = queryFilterBuilder.buildFilterSpecification(filterContext);
        List<User> results = userRepository.findAll(specification);

        assertThat(results).hasSize(1).first().extracting(User::getFirstName).isEqualTo("Admin");
    }

    @Test
    @DisplayName("Filters by alias using request body")
    void testFilter_ByAlias_RequestBody() {
        // Build a template that registers an alias 'fn' for 'firstName' for body source
        FilterContext.Template<User> aliasTemplate = FilterContext.buildTemplateForType(User.class)
            .requestBody(configurer -> configurer.addFilter("fn", "firstName", Operator.EQ))
            .buildTemplate();

        List<FilterRequest> filterRequests = List.of(new FilterRequest("fn", Operator.EQ.getValue(), "Admin"));

        FilterContext<User> filterContext = aliasTemplate
            .newSourceBuilder()
            .withBodySource(filterRequests)
            .buildFilterContext();

        Specification<User> specification = queryFilterBuilder.buildFilterSpecification(filterContext);
        List<User> results = userRepository.findAll(specification);

        assertThat(results).hasSize(1).first().extracting(User::getFirstName).isEqualTo("Admin");
    }

    @Test
    @DisplayName("Filters by multiple aliases")
    void testFilter_ByMultipleAliases() {
        // Register multiple aliases for the same field
        FilterContext.Template<User> aliasTemplate = FilterContext.buildTemplateForType(User.class)
            .queryParam(configurer -> configurer.addFilter(Set.of("fn", "first_name"), "firstName", Operator.EQ))
            .buildTemplate();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("first_name", "Admin");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        FilterContext<User> filterContext = aliasTemplate
            .newSourceBuilder()
            .withQuerySource(request)
            .buildFilterContext();

        Specification<User> specification = queryFilterBuilder.buildFilterSpecification(filterContext);
        List<User> results = userRepository.findAll(specification);

        assertThat(results).hasSize(1).first().extracting(User::getFirstName).isEqualTo("Admin");
    }

    @Test
    @DisplayName("Sorts by alias using request body")
    void testSort_ByAlias_RequestBody() {
        // Register alias 'fn' for firstName for sort request body
        SortContext.Template<User> aliasSortTemplate = SortContext.buildTemplateForType(User.class)
            .requestBody(configurer -> configurer.addSorts("fn", "firstName"))
            .buildTemplate();

        List<SortRequest> sortRequests = List.of(new SortRequest("fn", Sort.Direction.DESC));

        SortContext<User> sortContext = aliasSortTemplate
            .newSourceBuilder()
            .withBodySource(sortRequests)
            .buildSortContext();

        Specification<User> specification = queryFilterBuilder.buildSortSpecification(sortContext);
        List<User> results = userRepository.findAll(specification);

        // Descending by firstName: Regular, Inactive, Admin
        assertThat(results)
            .hasSize(3)
            .extracting(User::getFirstName)
            .containsSequence("Regular", "Inactive", "Admin");
    }

    @Test
    @DisplayName("Sorts by alias using query parameter")
    void testSort_ByAlias_QueryParam() {
        // Register alias 'fn' for firstName for sort query params
        SortContext.Template<User> aliasSortTemplate = SortContext.buildTemplateForType(User.class)
            .queryParam(configurer -> configurer.addSorts("fn", "firstName"))
            .buildTemplate();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("sort", "-fn");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        SortContext<User> sortContext = aliasSortTemplate
            .newSourceBuilder()
            .withQuerySource(request)
            .buildSortContext();

        Specification<User> specification = queryFilterBuilder.buildSortSpecification(sortContext);
        List<User> results = userRepository.findAll(specification);

        assertThat(results)
            .hasSize(3)
            .extracting(User::getFirstName)
            .containsSequence("Regular", "Inactive", "Admin");
    }

    @Test
    @DisplayName("Filters by alias with expression (request body)")
    void testFilter_ByAlias_WithExpression_RequestBody() {
        // Register an alias 'creatorFirst' that maps to createdBy.firstName via an expression provider
        FilterContext.Template<User> aliasTemplate = FilterContext.buildTemplateForType(User.class)
            .requestBody(configurer -> configurer.addFilter(
                "creatorFirst",
                "createdBy.firstName",
                (root, q, cb) -> root.join("createdBy", JoinType.LEFT).get("firstName"),
                Operator.EQ
            ))
            .buildTemplate();

        // We're looking for users whose creator's first name is 'Admin' -> should return the regular user
        List<FilterRequest> filterRequests = List.of(new FilterRequest("creatorFirst", Operator.EQ.getValue(), "Admin"));

        FilterContext<User> filterContext = aliasTemplate
            .newSourceBuilder()
            .withBodySource(filterRequests)
            .buildFilterContext();

        Specification<User> specification = queryFilterBuilder.buildFilterSpecification(filterContext);
        List<User> results = userRepository.findAll(specification);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getFirstName()).isEqualTo("Regular");
    }

    @Test
    @DisplayName("Sorts by alias with expression (query param)")
    void testSort_ByAlias_WithExpression_QueryParam() {
        // Register alias 'creatorFirst' for query param sorting that sorts by createdBy.firstName
        SortContext.Template<User> aliasSortTemplate = SortContext.buildTemplateForType(User.class)
            .queryParam(configurer -> configurer.addSorts(
                "creatorFirst",
                "createdBy.firstName",
                (root, q, cb) -> root.join("createdBy", JoinType.LEFT).get("firstName")
            ))
            .buildTemplate();

        MockHttpServletRequest request = new MockHttpServletRequest();
        // descending by creator's first name
        request.addParameter("sort", "-creatorFirst");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        SortContext<User> sortContext = aliasSortTemplate
            .newSourceBuilder()
            .withQuerySource(request)
            .buildSortContext();

        Specification<User> specification = queryFilterBuilder.buildSortSpecification(sortContext);
        List<User> results = userRepository.findAll(specification);

        // Map to creator first names (null when no creator) and assert descending order: Regular, Admin, null
        List<String> creatorFirstNames = results.stream()
            .map(u -> u.getCreatedBy() == null ? null : u.getCreatedBy().getFirstName())
            .toList();

        assertThat(creatorFirstNames).containsSequence("Regular", "Admin", null);
    }

    @Test
    @DisplayName("Sorts by alias with expression (request body - desc)")
    void testSort_ByAlias_WithExpression_RequestBody_Desc() {
        // Register alias 'creatorFirstDesc' for body sorting that sorts by createdBy.firstName descending
        SortContext.Template<User> aliasSortTemplate = SortContext.buildTemplateForType(User.class)
            .requestBody(configurer -> configurer.addDescSort(
                "creatorFirstDesc",
                "createdBy.firstName",
                (root, q, cb) -> root.join("createdBy", JoinType.LEFT).get("firstName")
            ))
            .buildTemplate();

        List<SortRequest> sortRequests = List.of(new SortRequest("creatorFirstDesc", Sort.Direction.DESC));

        SortContext<User> sortContext = aliasSortTemplate
            .newSourceBuilder()
            .withBodySource(sortRequests)
            .buildSortContext();

        Specification<User> specification = queryFilterBuilder.buildSortSpecification(sortContext);
        List<User> results = userRepository.findAll(specification);

        List<String> creatorFirstNames = results.stream()
            .map(u -> u.getCreatedBy() == null ? null : u.getCreatedBy().getFirstName())
            .toList();

        // descending by creator first name: inactive user (creator=Regular), regular (creator=Admin), admin (creator=null)
        assertThat(creatorFirstNames).containsSequence("Regular", "Admin", null);
    }

    @Test
    @DisplayName("Sorts by aliases set (request body)")
    void testSort_ByAliasesSet_RequestBody() {
        // Register multiple aliases for firstName and sort using one of the aliases
        SortContext.Template<User> aliasSortTemplate = SortContext.buildTemplateForType(User.class)
            .requestBody(configurer -> configurer.addAscSort(Set.of("fn", "first_name"), "firstName"))
            .buildTemplate();

        List<SortRequest> sortRequests = List.of(new SortRequest("first_name", Sort.Direction.ASC));

        SortContext<User> sortContext = aliasSortTemplate
            .newSourceBuilder()
            .withBodySource(sortRequests)
            .buildSortContext();

        Specification<User> specification = queryFilterBuilder.buildSortSpecification(sortContext);
        List<User> results = userRepository.findAll(specification);

        assertThat(results).hasSize(3);
        // Ensure alias mapping produced an ordering (non-empty check)
        assertThat(results.stream().map(User::getFirstName).toList()).isNotEmpty();
    }

    @Test
    @DisplayName("Sorts by field with expression provider (ascending)")
    void testSort_ByField_WithExpressionProvider_Asc() {
        SortContext.Template<User> expressionSortTemplate = SortContext.buildTemplateForType(User.class)
            .requestBody(configurer -> configurer.addAscSort(
                "firstName",
                (root, q, cb) -> root.get("firstName")
            ))
            .buildTemplate();

        List<SortRequest> sortRequests = List.of(new SortRequest("firstName", Sort.Direction.ASC));

        SortContext<User> sortContext = expressionSortTemplate
            .newSourceBuilder()
            .withBodySource(sortRequests)
            .buildSortContext();

        Specification<User> specification = queryFilterBuilder.buildSortSpecification(sortContext);
        List<User> results = userRepository.findAll(specification);

        assertThat(results)
            .hasSize(3)
            .extracting(User::getFirstName)
            .containsExactly("Admin", "Inactive", "Regular");
    }

    @Test
    @DisplayName("Sorts by field with expression provider (descending)")
    void testSort_ByField_WithExpressionProvider_Desc() {
        SortContext.Template<User> expressionSortTemplate = SortContext.buildTemplateForType(User.class)
            .requestBody(configurer -> configurer.addDescSort(
                "firstName",
                (root, q, cb) -> root.get("firstName")
            ))
            .buildTemplate();

        List<SortRequest> sortRequests = List.of(new SortRequest("firstName", Sort.Direction.DESC));

        SortContext<User> sortContext = expressionSortTemplate
            .newSourceBuilder()
            .withBodySource(sortRequests)
            .buildSortContext();

        Specification<User> specification = queryFilterBuilder.buildSortSpecification(sortContext);
        List<User> results = userRepository.findAll(specification);

        assertThat(results)
            .hasSize(3)
            .extracting(User::getFirstName)
            .containsExactly("Regular", "Inactive", "Admin");
    }

    @Test
    @DisplayName("Sorts by field with expression provider (both directions)")
    void testSort_ByField_WithExpressionProvider_Both() {
        SortContext.Template<User> expressionSortTemplate = SortContext.buildTemplateForType(User.class)
            .requestBody(configurer -> configurer.addSorts(
                "firstName",
                (root, q, cb) -> root.get("firstName")
            ))
            .buildTemplate();

        List<SortRequest> sortRequests = List.of(new SortRequest("firstName", Sort.Direction.DESC));

        SortContext<User> sortContext = expressionSortTemplate
            .newSourceBuilder()
            .withBodySource(sortRequests)
            .buildSortContext();

        Specification<User> specification = queryFilterBuilder.buildSortSpecification(sortContext);
        List<User> results = userRepository.findAll(specification);

        assertThat(results)
            .hasSize(3)
            .extracting(User::getFirstName)
            .containsExactly("Regular", "Inactive", "Admin");
    }

    @Test
    @DisplayName("Sorts by single alias (ascending)")
    void testSort_BySingleAlias_Asc() {
        SortContext.Template<User> aliasSortTemplate = SortContext.buildTemplateForType(User.class)
            .requestBody(configurer -> configurer.addAscSort("fn", "firstName"))
            .buildTemplate();

        List<SortRequest> sortRequests = List.of(new SortRequest("fn", Sort.Direction.ASC));

        SortContext<User> sortContext = aliasSortTemplate
            .newSourceBuilder()
            .withBodySource(sortRequests)
            .buildSortContext();

        Specification<User> specification = queryFilterBuilder.buildSortSpecification(sortContext);
        List<User> results = userRepository.findAll(specification);

        assertThat(results)
            .hasSize(3)
            .extracting(User::getFirstName)
            .containsExactly("Admin", "Inactive", "Regular");
    }

    @Test
    @DisplayName("Sorts by single alias (descending)")
    void testSort_BySingleAlias_Desc() {
        SortContext.Template<User> aliasSortTemplate = SortContext.buildTemplateForType(User.class)
            .requestBody(configurer -> configurer.addDescSort("ln", "firstName"))
            .buildTemplate();

        List<SortRequest> sortRequests = List.of(new SortRequest("ln", Sort.Direction.DESC));

        SortContext<User> sortContext = aliasSortTemplate
            .newSourceBuilder()
            .withBodySource(sortRequests)
            .buildSortContext();

        Specification<User> specification = queryFilterBuilder.buildSortSpecification(sortContext);
        List<User> results = userRepository.findAll(specification);

        assertThat(results)
            .hasSize(3)
            .extracting(User::getFirstName)
            .containsExactly("Regular", "Inactive", "Admin");
    }

    @Test
    @DisplayName("Sorts by single alias (both directions)")
    void testSort_BySingleAlias_Both() {
        SortContext.Template<User> aliasSortTemplate = SortContext.buildTemplateForType(User.class)
            .requestBody(configurer -> configurer.addSorts("role_alias", "role"))
            .buildTemplate();

        List<SortRequest> sortRequests = List.of(new SortRequest("role_alias", Sort.Direction.ASC));

        SortContext<User> sortContext = aliasSortTemplate
            .newSourceBuilder()
            .withBodySource(sortRequests)
            .buildSortContext();

        Specification<User> specification = queryFilterBuilder.buildSortSpecification(sortContext);
        List<User> results = userRepository.findAll(specification);

        assertThat(results)
            .hasSize(3)
            .extracting(User::getRole)
            .containsExactly(User.Role.ADMIN, User.Role.USER, User.Role.USER);
    }

    @Test
    @DisplayName("Sorts by alias with expression provider (ascending)")
    void testSort_ByAlias_WithExpressionProvider_Asc() {
        SortContext.Template<User> aliasSortTemplate = SortContext.buildTemplateForType(User.class)
            .requestBody(configurer -> configurer.addAscSort(
                "creator_first",
                "createdBy.firstName",
                (root, q, cb) -> root.join("createdBy", JoinType.LEFT).get("firstName")
            ))
            .buildTemplate();

        List<SortRequest> sortRequests = List.of(new SortRequest("creator_first", Sort.Direction.ASC));

        SortContext<User> sortContext = aliasSortTemplate
            .newSourceBuilder()
            .withBodySource(sortRequests)
            .buildSortContext();

        Specification<User> specification = queryFilterBuilder.buildSortSpecification(sortContext);
        List<User> results = userRepository.findAll(specification);

        List<String> creatorFirstNames = results.stream()
            .map(u -> u.getCreatedBy() == null ? null : u.getCreatedBy().getFirstName())
            .toList();

        // Ascending: null, Admin, Regular
        assertThat(creatorFirstNames).containsSequence(null, "Admin", "Regular");
    }

    @Test
    @DisplayName("Sorts by alias with expression provider (descending)")
    void testSort_ByAlias_WithExpressionProvider_Desc() {
        SortContext.Template<User> aliasSortTemplate = SortContext.buildTemplateForType(User.class)
            .requestBody(configurer -> configurer.addDescSort(
                "creator_first",
                "createdBy.firstName",
                (root, q, cb) -> root.join("createdBy", JoinType.LEFT).get("firstName")
            ))
            .buildTemplate();

        List<SortRequest> sortRequests = List.of(new SortRequest("creator_first", Sort.Direction.DESC));

        SortContext<User> sortContext = aliasSortTemplate
            .newSourceBuilder()
            .withBodySource(sortRequests)
            .buildSortContext();

        Specification<User> specification = queryFilterBuilder.buildSortSpecification(sortContext);
        List<User> results = userRepository.findAll(specification);

        List<String> creatorFirstNames = results.stream()
            .map(u -> u.getCreatedBy() == null ? null : u.getCreatedBy().getFirstName())
            .toList();

        // Descending: Regular, Admin, null
        assertThat(creatorFirstNames).containsSequence("Regular", "Admin", null);
    }

    @Test
    @DisplayName("Sorts by alias with expression provider (both directions)")
    void testSort_ByAlias_WithExpressionProvider_Both() {
        SortContext.Template<User> aliasSortTemplate = SortContext.buildTemplateForType(User.class)
            .requestBody(configurer -> configurer.addSorts(
                "creator_name",
                "createdBy.firstName",
                (root, q, cb) -> root.join("createdBy", JoinType.LEFT).get("firstName")
            ))
            .buildTemplate();

        List<SortRequest> sortRequests = List.of(new SortRequest("creator_name", Sort.Direction.DESC));

        SortContext<User> sortContext = aliasSortTemplate
            .newSourceBuilder()
            .withBodySource(sortRequests)
            .buildSortContext();

        Specification<User> specification = queryFilterBuilder.buildSortSpecification(sortContext);
        List<User> results = userRepository.findAll(specification);

        List<String> creatorFirstNames = results.stream()
            .map(u -> u.getCreatedBy() == null ? null : u.getCreatedBy().getFirstName())
            .toList();

        // Descending: Regular, Admin, null
        assertThat(creatorFirstNames).containsSequence("Regular", "Admin", null);
    }

    @Test
    @DisplayName("Sorts by aliases set (ascending)")
    void testSort_ByAliasesSet_Asc() {
        SortContext.Template<User> aliasSortTemplate = SortContext.buildTemplateForType(User.class)
            .requestBody(configurer -> configurer.addAscSort(Set.of("fn", "first_name", "fname"), "firstName"))
            .buildTemplate();

        List<SortRequest> sortRequests = List.of(new SortRequest("fname", Sort.Direction.ASC));

        SortContext<User> sortContext = aliasSortTemplate
            .newSourceBuilder()
            .withBodySource(sortRequests)
            .buildSortContext();

        Specification<User> specification = queryFilterBuilder.buildSortSpecification(sortContext);
        List<User> results = userRepository.findAll(specification);

        assertThat(results)
            .hasSize(3)
            .extracting(User::getFirstName)
            .containsExactly("Admin", "Inactive", "Regular");
    }

    @Test
    @DisplayName("Sorts by aliases set (descending)")
    void testSort_ByAliasesSet_Desc() {
        SortContext.Template<User> aliasSortTemplate = SortContext.buildTemplateForType(User.class)
            .requestBody(configurer -> configurer.addDescSort(Set.of("ln", "last_name", "lname"), "firstName"))
            .buildTemplate();

        List<SortRequest> sortRequests = List.of(new SortRequest("last_name", Sort.Direction.DESC));

        SortContext<User> sortContext = aliasSortTemplate
            .newSourceBuilder()
            .withBodySource(sortRequests)
            .buildSortContext();

        Specification<User> specification = queryFilterBuilder.buildSortSpecification(sortContext);
        List<User> results = userRepository.findAll(specification);

        assertThat(results)
            .hasSize(3)
            .extracting(User::getFirstName)
            .containsExactly("Regular", "Inactive", "Admin");
    }

    @Test
    @DisplayName("Sorts by aliases set (both directions)")
    void testSort_ByAliasesSet_Both() {
        SortContext.Template<User> aliasSortTemplate = SortContext.buildTemplateForType(User.class)
            .requestBody(configurer -> configurer.addSorts(Set.of("r", "role_alias"), "role"))
            .buildTemplate();

        List<SortRequest> sortRequests = List.of(new SortRequest("r", Sort.Direction.ASC));

        SortContext<User> sortContext = aliasSortTemplate
            .newSourceBuilder()
            .withBodySource(sortRequests)
            .buildSortContext();

        Specification<User> specification = queryFilterBuilder.buildSortSpecification(sortContext);
        List<User> results = userRepository.findAll(specification);

        assertThat(results)
            .hasSize(3)
            .extracting(User::getRole)
            .containsExactly(User.Role.ADMIN, User.Role.USER, User.Role.USER);
    }

    @Test
    @DisplayName("Sorts by aliases set with expression provider (ascending)")
    void testSort_ByAliasesSet_WithExpressionProvider_Asc() {
        SortContext.Template<User> aliasSortTemplate = SortContext.buildTemplateForType(User.class)
            .requestBody(configurer -> configurer.addAscSort(
                Set.of("cf", "creator_first", "c_first"),
                "createdBy.firstName",
                (root, q, cb) -> root.join("createdBy", JoinType.LEFT).get("firstName")
            ))
            .buildTemplate();

        List<SortRequest> sortRequests = List.of(new SortRequest("c_first", Sort.Direction.ASC));

        SortContext<User> sortContext = aliasSortTemplate
            .newSourceBuilder()
            .withBodySource(sortRequests)
            .buildSortContext();

        Specification<User> specification = queryFilterBuilder.buildSortSpecification(sortContext);
        List<User> results = userRepository.findAll(specification);

        List<String> creatorFirstNames = results.stream()
            .map(u -> u.getCreatedBy() == null ? null : u.getCreatedBy().getFirstName())
            .toList();

        // Ascending: null, Admin, Regular
        assertThat(creatorFirstNames).containsSequence(null, "Admin", "Regular");
    }

    @Test
    @DisplayName("Sorts by aliases set with expression provider (descending)")
    void testSort_ByAliasesSet_WithExpressionProvider_Desc() {
        SortContext.Template<User> aliasSortTemplate = SortContext.buildTemplateForType(User.class)
            .requestBody(configurer -> configurer.addDescSort(
                Set.of("cl", "creator_last", "c_last"),
                "createdBy.firstName",
                (root, q, cb) -> root.join("createdBy", JoinType.LEFT).get("firstName")
            ))
            .buildTemplate();

        List<SortRequest> sortRequests = List.of(new SortRequest("creator_last", Sort.Direction.DESC));

        SortContext<User> sortContext = aliasSortTemplate
            .newSourceBuilder()
            .withBodySource(sortRequests)
            .buildSortContext();

        Specification<User> specification = queryFilterBuilder.buildSortSpecification(sortContext);
        List<User> results = userRepository.findAll(specification);

        List<String> creatorFirstNames = results.stream()
            .map(u -> u.getCreatedBy() == null ? null : u.getCreatedBy().getFirstName())
            .toList();

        // Descending: Regular, Admin, null
        assertThat(creatorFirstNames).containsSequence("Regular", "Admin", null);
    }

    @Test
    @DisplayName("Sorts by aliases set with expression provider (both directions)")
    void testSort_ByAliasesSet_WithExpressionProvider_Both() {
        SortContext.Template<User> aliasSortTemplate = SortContext.buildTemplateForType(User.class)
            .requestBody(configurer -> configurer.addSorts(
                Set.of("cn", "creator_name", "c_name"),
                "createdBy.firstName",
                (root, q, cb) -> root.join("createdBy", JoinType.LEFT).get("firstName")
            ))
            .buildTemplate();

        List<SortRequest> sortRequests = List.of(new SortRequest("cn", Sort.Direction.DESC));

        SortContext<User> sortContext = aliasSortTemplate
            .newSourceBuilder()
            .withBodySource(sortRequests)
            .buildSortContext();

        Specification<User> specification = queryFilterBuilder.buildSortSpecification(sortContext);
        List<User> results = userRepository.findAll(specification);

        List<String> creatorFirstNames = results.stream()
            .map(u -> u.getCreatedBy() == null ? null : u.getCreatedBy().getFirstName())
            .toList();

        // Descending: Regular, Admin, null
        assertThat(creatorFirstNames).containsSequence("Regular", "Admin", null);
    }

    @Test
    @DisplayName("Sorts by custom sort function")
    void testSort_CustomSort() {
        SortContext.Template<User> customSortTemplate = SortContext.buildTemplateForType(User.class)
            .requestBody(configurer -> configurer.addCustomSort(
                "customLengthSort",
                (root, criteriaQuery, cb, errorWrapper) -> {
                    Expression<String> firstNameExpression = root.get("firstName");
                    return Optional.ofNullable(cb.asc(cb.length(firstNameExpression)));
                }
            ))
            .buildTemplate();

        List<SortRequest> sortRequests = List.of(new SortRequest("customLengthSort", Sort.Direction.ASC));

        SortContext<User> sortContext = customSortTemplate
            .newSourceBuilder()
            .withBodySource(sortRequests)
            .buildSortContext();

        Specification<User> specification = queryFilterBuilder.buildSortSpecification(sortContext);
        List<User> results = userRepository.findAll(specification);

        assertThat(results)
            .hasSize(3)
            .extracting(User::getFirstName)
            .containsExactly("Admin", "Regular", "Inactive");
    }

    @Test
    @DisplayName("Sorts by alias that map to association field")
    void testSort_ByAlias_AssociationField() {
        List<SortRequest> sortRequests = List.of(new SortRequest("parentUser", Sort.Direction.ASC));

        SortContext<User> sortContext = userSortTemplate
            .newSourceBuilder()
            .withBodySource(sortRequests)
            .buildSortContext();

        Specification<User> specification = queryFilterBuilder.buildSortSpecification(sortContext);
        List<User> results = userRepository.findAll(specification);

        List<String> creatorFirstNames = results.stream()
            .map(u -> u.getCreatedBy() == null ? null : u.getCreatedBy().getFirstName())
            .toList();

        assertThat(creatorFirstNames).containsSequence(null, "Admin", "Regular");
    }

    @Test
    @DisplayName("Filters by alias that map to association field")
    void testFilter_ByAlias_AssociationField() {
        List<FilterRequest> filterRequests = List.of(
                new FilterRequest(
                        "parent-user",
                        Operator.EQ.getValue(),
                        "c0a86433-9e3b-198a-819e-3be990d40001"
                )
        );

        FilterContext<User> filterContext = userFilterTemplate
                .newSourceBuilder()
                .withBodySource(filterRequests)
                .buildFilterContext();

        Specification<User> specification = queryFilterBuilder.buildFilterSpecification(filterContext);
        List<User> results = userRepository.findAll(specification);

        assertThat(results).hasSize(1)
                .first()
                .extracting(User::getFirstName)
                .isEqualTo("Inactive");
    }
}
