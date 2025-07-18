package io.github._0xorigin.queryfilterbuilder.base;

import io.github._0xorigin.queryfilterbuilder.base.wrapper.FilterWrapper;

import java.util.List;

@FunctionalInterface
public interface Parser {

    List<FilterWrapper> parse();

}
