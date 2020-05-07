package it.donatoleone.sqlutil.interfaces;

import java.util.List;

/**
 * Define an on cause used in {@link Join} operations
 *
 * @param <N> return type of operations
 */
public interface On<N> extends CommonOperations<N>, SqlQuery {

    List<Object> getParams();
    N isEqualsToColumn(String column);
}
