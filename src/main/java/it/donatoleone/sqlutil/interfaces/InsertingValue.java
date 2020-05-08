package it.donatoleone.sqlutil.interfaces;

public interface InsertingValue extends Inserted {

    Insert withValue(Object value);
    Insert withExpression(String expression);
}
