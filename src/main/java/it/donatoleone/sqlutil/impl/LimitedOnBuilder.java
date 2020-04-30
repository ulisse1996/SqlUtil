package it.donatoleone.sqlutil.impl;

import it.donatoleone.sqlutil.interfaces.LimitedOn;

final class LimitedOnBuilder extends BaseOn<LimitedOn> implements LimitedOn {

    LimitedOnBuilder(String column, boolean or) {
        super(column, or);
    }

    @Override
    LimitedOn getReturn() {
        return this;
    }
}
