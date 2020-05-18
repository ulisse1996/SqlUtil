package it.donatoleone.sqlutil.interfaces.select;

import it.donatoleone.sqlutil.interfaces.SqlDefinition;

public interface Alias extends SqlDefinition {

    String getKey();
    String getAlias();
}
