# Query Filter Builder

[![Maven Central](https://img.shields.io/maven-central/v/io.github.0xorigin/query-filter-builder.svg)](https://search.maven.org/artifact/io.github.0xorigin/query-filter-builder)
[![javadoc](https://javadoc.io/badge2/io.github.0xorigin/query-filter-builder/javadoc.svg?kill-cache=true)](https://javadoc.io/doc/io.github.0xorigin/query-filter-builder)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

**Query Filter Builder** is a robust and versatile library designed to dynamically generate type-safe JPA queries from HTTP query parameters in Spring Boot applications.

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Requirements](#requirements)
- [Installation](#installation)
- [Quick Start](#quick-start)
- [Query Parameter Format](#query-parameter-format)
- [Supported Operators](#supported-operators)
- [Supported Types](#supported-types)
- [Error Handling](#error-handling)
- [Contributing](#contributing)
- [License](#license)

## Overview

**Query Filter Builder** simplifies the process of dynamically filtering data in Spring Boot applications by converting HTTP query parameters into type-safe JPA predicates. It eliminates the complexity of manually writing filtering logic while maintaining clean and maintainable code.

## Features

- 🚀 **Automatic Conversion**: Effortlessly transform query parameters into JPA predicates.
- 🎯 **Comprehensive Operators**: Supports a wide variety of filtering operators for flexibility.
- 🔒 **Type-Safe Validation**: Ensures data integrity with robust parameter validation.
- 🎨 **Clean API Design**: Provides an intuitive and developer-friendly API.
- 🔌 **Spring Data Integration**: Seamlessly works with Spring Data JPA and Hibernate.
- 📦 **Nested Property Filtering**: Enables filtering across related entities with ease.
- 🛠 **Extensibility**: Highly customizable to adapt to project-specific requirements.

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
    <version>1.0.0</version>
</dependency>
```

## Quick Start

Follow these simple steps to start using **Query Filter Builder**:

1. **Define a Specification**: Create a `Specification` for your entity.
2. **Inject QueryFilterBuilder**: Use the `QueryFilterBuilder` to simplify the construction of JPA predicates.
3. **Build a Filter Context**:
    - Use `addFilter(String fieldName, Operator... operators)` to define entity filters.
    - Use `addFilter(String fieldName, Class<? extends Comparable<?>> dataType, CustomFilterFunction<T> filterFunction)` for custom filters.
4. **Generate Predicates**: Invoke `buildFilterPredicate` to create dynamic JPA predicates.
5. **Return the Predicate**: Integrate the predicate into your `toPredicate` method.

### Example

```java
@Service
public class UserSpecification implements Specification<User> {

    private final QueryFilterBuilder<User> queryFilterBuilder;

    public UserSpecification(QueryFilterBuilder<User> queryFilterBuilder) {
        this.queryFilterBuilder = queryFilterBuilder;
    }

    @Override
    public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return cb.and(getQueryFilterPredicate(root, query, cb));
    }

    private Predicate getQueryFilterPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        FilterContext<User> filterContext = new FilterContext<>();
        filterContext.addFilter("createdBy", Operator.EQ)
                .addFilter("isActive", Operator.IS_NULL, Operator.IS_NOT_NULL)
                .addFilter("lastLogin", Operator.EQ, Operator.GTE)
                .addFilter("createdBy__lastLogin__createdBy", Operator.EQ)
                .addFilter("search", String.class, this::search);

        return queryFilterBuilder.buildFilterPredicate(root, query, cb, filterContext);
    }

    private Predicate search(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb, List<?> values, ErrorWrapper errorWrapper) {
        return cb.or(cb.equal(root.get("firstName"), values.get(0)), cb.equal(root.get("lastName"), values.get(0)));
    }

}
```

## Query Parameter Format

**Query Filter Builder** uses a consistent, delimiter-based format for query parameters:

```
field__path__to__property__operator=value
```

- **Default delimiter**: `__` (double underscores)
- **Customizable**: Override the delimiter using the `query-filter-builder.defaults.field-delimiter` property in your application's configuration.

Examples:
```
# Basic filtering
/api/users?name=john&age__gte=18

# Nested property filtering
/api/users?department__name__endsWith=Engineering&manager__email__contains=@company.com

# Complex filtering
/api/users?createdBy__lastLogin__between=2024-01-01,2024-12-31&status__in=ACTIVE,PENDING
```

### Supported Operators

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

### Numeric/Boolean Types
- `Byte`, `Short`, `Integer`, `Long`, `Float`, `Double`, `Boolean`

### Text Types
- `Character`, `String`

### Special Types
- `UUID`

### Date and Time Types
- `Instant`, `OffsetDateTime`, `ZonedDateTime`, `OffsetTime`, `LocalDateTime`, `LocalDate`, `LocalTime`, `YearMonth`, `Year`

## Error Handling

Comprehensive error handling is provided for invalid query parameters and misconfigurations. Use exception handlers to customize responses.
- See [QueryFilterBuilderExceptionHandler](https://github.com/0xOrigin/query-filter-builder/blob/master/src/main/java/io/github/_0xorigin/queryfilterbuilder/QueryFilterBuilderExceptionHandler.java).

## Contributing

1. Fork the repository
2. Create a feature/bug branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.
