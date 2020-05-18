package it.donatoleone.sqlutil.impl;

import it.donatoleone.sqlutil.interfaces.create.CreateTable;
import it.donatoleone.sqlutil.interfaces.insert.Insert;
import it.donatoleone.sqlutil.interfaces.select.Alias;
import it.donatoleone.sqlutil.interfaces.select.Select;
import it.donatoleone.sqlutil.interfaces.update.Update;

public final class SqlUtil {

    private SqlUtil() {}

    public static Select select(String... columns) {
        return StatementFactory.buildSelect(columns);
    }

    public static Select select() {
        return StatementFactory.buildSelect();
    }

    public static Select select(Alias... aliases) {
        return StatementFactory.buildSelect(aliases);
    }

    public static Insert insertInto(String table) {
        return StatementFactory.buildInsert(table);
    }

    public static Update update(String table) {
        return StatementFactory.buildUpdate(table);
    }

    public static CreateTable createTable(String table) {
        return StatementFactory.buildCreateTable(table);
    }
}
