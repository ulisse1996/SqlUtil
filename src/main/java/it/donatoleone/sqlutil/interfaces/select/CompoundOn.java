package it.donatoleone.sqlutil.interfaces.select;

import it.donatoleone.sqlutil.interfaces.SqlDefinition;

import java.util.List;

public interface CompoundOn extends SqlDefinition {

    List<Object> getParams();
}
