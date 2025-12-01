package io.github._0xorigin.queryfilterbuilder.base.wrappers;

import org.springframework.validation.BindingResult;

/**
 * A wrapper that holds the {@link BindingResult} and the {@link FilterWrapper}
 * currently being processed. This is used to provide context when reporting errors.
 *
 * @param bindingResult The Spring {@link BindingResult} instance where validation errors are collected.
 * @param filterWrapper The filter wrapper that is being processed when the error occurs.
 */
public record FilterErrorWrapper(
    BindingResult bindingResult,
    FilterWrapper filterWrapper
) {}
