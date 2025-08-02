package io.github._0xorigin.queryfilterbuilder.base.parsers;

import io.github._0xorigin.queryfilterbuilder.base.dtos.SortRequest;
import io.github._0xorigin.queryfilterbuilder.base.wrappers.SortWrapper;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface SortParser {

    List<SortWrapper> parse(HttpServletRequest request);

    List<SortWrapper> parse(List<SortRequest> sortRequests);

}
