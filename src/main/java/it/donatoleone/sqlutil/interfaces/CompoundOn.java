package it.donatoleone.sqlutil.interfaces;

import java.util.List;

public interface CompoundOn extends SqlQuery {

    List<Object> getParams();
}
