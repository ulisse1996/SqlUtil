package it.donatoleone.sqlutil.interfaces.common;

import it.donatoleone.sqlutil.interfaces.SqlDefinition;

import java.util.List;

public interface LimitedWhere extends SqlDefinition {

    List<Object> getParams();
}
