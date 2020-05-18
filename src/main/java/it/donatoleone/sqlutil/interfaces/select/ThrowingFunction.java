package it.donatoleone.sqlutil.interfaces.select;

/**
 * Extended Function that throw an {@link Exception} during {@link #apply(Object)} method
 * @param <T>
 * @param <R>
 * @param <X>
 */
public interface ThrowingFunction<T,R, X extends Exception> {

    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     */
    R apply(T t) throws X;
}
