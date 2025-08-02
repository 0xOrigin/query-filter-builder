package io.github._0xorigin.queryfilterbuilder.base.wrappers;

import org.springframework.validation.BindingResult;

public record FilterErrorWrapper(
    BindingResult bindingResult,
    FilterWrapper filterWrapper
) {}
