package it.donatoleone.sqlutil.interfaces;

import java.util.List;

public interface LimitedOn extends SqlQuery {

    List<Object> getParams();
}
