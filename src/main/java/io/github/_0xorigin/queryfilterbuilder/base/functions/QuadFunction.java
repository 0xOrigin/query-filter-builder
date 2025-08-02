package io.github._0xorigin.queryfilterbuilder.base.functions;

@FunctionalInterface
public interface QuadFunction<T, U, W, V, R> {

    R apply(T t, U u, W w, V v);

}
