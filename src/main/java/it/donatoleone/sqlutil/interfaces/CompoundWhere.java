package it.donatoleone.sqlutil.interfaces;

import java.util.List;

public interface CompoundWhere extends SqlQuery {

    void setOr(boolean or);
    List<Object> getParams();
}
