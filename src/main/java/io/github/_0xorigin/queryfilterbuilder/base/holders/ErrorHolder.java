package io.github._0xorigin.queryfilterbuilder.base.holders;

import org.springframework.core.MethodParameter;
import org.springframework.validation.BindingResult;

public record ErrorHolder(
    BindingResult bindingResult,
    MethodParameter methodParameter
) {}
