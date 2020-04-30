package it.donatoleone.sqlutil.interfaces;

public interface CompoundWhere extends SqlQuery {

    void setOr(boolean or);
}
