package it.donatoleone.sqlutil.interfaces.select;

import it.donatoleone.sqlutil.interfaces.SqlDefinition;

import java.util.List;

public interface Join extends SqlDefinition {

    On<From> on(String column);
    From on(CompoundOn compoundOn);
    List<Object> getParams();
}
