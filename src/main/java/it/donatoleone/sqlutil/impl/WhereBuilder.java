package it.donatoleone.sqlutil.impl;

import java.util.List;

final class WhereBuilder<T> extends BaseWhere<T> {

    private final T parent;

    WhereBuilder(String column, T parent, boolean or) {
        super(column, or);
        this.parent = parent;
    }

    WhereBuilder(T parent, boolean or) {
        super("", or);
        this.parent = parent;
    }

    @Override
    T getReturn() {
        return this.parent;
    }

    @Override
    public List<Object> getParams() {
        return this.getParamsList();
    }
}
