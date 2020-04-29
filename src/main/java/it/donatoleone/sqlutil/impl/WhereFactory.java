package it.donatoleone.sqlutil.impl;

import it.donatoleone.sqlutil.interfaces.CompoundWhere;
import it.donatoleone.sqlutil.interfaces.LimitedWhere;
import it.donatoleone.sqlutil.interfaces.Where;

public final class WhereFactory {

    private WhereFactory() {}

    public static CompoundWhere compoundWhere(LimitedWhere... wheres) {
        return new CompoundWhereBuilder(wheres);
    }

    public static Where<LimitedWhere> where(String column) {
        return StatementFactory.buildLimitedWhere(column);
    }

    public static Where<LimitedWhere> orWhere(String column) {
        return StatementFactory.buildLimitedOrWhere(column);
    }
}
