package it.donatoleone.sqlutil.interfaces.update;

import it.donatoleone.sqlutil.interfaces.select.From;
import it.donatoleone.sqlutil.interfaces.SqlDefinition;

import java.util.List;

public interface Setter extends SqlDefinition {

    Update withValue(Object value);
    Update withExpression(String expression);
    Update withResult(From query);

    List<Object> getParams();
}
