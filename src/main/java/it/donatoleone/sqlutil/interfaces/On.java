package it.donatoleone.sqlutil.interfaces;

import java.util.List;

public interface On<N> extends CommonOperations<N>, SqlQuery {

    List<Object> getParams();
    N isEqualsToColumn(String column);
}
