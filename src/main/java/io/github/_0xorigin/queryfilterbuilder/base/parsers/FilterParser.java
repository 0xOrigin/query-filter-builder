package io.github._0xorigin.queryfilterbuilder.base.parsers;

import io.github._0xorigin.queryfilterbuilder.base.dtos.FilterRequest;
import io.github._0xorigin.queryfilterbuilder.base.wrappers.FilterWrapper;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface FilterParser {

    List<FilterWrapper> parse(HttpServletRequest request);

    List<FilterWrapper> parse(List<FilterRequest> filterRequests);

}
