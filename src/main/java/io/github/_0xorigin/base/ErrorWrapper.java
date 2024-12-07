package io.github._0xorigin.base;

import org.springframework.validation.BindingResult;

public class ErrorWrapper {

    private final BindingResult bindingResult;
    private final FilterWrapper filterWrapper;

    public ErrorWrapper(BindingResult bindingResult, FilterWrapper filterWrapper) {
        this.bindingResult = bindingResult;
        this.filterWrapper = filterWrapper;
    }

    public BindingResult getBindingResult() {
        return bindingResult;
    }

    public FilterWrapper getFilterWrapper() {
        return filterWrapper;
    }

}
