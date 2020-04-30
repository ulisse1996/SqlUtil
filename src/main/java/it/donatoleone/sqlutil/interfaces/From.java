package it.donatoleone.sqlutil.interfaces;

import it.donatoleone.sqlutil.enums.JoinType;

public interface From extends SqlQuery {

    Where<From> where(String column);
    From whereExists(From subQuery);
    From orWhereExists(From subQuery);
    From withId(String tableId);
    Where<From> orWhere(String column);
    From where(CompoundWhere where);
    From orWhere(CompoundWhere where);
    Join join(JoinType joinType, String table);
}
