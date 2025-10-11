# Query Filter Builder

[![Maven Central](https://img.shields.io/maven-central/v/io.github.0xorigin/query-filter-builder.svg)](https://search.maven.org/artifact/io.github.0xorigin/query-filter-builder)
[![javadoc](https://javadoc.io/badge2/io.github.0xorigin/query-filter-builder/JavaDoc.svg?color=blue&kill-cache=true)](https://javadoc.io/doc/io.github.0xorigin/query-filter-builder)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

**Query Filter Builder** is a robust and versatile library designed to dynamically generate type-safe JPA queries from **HTTP query parameters and request bodies** in Spring Boot applications.

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Requirements](#requirements)
- [Installation](#installation)
- [Configuration](#configuration)
- [Quick Start](#quick-start)
- [Usage](#usage)
    - [Injecting QueryFilterBuilder](#injecting-queryfilterbuilder)
    - [Defining Filter and Sort Templates](#defining-filter-and-sort-templates)
    - [Building Contexts from Requests](#building-contexts-from-requests)
    - [Applying Specifications to Repository](#applying-specifications-to-repository)
    - [Nested Field Filtering and Sorting](#nested-field-filtering-and-sorting)
    - [Using ListAPIRequest in Controller and Service](#using-listapirequest-in-controller-and-service)
- [Supported Operators](#supported-operators)
- [Supported Types](#supported-types)
- [Advanced Features](#advanced-features)
    - [Custom Filters](#custom-filters)
    - [Custom Sorts](#custom-sorts)
    - [Enum Filter Implementation](#enum-filter-implementation)
    - [Sorting via HTTP Query Parameters](#sorting-via-http-query-parameters)
    - [Sorting via Request Body](#sorting-via-request-body)
- [Edge Cases & Error Handling](#edge-cases--error-handling)
- [Examples](#examples)
- [Public API Reference](#public-api-reference)
- [Contributing](#contributing)
- [License](#license)

## Overview

**Query Filter Builder** simplifies the process of dynamically **filtering and sorting** data in Spring Boot applications by converting **HTTP query parameters and JSON request bodies** into type-safe JPA predicates. It eliminates the complexity of manually writing filtering logic while maintaining clean and maintainable code. By abstracting query construction, it enables you to deliver clean software in less time, accelerating development and reducing maintenance overhead.

## Features

- **Automatic Conversion**: Effortlessly transform query parameters **and request bodies** into JPA predicates.
- **Comprehensive Operators**: Supports 18+ filtering operators including comparison, collection, string matching, null checks, and range operators.
- **Type-Safe Validation**: Ensures data integrity with robust parameter validation and type conversion.
- **Clean API Design**: Provides an intuitive and developer-friendly API with fluent interfaces.
- **Spring Data Integration**: Seamlessly works with Spring Data JPA and Hibernate.
- **Nested Property Filtering**: Enables filtering across related entities with customizable field delimiters.
- **Extensibility**: Highly customizable with custom filter functions, custom sort functions, and expression providers.
- **Flexible Configuration**: Configurable field delimiters, sort parameters, and localization support.
- **Custom Filters & Sorting**: Support for complex, custom filtering and sorting logic beyond standard operators.
- **Registry-Based Architecture**: Extensible operator and field registries for maximum flexibility.
- **Comprehensive Testing**: Extensive unit test coverage for all operators, data types, and edge cases.
- **Security-First Design**: Input validation, SQL injection prevention, and secure type conversion.
- **Performance Optimized**: Efficient query building with minimal overhead and smart predicate generation.
- **Request Body Support**: Full support for JSON request body filtering and sorting alongside query parameters.

## Requirements

- **Java**: Version 17 or higher
- **Spring Boot**: Version 3.1.0 or higher
- **Jakarta Persistence API**: Version 3.1.0 or higher

## Installation

To integrate Query Filter Builder into your project, add the following Maven dependency:

```xml
<dependency>
    <groupId>io.github.0xorigin</groupId>
    <artifactId>query-filter-builder</artifactId>
    <version>2.0.0</version>
</dependency>
```

## Configuration

- The default field delimiter for nested property paths is `.` (dot). You can change it by setting the property `query-filter-builder.defaults.field-delimiter` in your application configuration.
    - **Note:** If you change the field delimiter, make sure to use the same delimiter in your filter and sort context template definitions (e.g., when specifying nested fields like `createdBy.firstName`).
- The default sort parameter name used for sorting via HTTP query parameters is `sort`. You can change it by setting the property `query-filter-builder.query-param.defaults.sort-parameter` in your application configuration.

## Quick Start

1. **Inject `QueryFilterBuilder<T>`**: Inject an instance of `QueryFilterBuilder<T>` into your class—typically a service layer class—where `T` is your entity class.
2. **Define a Template**: Use `FilterContext.buildTemplateForType` and `SortContext.buildTemplateForType` to specify which fields are filterable/sortable and how.
3. **Build a Context**: Use the template's `newSourceBuilder()` to create a `SourceBuilder`, then provide the input source (query or body) and call `buildFilterContext()` or `buildSortContext()`.
4. **Build Specification**: Use your injected `QueryFilterBuilder` instance to build a JPA `Specification` for filtering and sorting.
5. **Apply to Repository**: Ensure your repository extends `JpaSpecificationExecutor` and pass the specification to its `findAll` method.

## Usage

### Injecting QueryFilterBuilder

First, inject an instance of `QueryFilterBuilder<T>` into your desired class, typically a service. `T` should be your JPA entity.

```java
import io.github._0xorigin.queryfilterbuilder.QueryFilterBuilder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final QueryFilterBuilder<User> queryFilterBuilder;

    public UserService(QueryFilterBuilder<User> queryFilterBuilder) {
        this.queryFilterBuilder = queryFilterBuilder;
    }

    // ... your service logic
}
```

### Defining Filter and Sort Templates

Define templates at application startup (e.g., in a private final field) to encapsulate allowed fields, operators, and custom logic for both filters and sorts. Use the builder pattern to configure templates for each entity.

```java
// 1. Build and store the template (typically at startup in a private final field)
FilterContext.Template<User> userFilterTemplate = FilterContext.buildTemplateForType(User.class)
    .queryParam(configurer -> configurer
        .addFilter("role", Operator.EQ, Operator.NEQ, Operator.IN)
        .addFilter("firstName", Operator.EQ, Operator.CONTAINS, Operator.ICONTAINS)
        .addFilter("lastName", Operator.EQ, Operator.CONTAINS, Operator.ICONTAINS)
        .addFilter("isActive", (root, cq, cb) -> root.get("isActive"), Operator.EQ) // Can provide expression
        .addFilter("createdAt", Operator.GT, Operator.LT, Operator.GTE, Operator.LTE, Operator.BETWEEN)
    )
    .requestBody(configurer -> configurer
        .addFilter("role", Operator.EQ, Operator.NEQ, Operator.IN)
        .addFilter("firstName", Operator.EQ, Operator.CONTAINS, Operator.ICONTAINS)
        .addFilter("lastName", Operator.EQ, Operator.CONTAINS, Operator.ICONTAINS)
        .addFilter("isActive", Operator.EQ)
        .addFilter("lastLogin", Operator.GT, Operator.LT, Operator.GTE, Operator.LTE, Operator.BETWEEN)
        .addFilter("createdAt", Operator.GT, Operator.LT, Operator.GTE, Operator.LTE, Operator.BETWEEN)
        .addFilter("createdBy.firstName", Operator.EQ) // Nested field example
        .addCustomFilter("customRoleFilter", User.Role.class,
            (root, criteriaQuery, cb, values, filterErrorWrapper) ->
                Optional.of(cb.equal(root.get("role"), values.get(0)))
        )
    )
    .buildTemplate();

SortContext.Template<User> userSortTemplate = SortContext.buildTemplateForType(User.class)
    .queryParam(configurer -> configurer
        .addSorts("firstName") // Adds both ASC and DESC
        .addSorts("lastName")
        .addSorts("createdAt", (root, cq, cb) -> root.get("createdAt")) // Can provide expression
        .addSorts("role")
        .addDescSort("createdBy.firstName") // Nested field sort
        .addAscSort("createdBy.lastName")
        .addCustomSort("customSort",
            (root, cq, cb, errorWrapper) -> 
                Optional.of(cb.asc(root.get("firstName")))
        )
    )
    .requestBody(configurer -> configurer
        .addSorts("firstName")
        .addSorts("lastName")
        .addSorts("createdAt")
        .addSorts("role"))
    .buildTemplate();
```

### Building Contexts from Requests

```java
// For query parameters (e.g., from HttpServletRequest)
FilterContext<User> filterContext = userFilterTemplate.newSourceBuilder()
    .withQuerySource(request)
    .buildFilterContext();

SortContext<User> sortContext = userSortTemplate.newSourceBuilder()
    .withQuerySource(request)
    .buildSortContext();

// For request body (e.g., from controller DTOs)
FilterContext<User> filterContextFromBody = userFilterTemplate.newSourceBuilder()
    .withBodySource(filterRequests)
    .buildFilterContext();

SortContext<User> sortContextFromBody = userSortTemplate.newSourceBuilder()
    .withBodySource(sortRequests)
    .buildSortContext();

// Providing both query parameters and request body
FilterContext<User> filterContextWithBoth = userFilterTemplate.newSourceBuilder()
    .withQuerySource(request)
    .withBodySource(filterRequests)
    .buildFilterContext();

SortContext<User> sortContextWithBoth = userSortTemplate.newSourceBuilder()
    .withQuerySource(request)
    .withBodySource(sortRequests)
    .buildSortContext();
```

### Applying Specifications to Repository

```java
Specification<User> filterSpec = queryFilterBuilder.buildFilterSpecification(filterContext);
Specification<User> sortSpec = queryFilterBuilder.buildSortSpecification(sortContext);

// Combine and use with Spring Data JPA
List<User> users = userRepository.findAll(Specification.where(filterSpec).and(sortSpec));
```

### Nested Field Filtering and Sorting

```java
// Filtering on nested property (e.g., createdBy.firstName)
FilterContext<User> filterContext = userFilterTemplate.newSourceBuilder()
    .withQuerySource(request)
    .withBodySource(filterRequests)
    .buildFilterContext();
// Example query param: `?createdBy.firstName.contains=John`
// Example request body: `[{"field": "createdBy.firstName", "operator": "contains", "value": "John"}]`


// Sorting on nested property (e.g., createdBy.lastName DESC)
SortContext<User> sortContext = userSortTemplate.newSourceBuilder()
    .withQuerySource(request)
    .withBodySource(sortRequests)
    .buildSortContext();
// Example query param: `?sort=-createdBy.lastName`
// Example request body: `[{"field": "createdBy.lastName", "direction": "ASC"}]`

```

### Using ListAPIRequest in Controller and Service

You can use the provided `ListAPIRequest` DTO to accept filtering and sorting criteria in a structured way, making your API endpoints consistent and predictable. Below is a complete example of a controller and service using `ListAPIRequest`:

#### Controller Example

```java
import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/list")
    public ResponseEntity<List<User>> listUsers(@Valid @RequestBody ListAPIRequest request) {
        List<User> users = userService.listUsers(request);
        return ResponseEntity.ok(users);
    }
}
```

#### Service Example
```java
@Service
public class UserService {
    private final QueryFilterBuilder<User> queryFilterBuilder;
    private final UserRepository userRepository;
    private final FilterContext.Template<User> userFilterTemplate;
    private final SortContext.Template<User> userSortTemplate;

    public UserService(QueryFilterBuilder<User> queryFilterBuilder,
                      UserRepository userRepository) {
        this.queryFilterBuilder = queryFilterBuilder;
        this.userRepository = userRepository;
        // Assume templates are initialized elsewhere and injected or built here
        this.userFilterTemplate = ...;
        this.userSortTemplate = ...;
    }

    public List<User> listUsers(ListAPIRequest request) {
        FilterContext<User> filterContext = userFilterTemplate.newSourceBuilder()
            .withBodySource(request.filters())
            .buildFilterContext();
        SortContext<User> sortContext = userSortTemplate.newSourceBuilder()
            .withBodySource(request.sorts())
            .buildSortContext();
        Specification<User> filterSpecification = queryFilterBuilder.buildFilterSpecification(filterContext);
        Specification<User> sortSpecification = queryFilterBuilder.buildSortSpecification(sortContext);
        return userRepository.findAll(Specification.where(filterSpecification).and(sortSpecification));
    }
}
```

This approach ensures maintainable, consistent, and predictable results for your API endpoints.

## Supported Operators

Operator literals are case-insensitive: whether sent in query parameters or request body, any case (e.g., `EQ`, `eq`, `Eq`, `eQ`) will be accepted and correctly interpreted.

| Operator      | Description                  |
|---------------|------------------------------|
| `eq`          | Equal to                     |
| `neq`         | Not equal to                 |
| `gt`          | Greater than                 |
| `gte`         | Greater than or equal to     |
| `lt`          | Less than                    |
| `lte`         | Less than or equal to        |
| `in`          | In a list of values          |
| `notIn`       | Not in a list of values      |
| `isNull`      | Is null                      |
| `isNotNull`   | Is not null                  |
| `contains`    | Contains string              |
| `icontains`   | Case-insensitive contains    |
| `startsWith`  | Starts with                  |
| `istartsWith` | Case-insensitive starts with |
| `endsWith`    | Ends with                    |
| `iendsWith`   | Case-insensitive ends with   |
| `between`     | Between two values           |
| `notBetween`  | Not between two values       |

## Supported Types

Query Filter Builder supports a wide range of types for filtering and sorting, classified as follows:

### Numeric/Boolean Types
- `Byte`, `Short`, `Integer`, `Long`, `Float`, `Double`, `BigDecimal`, `BigInteger`, `Boolean`

### Text Types
- `Character`, `String`, Any Java `enum` type (enums are treated as text for filtering and sorting)

### Special Types
- `UUID`

### Date and Time Types
- `Instant`, `OffsetDateTime`, `ZonedDateTime`, `OffsetTime`, `LocalDateTime`, `LocalDate`, `LocalTime`, `YearMonth`, `Year`

## Advanced Features

### Custom Filters

Custom filters allow for complex logic beyond standard operators. Define them in the template using `addCustomFilter`.

See the [Defining Filter and Sort Templates](#defining-filter-and-sort-templates) example for usage.

### Custom Sorts

Custom sorts allow for complex logic beyond simple sorting. Define them in the template using `addCustomSort`.

See the [Defining Filter and Sort Templates](#defining-filter-and-sort-templates) example for usage.

### Enum Filter Implementation

Query Filter Builder provides a default implementation for enum filtering via `EnumFilterFieldImp`, which uses `Enum.valueOf(Class, String)` for casting string values to enum types. This is suitable for most use cases where enum names are matched exactly.

#### Custom Enum Filter Implementation
If you need to customize how enum values are parsed (e.g., case-insensitive matching, mapping from other string representations), you can provide your own implementation by subclassing `AbstractEnumFilterField` and overriding the `cast(Class<T>, String)` method.

To use your custom implementation, annotate your bean with `@Primary` so that Spring will inject it instead of the default:

```java
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class MyCustomEnumFilterField extends AbstractEnumFilterField {
    @Override
    public <T extends Enum<T>> T cast(Class<T> enumClass, String value) {
        // Custom logic, e.g., case-insensitive
        for (T constant : enumClass.getEnumConstants()) {
            if (constant.name().equalsIgnoreCase(value)) {
                return constant;
            }
        }
        throw new IllegalArgumentException("No enum constant " + enumClass.getName() + "." + value);
    }
}
```

Spring will automatically use your implementation for enum filtering if it is marked as `@Primary`.

### Sorting via HTTP Query Parameters

When sorting via HTTP query parameters, the `sort` parameter defines the fields and their sorting order. The format is as follows:

```
sort=-field1,field2,field3,...
```

- A leading `-` before a field name indicates descending order.
- If no `-` is present, the field is sorted in ascending order.
- Multiple fields can be specified, separated by commas. The sorting respects the order of fields as they appear in the query parameter.

#### Example:

```
sort=-firstName,lastName,createdAt
```

- `firstName` will be sorted in descending order.
- `lastName` will be sorted in ascending order.
- `createdAt` will be sorted in ascending order.

### Sorting via Request Body

- The order of fields in the request body determines the sorting precedence.
- Sorting direction literals are case-insensitive. Any case (e.g., ASC, asc, aSc, DESC, desc, dEsC) will be accepted and correctly interpreted.
- Defaults to ASC if no direction is specified or if the direction is invalid string.

This ensures that the sorting process is predictable and respects the client's specified order, whether provided in query parameters or the request body.

## Edge Cases & Error Handling

- **Request Body Override**: If a filter or sort field is provided in both query parameters and the request body, the value from the request body will take precedence and override the one from the query parameters.
- If a field passed via query parameters or request body is not defined in the filter/sort template, it is silently ignored.
- If the entity class is not a JPA `@Entity`, an exception is thrown.
- If the context or required arguments are null, a `NullPointerException` is thrown.
- If no valid filters or sorts are found, the resulting specification will not filter or sort any results.
- Unsupported types will throw an error at template build time.
- In all other error scenarios, an exception is thrown to ensure predictable behavior and easier debugging. This includes:
    - Invalid value for a field (e.g., "abc" for a number).
    - Invalid operator for a field.
    - Invalid field path for nested properties.
    - Misconfiguration of templates.
- The specific exceptions thrown are detailed in the `Exception Handling` section.

### Exception Handling

Query Filter Builder provides robust exception handling to ensure predictable error responses and easier debugging.

#### Built-in Exception Handler
- The package provides a base exception handler class (`QueryFilterBuilderExceptionHandler`). To activate exception handling, you must subclass this base class in your application and annotate it with `@ControllerAdvice`. This enables automatic handling of exceptions thrown by the library and returns meaningful error responses in your Spring Boot application.
- You can further customize the error handling by overriding methods in your subclass.

#### Exceptions Thrown

##### - InvalidQueryParameterException
- **Description:** Thrown when a query parameter or request body contains invalid, unrecognized, or unconvertible values (e.g., invalid value).
- **Handling:** The exception handler will return a clear error message indicating which parameter or value was invalid, helping clients correct their requests.

##### - QueryBuilderConfigurationException
- **Description:** Thrown for internal errors within the filter builder, such as misconfiguration, template errors, or unexpected failures during query construction.
- **Handling:** The exception handler will return a clear error message indicating which parameter or value was invalid, helping developers correct their templates.

For custom error handling, you can provide your own `@RestControllerAdvice` and handle these exceptions as needed.

## Examples

### Filtering and Sorting Users

```java
// Assume userFilterTemplate and userSortTemplate are defined as above
FilterContext<User> filterContext = userFilterTemplate.newSourceBuilder()
    .withQuerySource(request)
    .buildFilterContext();
SortContext<User> sortContext = userSortTemplate.newSourceBuilder()
    .withQuerySource(request)
    .buildSortContext();

Specification<User> filterSpec = queryFilterBuilder.buildFilterSpecification(filterContext);
Specification<User> sortSpec = queryFilterBuilder.buildSortSpecification(sortContext);

List<User> users = userRepository.findAll(Specification.where(filterSpec).and(sortSpec));
```

### Filtering on Nested Fields

```java
// Example: Filter users by their creator's first name
FilterContext<User> filterContext = userFilterTemplate.newSourceBuilder()
    .withQuerySource(request)
    .buildFilterContext();
// Query param: ?createdBy.firstName=John
```

### Sorting on Nested Fields

```java
// Example: Sort users by their creator's last name descending
SortContext<User> sortContext = userSortTemplate.newSourceBuilder()
    .withQuerySource(request)
    .buildSortContext();
// Query param: ?sort=-createdBy.lastName
```

## DTOs for Consistent API Design

### ListAPIRequest

The package provides a convenient `ListAPIRequest` DTO, which encapsulates lists of `FilterRequest` and `SortRequest` objects. This DTO is ready to use in any controller method, allowing you to accept filtering and sorting criteria in a structured and predictable way:

```java
@PostMapping("/users/list")
public ResponseEntity<List<User>> listUsers(@RequestBody ListAPIRequest request) {
    Specification<User> spec = filterContext.newSourceBuilder().fromFilterRequests(request.filters()).build();
    Sort sort = sortContext.newSourceBuilder().fromSortRequests(request.sorts()).build();
    List<User> users = userRepository.findAll(spec, sort);
    return ResponseEntity.ok(users);
}
```

Using `ListAPIRequest` helps standardize your API endpoints, making development more consistent and results more predictable. It also improves maintainability by providing a single DTO for both filtering and sorting operations.

## Public API Reference

### FilterContext
- `buildTemplateForType(Class<T> type)`: Start building a filter template for an entity.
- `TemplateBuilder<T>.queryParam(Consumer<FilterConfigurer<T>>)`: Configure the filter template for query parameters.
- `TemplateBuilder<T>.requestBody(Consumer<FilterConfigurer<T>>)`: Configure the filter template for request body.
- `FilterConfigurer<T>.addFilter(String, Operator...)`: Add a filter with specified operators array to the template.
- `FilterConfigurer<T>.addFilter(String, ExpressionProviderFunction, Operator...)`: Add a filter with expression provider function and specified operators array to the template.
- `FilterConfigurer<T>.addCustomFilter(String, Class, CustomFilterFunction)`: Add a custom filter with specified datatype for input and custom filter function to the template.
- `TemplateBuilder<T>.buildTemplate()`: Build the filter template.
- `Template<T>.newSourceBuilder()`: Create a new SourceBuilder for the template.
- `SourceBuilder<T>.withQuerySource(HttpServletRequest)`: Use query parameters as source.
- `SourceBuilder<T>.withBodySource(List<FilterRequest>)`: Use request body as source.
- `SourceBuilder<T>.buildFilterContext()`: Build the FilterContext.

### SortContext
- `buildTemplateForType(Class<T> type)`: Start building a sort template for an entity.
- `TemplateBuilder<T>.queryParam(Consumer<SortConfigurer<T>>)`: Configure the sort template for query parameters.
- `TemplateBuilder<T>.requestBody(Consumer<SortConfigurer<T>>)`: Configure the sort template for request body.
- `SortConfigurer<T>.addAscSort(String)`: Add an ascending sort for the specified field to the template.
- `SortConfigurer<T>.addAscSort(String, ExpressionProviderFunction)`: Add an ascending sort with expression provider function for the specified field to the template.
- `SortConfigurer<T>.addDescSort(String)`: Add a descending sort for the specified field to the template.
- `SortConfigurer<T>.addDescSort(String, ExpressionProviderFunction)`: Add a descending sort with expression provider function for the specified field to the template.
- `SortConfigurer<T>.addSorts(String)`: Add both ascending and descending sorts for the specified field to the template.
- `SortConfigurer<T>.addSorts(String, ExpressionProviderFunction)`: Add both ascending and descending sorts with expression provider function for the specified field to the template.
- `SortConfigurer<T>.addCustomSort(String, CustomSortFunction)`: Add a custom sort for the specified field to the template.
- `TemplateBuilder<T>.buildTemplate()`: Build the sort template.
- `Template<T>.newSourceBuilder()`: Create a new SourceBuilder for the template.
- `SourceBuilder<T>.withQuerySource(HttpServletRequest)`: Use query parameters as source.
- `SourceBuilder<T>.withBodySource(List<SortRequest>)`: Use request body as source.
- `SourceBuilder<T>.buildSortContext()`: Build the SortContext.

### QueryFilterBuilder
- `buildFilterSpecification(FilterContext<T>)`: Build a filter specification.
- `buildSortSpecification(SortContext<T>)`: Build a sort specification.

---

For more advanced usage, see the integration tests and Javadoc.

---

## Contributing

We welcome contributions from the community to make Query Filter Builder even better! Please refer to the [CONTRIBUTING.md](CONTRIBUTING.md) file for detailed guidelines on how to contribute to the project.

---

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.