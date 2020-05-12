package it.donatoleone.sqlutil.impl;

import it.donatoleone.sqlutil.interfaces.From;

final class OnBuilder extends BaseOn<From> {

    private final From from;

    OnBuilder(String column, From from) {
        super(column);
        this.from = from;
    }

    @Override
    From getReturn() {
        return this.from;
    }
}
