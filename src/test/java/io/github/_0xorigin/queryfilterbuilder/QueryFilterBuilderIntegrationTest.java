package io.github._0xorigin.queryfilterbuilder;

import io.github._0xorigin.queryfilterbuilder.base.dtos.FilterRequest;
import io.github._0xorigin.queryfilterbuilder.base.dtos.SortRequest;
import io.github._0xorigin.queryfilterbuilder.base.enums.SourceType;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;
import io.github._0xorigin.queryfilterbuilder.configs.*;
import io.github._0xorigin.queryfilterbuilder.entities.User;
import io.github._0xorigin.queryfilterbuilder.entities.UserRepository;
import io.github._0xorigin.queryfilterbuilder.exceptions.InvalidQueryParameterException;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
        adminUser.setFirstName("Admin");
        adminUser.setLastName(null);
        adminUser.setRole(User.Role.ADMIN);
        adminUser.setIsActive(true);
        adminUser.setCreatedAt(now.minus(3, ChronoUnit.HOURS));
        adminUser.setLastLogin(OffsetDateTime.now());
        userRepository.save(adminUser);

        User regularUser = new User();
        regularUser.setFirstName("Regular");
        regularUser.setLastName("User1");
        regularUser.setRole(User.Role.USER);
        regularUser.setIsActive(true);
        regularUser.setCreatedAt(now.minus(1, ChronoUnit.HOURS));
        regularUser.setLastLogin(OffsetDateTime.now().minusDays(1));
        regularUser.setCreatedBy(adminUser);
        userRepository.save(regularUser);

        User inactiveUser = new User();
        inactiveUser.setFirstName("Inactive");
        inactiveUser.setLastName("User1");
        inactiveUser.setRole(User.Role.USER);
        inactiveUser.setIsActive(false);
        inactiveUser.setCreatedAt(now);
        inactiveUser.setLastLogin(OffsetDateTime.now().minusDays(30));
        inactiveUser.setCreatedBy(regularUser);
        userRepository.save(inactiveUser);
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
}
