package io.github._0xorigin.queryfilterbuilder.base;

import io.github._0xorigin.queryfilterbuilder.base.wrapper.FilterWrapper;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface FilterParser {

    List<FilterWrapper> parse(HttpServletRequest request);

}
