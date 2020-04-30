package it.donatoleone.sqlutil.impl;

import it.donatoleone.sqlutil.enums.JoinType;
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

    static Where<From> buildWhere(From from) {
        return new WhereBuilder(from, false);
    }

    static Where<From> buildOrWhere(From from) {
        return new WhereBuilder(from, true);
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

    static Join buildJoin(JoinType joinType, String table, From from) {
        return new JoinBuilder(joinType, from, table);
    }

    static On<From> buildOn(String column, From from) {
        return new OnBuilder(column, from);
    }

    static On<LimitedOn> buildLimitedOn(String column) {
        return new LimitedOnBuilder(column, false);
    }

    static On<LimitedOn> buildLimitedOrOn(String column) {
        return new LimitedOnBuilder(column, true);
    }
}
