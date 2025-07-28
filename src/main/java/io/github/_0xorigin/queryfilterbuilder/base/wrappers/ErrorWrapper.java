package io.github._0xorigin.queryfilterbuilder.base.wrappers;

import org.springframework.validation.BindingResult;

public record ErrorWrapper (
    BindingResult bindingResult,
    FilterWrapper filterWrapper
) {}
