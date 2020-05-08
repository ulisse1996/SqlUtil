package it.donatoleone.sqlutil.interfaces;

import java.util.List;

public interface Select extends SqlQuery {

    From from(String table);
    List<String> getColumns();
}
