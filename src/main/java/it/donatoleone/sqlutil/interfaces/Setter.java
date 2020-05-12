package it.donatoleone.sqlutil.interfaces;

import java.util.List;

public interface Setter extends SqlQuery {

    Update withValue(Object value);
    Update withExpression(String expression);
    Update withResult(From query);

    List<Object> getParams();
}
