package it.donatoleone.sqlutil.interfaces;

public interface Insert extends SqlQuery, LimitedInsert {

    InsertingValue insert(String column);
    LimitedInsertingValue insert(String... columns);
}
