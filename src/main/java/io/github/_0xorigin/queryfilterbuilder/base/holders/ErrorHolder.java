package io.github._0xorigin.queryfilterbuilder.base.holders;

import org.springframework.core.MethodParameter;
import org.springframework.validation.BindingResult;

/**
 * A holder for objects required for validation and error reporting.
 *
 * @param bindingResult   The Spring {@link BindingResult} instance where validation errors are collected.
 * @param methodParameter The {@link MethodParameter} that is being processed, providing context for validation.
 */
public record ErrorHolder(
    BindingResult bindingResult,
    MethodParameter methodParameter
) {}
