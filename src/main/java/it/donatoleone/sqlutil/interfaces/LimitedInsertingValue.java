package it.donatoleone.sqlutil.interfaces;

public interface LimitedInsertingValue extends Inserted {

    LimitedInsert withResult(From query);
}
