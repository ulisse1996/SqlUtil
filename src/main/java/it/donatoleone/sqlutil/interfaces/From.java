package it.donatoleone.sqlutil.interfaces;

public interface From extends StringSequence {

    Where<From> where(String column);
    Where<From> orWhere(String column);
    From where(CompoundWhere where);
    From orWhere(CompoundWhere where);
}
