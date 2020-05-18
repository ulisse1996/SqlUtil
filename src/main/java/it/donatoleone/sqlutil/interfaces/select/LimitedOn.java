package it.donatoleone.sqlutil.interfaces.select;

import it.donatoleone.sqlutil.interfaces.SqlDefinition;

import java.util.List;

public interface LimitedOn extends SqlDefinition {

    List<Object> getParams();
}
