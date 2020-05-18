package it.donatoleone.sqlutil.interfaces.select;

import it.donatoleone.sqlutil.interfaces.SqlDefinition;

import java.util.List;

public interface Select extends SqlDefinition {

    From from(String table);
    List<String> getColumns();
}
