package it.donatoleone.sqlutil.impl;

import it.donatoleone.sqlutil.interfaces.common.CompoundWhere;
import it.donatoleone.sqlutil.interfaces.common.LimitedWhere;
import it.donatoleone.sqlutil.interfaces.common.Where;

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
