package it.donatoleone.sqlutil.interfaces.common;

import it.donatoleone.sqlutil.interfaces.SqlDefinition;
import it.donatoleone.sqlutil.interfaces.select.From;

import java.util.List;

public interface Where<N> extends SqlDefinition, CommonOperations<N> {

    void exists(From subQuery);
    List<Object> getParams();
}
