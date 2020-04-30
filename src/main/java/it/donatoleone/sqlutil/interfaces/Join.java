package it.donatoleone.sqlutil.interfaces;

public interface Join extends SqlQuery {

    On<From> on(String column);
    From on(CompoundOn compoundOn);
}
