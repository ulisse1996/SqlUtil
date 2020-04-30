package it.donatoleone.sqlutil.interfaces;

public interface Select extends SqlQuery {

    From from(String table);
}
