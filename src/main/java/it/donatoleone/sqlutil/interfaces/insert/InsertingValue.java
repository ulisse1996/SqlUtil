package it.donatoleone.sqlutil.interfaces.insert;

public interface InsertingValue extends Inserted {

    Insert withValue(Object value);
    Insert withExpression(String expression);
}
