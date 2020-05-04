package it.donatoleone.sqlutil.interfaces;

import java.util.List;

public interface LimitedWhere extends SqlQuery {

    List<Object> getParams();
}
