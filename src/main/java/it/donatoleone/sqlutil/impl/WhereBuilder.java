package it.donatoleone.sqlutil.impl;

import it.donatoleone.sqlutil.interfaces.From;

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
}
