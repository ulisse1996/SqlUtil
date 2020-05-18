package it.donatoleone.sqlutil.interfaces.select;

import it.donatoleone.sqlutil.interfaces.common.CommonOperations;
import it.donatoleone.sqlutil.interfaces.SqlDefinition;

import java.util.List;

/**
 * Define an on cause used in {@link Join} operations
 *
 * @param <N> return type of operations
 */
public interface On<N> extends CommonOperations<N>, SqlDefinition {

    List<Object> getParams();
    N isEqualsToColumn(String column);
}
