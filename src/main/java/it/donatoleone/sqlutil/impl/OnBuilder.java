package it.donatoleone.sqlutil.impl;

import it.donatoleone.sqlutil.interfaces.From;

final class OnBuilder extends BaseOn<From> {

    OnBuilder(String column, From from) {
        super(column, from);
    }

    @Override
    From getReturn() {
        return this.from;
    }
}
