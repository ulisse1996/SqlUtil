package it.donatoleone.sqlutil.interfaces.insert;

import it.donatoleone.sqlutil.interfaces.select.From;

public interface LimitedInsertingValue extends Inserted {

    LimitedInsert withResult(From query);
}
