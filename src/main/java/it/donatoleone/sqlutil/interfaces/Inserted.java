package it.donatoleone.sqlutil.interfaces;

import java.util.List;

public interface Inserted extends SqlQuery {

    List<String> getColumns();
    List<Object> getValue();
    boolean isSelectInsert();
}
