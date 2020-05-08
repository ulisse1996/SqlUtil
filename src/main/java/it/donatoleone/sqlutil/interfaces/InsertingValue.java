package it.donatoleone.sqlutil.interfaces;

public interface InsertingValue extends SqlQuery {

    Insert withValue(Object value);
    Insert withExpression(String expression);
    Insert withResult(From query);
    String getColumn();
    Object getValue();
}
