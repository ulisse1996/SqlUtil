package it.donatoleone.sqlutil.impl;

import it.donatoleone.sqlutil.interfaces.select.CompoundOn;
import it.donatoleone.sqlutil.interfaces.select.LimitedOn;
import it.donatoleone.sqlutil.interfaces.select.On;

public final class OnFactory {

    private OnFactory() {}

    public static CompoundOn compoundOn(LimitedOn... ons) {
        return new CompoundOnBuilder(ons);
    }

    public static On<LimitedOn> on(String column) {
        return StatementFactory.buildLimitedOn(column);
    }

    public static On<LimitedOn> orOn(String column) {
        return StatementFactory.buildLimitedOrOn(column);
    }
}
