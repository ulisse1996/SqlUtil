package it.donatoleone.sqlutil.impl;

import it.donatoleone.sqlutil.enums.JoinType;
import it.donatoleone.sqlutil.interfaces.common.LimitedWhere;
import it.donatoleone.sqlutil.interfaces.common.Where;
import it.donatoleone.sqlutil.interfaces.create.ColumnDefinition;
import it.donatoleone.sqlutil.interfaces.create.CreateTable;
import it.donatoleone.sqlutil.interfaces.insert.Insert;
import it.donatoleone.sqlutil.interfaces.insert.InsertingValue;
import it.donatoleone.sqlutil.interfaces.insert.LimitedInsertingValue;
import it.donatoleone.sqlutil.interfaces.select.*;
import it.donatoleone.sqlutil.interfaces.update.Setter;
import it.donatoleone.sqlutil.interfaces.update.Update;

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

    static <T> Where<T> buildWhere(String column, T parent) {
        return new WhereBuilder<>(column, parent, false);
    }

    static <T> Where<T> buildWhere(T parent) {
        return new WhereBuilder<>(parent, false);
    }

    static <T> Where<T> buildOrWhere(T parent) {
        return new WhereBuilder<>(parent, true);
    }

    static <T> Where<T> buildOrWhere(String column, T parent) {
        return new WhereBuilder<>(column, parent, true);
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

    static Insert buildInsert(String table) {
        return new InsertBuilder(table);
    }

    static InsertingValue buildInserting(String column, Insert parent) {
        return new InsertingBuilder(column, parent);
    }

    static LimitedInsertingValue buildInserting(Insert parent, String[] columns) {
        return new InsertingBuilder(columns, parent);
    }

    static Update buildUpdate(String table) {
        return new UpdateBuilder(table);
    }

    static Setter buildSetter(String column, Update parent) {
        return new SetterBuilder(column, parent);
    }

    static CreateTable buildCreateTable(String table) {
        return new CreateTableBuilder(table);
    }

    static ColumnDefinition buildColumn(String column, CreateTable parent) {
        return new ColumnDefinitionBuilder(column, parent);
    }
}
