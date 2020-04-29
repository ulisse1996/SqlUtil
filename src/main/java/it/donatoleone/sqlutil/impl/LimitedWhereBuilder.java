package it.donatoleone.sqlutil.impl;

import it.donatoleone.sqlutil.interfaces.LimitedWhere;

final class LimitedWhereBuilder extends BaseWhere<LimitedWhere> implements LimitedWhere {

    @Override
    LimitedWhere getReturn() {
        return this;
    }

    LimitedWhereBuilder(String column, boolean or) {
        super(column, or);
    }
}
