package it.donatoleone.sqlutil.interfaces.insert;

import it.donatoleone.sqlutil.interfaces.SqlDefinition;

import java.util.List;

public interface Inserted extends SqlDefinition {

    List<String> getColumns();
    List<Object> getValue();
    boolean isSelectInsert();
}
