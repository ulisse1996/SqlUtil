package it.donatoleone.sqlutil.interfaces;

public interface Select extends StringSequence {

    From from(String table);
}
