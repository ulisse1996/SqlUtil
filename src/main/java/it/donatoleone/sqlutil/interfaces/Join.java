package it.donatoleone.sqlutil.interfaces;

import java.util.List;

public interface Join extends SqlQuery {

    On<From> on(String column);
    From on(CompoundOn compoundOn);
    List<Object> getParams();
}
