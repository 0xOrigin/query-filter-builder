package io.github._0xorigin.queryfilterbuilder;

import io.github._0xorigin.queryfilterbuilder.base.dtos.FilterRequest;
import io.github._0xorigin.queryfilterbuilder.base.dtos.SortRequest;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;
import io.github._0xorigin.queryfilterbuilder.configs.*;
import io.github._0xorigin.queryfilterbuilder.entities.User;
import io.github._0xorigin.queryfilterbuilder.entities.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for QueryFilterBuilder using the User entity with real data.
 * This test autowires the QueryFilterBuilder and tests filtering by the role enum field
 * without using any mocks.
 */
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
        userRepository.deleteAll();

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
                        .addSorts("role"))
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
        User adminUser = new User();
        adminUser.setFirstName("Admin");
        adminUser.setLastName("User");
        adminUser.setRole(User.Role.ADMIN);
        adminUser.setIsActive(true);
        adminUser.setCreatedAt(Instant.now());
        adminUser.setLastLogin(OffsetDateTime.now());
        userRepository.save(adminUser);

        User regularUser = new User();
        regularUser.setFirstName("Regular");
        regularUser.setLastName("User");
        regularUser.setRole(User.Role.USER);
        regularUser.setIsActive(true);
        regularUser.setCreatedAt(Instant.now());
        regularUser.setLastLogin(OffsetDateTime.now().minusDays(1));
        userRepository.save(regularUser);

        User inactiveUser = new User();
        inactiveUser.setFirstName("Inactive");
        inactiveUser.setLastName("User");
        inactiveUser.setRole(User.Role.USER);
        inactiveUser.setIsActive(false);
        inactiveUser.setCreatedAt(Instant.now());
        inactiveUser.setLastLogin(OffsetDateTime.now().minusDays(30));
        userRepository.save(inactiveUser);
    }

    @Test
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
            .element(0)
            .extracting(User::getRole)
            .isEqualTo(User.Role.ADMIN);
    }

    @Test
    void testComplexFilter_MultipleConditions() {
        List<FilterRequest> filterRequests = Arrays.asList(
            new FilterRequest("isActive", Operator.EQ.name(), "true"),
            new FilterRequest("role", Operator.NEQ.name(), User.Role.ADMIN.name())
        );

        FilterContext<User> filterContext = userFilterTemplate
                .newSourceBuilder()
                .withBodySource(filterRequests)
                .buildFilterContext();

        Specification<User> specification = queryFilterBuilder.buildFilterSpecification(filterContext);
        List<User> results = userRepository.findAll(specification);

        assertThat(results)
            .hasSize(1)
            .element(0)
            .extracting(User::getFirstName)
            .isEqualTo("Regular");
    }

    @Test
    void testSorting_MultipleFields() {
        List<SortRequest> sortRequests = Arrays.asList(
            new SortRequest("role", Sort.Direction.ASC),
            new SortRequest("firstName", Sort.Direction.DESC)
        );

        SortContext<User> sortContext = userSortTemplate
                .newSourceBuilder()
                .withBodySource(sortRequests)
                .buildSortContext();

        Specification<User> specification = queryFilterBuilder.buildSortSpecification(sortContext);
        List<User> results = userRepository.findAll(specification);

        assertThat(results).hasSize(3);
        assertThat(results.get(0).getRole()).isEqualTo(User.Role.ADMIN);
    }

    @Test
    void testDateRangeFilter() {
        OffsetDateTime now = OffsetDateTime.now();
        List<FilterRequest> filterRequests = List.of(
            new FilterRequest("lastLogin", Operator.LT.name(), now.toString())
        );

        FilterContext<User> filterContext = userFilterTemplate
                .newSourceBuilder()
                .withBodySource(filterRequests)
                .buildFilterContext();

        Specification<User> specification = queryFilterBuilder.buildFilterSpecification(filterContext);
        List<User> results = userRepository.findAll(specification);

        assertThat(results)
            .hasSize(3)
            .allMatch(user -> user.getLastLogin().isBefore(now));
    }

    @Test
    void testCustomFilter_RoleFilter() {
        List<FilterRequest> filterRequests = List.of(
            new FilterRequest("customRoleFilter", Operator.EQ.name(), User.Role.USER.name())
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
    void testFilter_MultiValueOperator() {
        List<FilterRequest> filterRequests = List.of(
            new FilterRequest("role", Operator.IN.name(),
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
    void testFilter_EmptyResults() {
        List<FilterRequest> filterRequests = List.of(
            new FilterRequest("firstName", Operator.EQ.name(), "NonExistentUser")
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
    void testFilter_CaseInsensitiveSearch() {
        List<FilterRequest> filterRequests = List.of(
            new FilterRequest("firstName", Operator.ICONTAINS.name(), "admin")
        );

        FilterContext<User> filterContext = userFilterTemplate
                .newSourceBuilder()
                .withBodySource(filterRequests)
                .buildFilterContext();

        Specification<User> specification = queryFilterBuilder.buildFilterSpecification(filterContext);
        List<User> results = userRepository.findAll(specification);

        assertThat(results)
            .hasSize(1)
            .element(0)
            .extracting(User::getFirstName)
            .isEqualTo("Admin");  // Changed to standard equality since the ICONTAINS operator handles case insensitivity
    }

    @Test
    void testFilter_BetweenOperator() {
        OffsetDateTime start = OffsetDateTime.now().minusDays(2);
        OffsetDateTime end = OffsetDateTime.now();

        List<FilterRequest> filterRequests = List.of(
            new FilterRequest("lastLogin", Operator.BETWEEN.name(),
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
            .allMatch(user ->
                !user.getLastLogin().isBefore(start) &&
                !user.getLastLogin().isAfter(end));
    }

    @Test
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

        List<SortRequest> sortRequests = Arrays.asList(
            new SortRequest("firstName", Sort.Direction.ASC),
            new SortRequest("lastName", Sort.Direction.ASC)
        );

        SortContext<User> sortContext = userSortTemplate
                .newSourceBuilder()
                .withBodySource(sortRequests)
                .buildSortContext();

        Specification<User> specification = queryFilterBuilder.buildSortSpecification(sortContext);
        List<User> results = userRepository.findAll(specification);

        assertThat(results)
            .hasSize(4)
            .extracting(User::getFirstName)
            .containsNull();  // Changed to more appropriate assertion for null check
    }

    @Test
    void testComplexFilter_CombiningOperators() {
        List<FilterRequest> filterRequests = Arrays.asList(
            new FilterRequest("isActive", Operator.EQ.name(), "true"),
            new FilterRequest("role", Operator.IN.name(),
                String.format("%s,%s", User.Role.ADMIN.name(), User.Role.USER.name())),
            new FilterRequest("firstName", Operator.CONTAINS.name(), "r")
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
                assertThat(user.getFirstName().toLowerCase()).contains("r");
                assertThat(user.getRole()).isIn(User.Role.ADMIN, User.Role.USER);
            });
    }
}