package io.github._0xorigin.queryfilterbuilder.base.functions;

@FunctionalInterface
public interface QuintFunction<T, U, W, V, E, R> {

    R apply(T t, U u, W w, V v, E e);

}
