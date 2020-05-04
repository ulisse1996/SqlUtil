package it.donatoleone.sqlutil.interfaces;

import java.util.List;

public interface Where<N> extends SqlQuery, CommonOperations<N> {

    void exists(From subQuery);
    List<Object> getParams();
}
