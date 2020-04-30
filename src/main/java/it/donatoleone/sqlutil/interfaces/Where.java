package it.donatoleone.sqlutil.interfaces;

public interface Where<N> extends SqlQuery, CommonOperations<N> {

    void exists(From subQuery);
}
