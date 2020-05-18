package it.donatoleone.sqlutil.impl;

import it.donatoleone.sqlutil.interfaces.common.LimitedWhere;

import java.util.List;

final class LimitedWhereBuilder extends BaseWhere<LimitedWhere> implements LimitedWhere {

    @Override
    LimitedWhere getReturn() {
        return this;
    }

    LimitedWhereBuilder(String column, boolean or) {
        super(column, or);
    }

    @Override
    public List<Object> getParams() {
        return this.getParamsList();
    }
}
