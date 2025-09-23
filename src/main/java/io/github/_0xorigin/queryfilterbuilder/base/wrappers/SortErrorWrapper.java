package io.github._0xorigin.queryfilterbuilder.base.wrappers;

import org.springframework.validation.BindingResult;

/**
 * A wrapper that holds the {@link BindingResult} and the {@link SortWrapper}
 * currently being processed. This is used to provide context when reporting errors.
 *
 * @param bindingResult The Spring {@link BindingResult} instance where validation errors are collected.
 * @param sortWrapper   The sort wrapper that is being processed when the error occurs.
 */
public record SortErrorWrapper(
    BindingResult bindingResult,
    SortWrapper sortWrapper
) {}
