package it.donatoleone.sqlutil.impl;

import it.donatoleone.sqlutil.interfaces.Alias;
import it.donatoleone.sqlutil.interfaces.Insert;
import it.donatoleone.sqlutil.interfaces.Select;

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
}
