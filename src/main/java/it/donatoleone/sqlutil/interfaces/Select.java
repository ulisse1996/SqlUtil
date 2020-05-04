package it.donatoleone.sqlutil.interfaces;

import java.util.Set;

public interface Select extends SqlQuery {

    From from(String table);
    Set<String> getColumns();
}
