package it.donatoleone.sqlutil.impl;

import it.donatoleone.sqlutil.interfaces.*;

final class StatementFactory {

    private StatementFactory() {}

    static Select buildSelect() {
        return new SelectBuilder();
    }

    static Select buildSelect(String... columns) {
        return new SelectBuilder(columns);
    }

    static Select buildSelect(Alias... aliases) {
        return new SelectBuilder(aliases);
    }

    static From buildFrom(String table, Select select) {
        return new FromBuilder(table, select);
    }

    static Where<From> buildWhere(String column, From from) {
        return new WhereBuilder(column, from, false);
    }

    static Where<From> buildOrWhere(String column, From from) {
        return new WhereBuilder(column, from, true);
    }

    static Where<LimitedWhere> buildLimitedWhere(String column) {
        return new LimitedWhereBuilder(column, false);
    }

    static Where<LimitedWhere> buildLimitedOrWhere(String column) {
        return new LimitedWhereBuilder(column, true);
    }
}
