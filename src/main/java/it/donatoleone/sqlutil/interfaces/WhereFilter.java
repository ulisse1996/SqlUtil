package it.donatoleone.sqlutil.interfaces;

public interface WhereFilter<T> {

    Where<T> where(String column);
    T whereExists(From subQuery);
    T orWhereExists(From subQuery);
    Where<T> orWhere(String column);
    T where(CompoundWhere where);
    T orWhere(CompoundWhere where);
}
