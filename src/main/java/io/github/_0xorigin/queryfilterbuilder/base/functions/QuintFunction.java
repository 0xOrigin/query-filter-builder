package io.github._0xorigin.queryfilterbuilder.base.functions;

/**
 * Represents a function that accepts five arguments and produces a result.
 * This is a functional interface whose functional method is {@link #apply(Object, Object, Object, Object, Object)}.
 *
 * @param <T> the type of the first argument to the function
 * @param <U> the type of the second argument to the function
 * @param <W> the type of the third argument to the function
 * @param <V> the type of the fourth argument to the function
 * @param <E> the type of the fifth argument to the function
 * @param <R> the type of the result of the function
 */
@FunctionalInterface
public interface QuintFunction<T, U, W, V, E, R> {

    /**
     * Applies this function to the given arguments.
     *
     * @param t the first function argument
     * @param u the second function argument
     * @param w the third function argument
     * @param v the fourth function argument
     * @param e the fifth function argument
     * @return the function result
     */
    R apply(T t, U u, W w, V v, E e);

}
