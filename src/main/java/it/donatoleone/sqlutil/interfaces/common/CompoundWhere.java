package it.donatoleone.sqlutil.interfaces.common;

import it.donatoleone.sqlutil.interfaces.SqlDefinition;

import java.util.List;

public interface CompoundWhere extends SqlDefinition {

    void setOr(boolean or);
    List<Object> getParams();
}
