package it.donatoleone.sqlutil.interfaces;

import it.donatoleone.sqlutil.enums.LikeMatcher;

public interface CommonOperations<N> extends SqlQuery {

    N isEqualsTo(Object value);
    N isNotEqualsTo(Object value);
    N isLessThan(Object value);
    N isGreaterThan(Object value);
    N isGreaterOrEqualsTo(Object value);
    N isLessOrEqualsTo(Object value);
    <T> N between(T val1, T val2);
    <T> N in(Iterable<T> values);
    N like(String value, LikeMatcher matcher);
}
