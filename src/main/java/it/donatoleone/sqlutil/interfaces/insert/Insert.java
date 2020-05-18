package it.donatoleone.sqlutil.interfaces.insert;

import it.donatoleone.sqlutil.interfaces.SqlDefinition;

public interface Insert extends SqlDefinition, LimitedInsert {

    InsertingValue insert(String column);
    LimitedInsertingValue insert(String... columns);
}
