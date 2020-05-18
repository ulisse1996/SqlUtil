package it.donatoleone.sqlutil.interfaces.create;

import java.sql.JDBCType;

public interface ColumnDefinition extends Comparable<ColumnDefinition> {

    RestrictionColumnDefinition withType(JDBCType type);
    RestrictionColumnDefinition withType(JDBCType type, int size);
    RestrictionColumnDefinition withType(JDBCType type, int size, int precision);

    boolean isPrimary();
    String getName();
}
