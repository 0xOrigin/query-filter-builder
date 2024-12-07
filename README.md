# Query Filter Builder

[![Maven Central](https://img.shields.io/maven-central/v/com.github.0xorigin/query-filter-builder)](https://central.sonatype.com/artifact/com.github.0xorigin/query-filter-builder)
[![License](https://img.shields.io/github/license/0xOrigin/query-filter-builder)](LICENSE)

## Overview
**Query Filter Builder** is a Spring Boot-based library designed to simplify filtering data in Spring Data JPA repositories. It allows developers to build complex, dynamic queries with minimal boilerplate while enforcing clean code practices.

---

## Features
- Define query filters declaratively using the `Specification` API.
- Supports multiple field operations like `EQUAL`, `GREATER_THAN`, `IN`, and more.
- Easily extendable to accommodate custom filtering logic.
- Pluggable and compatible with any Spring Boot application.

---

## Requirements
- Java 17 or higher.
- Spring Boot 3.1.0 or higher.

---

## Installation

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.github._0xorigin</groupId>
    <artifactId>query-filter-builder</artifactId>
    <version>0.0.0-alpha</version>
</dependency>
