package it.donatoleone.sqlutil.impl;

import it.donatoleone.sqlutil.interfaces.From;

import java.util.List;

final class WhereBuilder extends BaseWhere<From> {

    WhereBuilder(String column, From from, boolean or) {
        super(column, from, or);
    }

    WhereBuilder(From from, boolean or) {
        super("", from, or);
    }

    @Override
    From getReturn() {
        return this.from;
    }

    @Override
    public List<Object> getParams() {
        return this.getParamsList();
    }
}
