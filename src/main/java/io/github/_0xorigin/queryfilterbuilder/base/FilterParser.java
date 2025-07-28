package io.github._0xorigin.queryfilterbuilder.base;

import io.github._0xorigin.queryfilterbuilder.base.dtos.FilterRequest;
import io.github._0xorigin.queryfilterbuilder.base.wrappers.FilterWrapper;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface FilterParser {

    List<FilterWrapper> parse(final HttpServletRequest request);

    List<FilterWrapper> parse(final List<FilterRequest> filterRequests);

}
