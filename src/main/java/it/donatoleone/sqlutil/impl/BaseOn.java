package it.donatoleone.sqlutil.impl;

import it.donatoleone.sqlutil.interfaces.From;
import it.donatoleone.sqlutil.interfaces.On;

abstract class BaseOn<N> extends BaseCommonOperations<N> implements On<N> {

    BaseOn(String column, From from) {
        this.column = column;
        this.from = from;
    }

    BaseOn(String column, boolean or) {
        this.column = column;
        this.or = or;
    }
}
