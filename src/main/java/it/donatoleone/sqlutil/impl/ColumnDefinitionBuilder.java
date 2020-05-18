package it.donatoleone.sqlutil.impl;

import it.donatoleone.sqlutil.interfaces.create.ColumnDefinition;
import it.donatoleone.sqlutil.interfaces.create.CreateTable;
import it.donatoleone.sqlutil.interfaces.create.RestrictionColumnDefinition;
import it.donatoleone.sqlutil.util.MessageFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.Objects;

final class ColumnDefinitionBuilder implements ColumnDefinition, RestrictionColumnDefinition {

    private final CreateTable parent;
    private final String columnName;
    private boolean primary;
    private boolean nullable = true;
    private JDBCType type;
    private int size;
    private int precision;

    ColumnDefinitionBuilder(String column, CreateTable parent) {
        this.columnName = Objects.requireNonNull(column, MessageFactory.notNull("column"));
        this.parent = parent;
    }

    @Override
    public String getName() {
        return this.columnName;
    }

    @Override
    public boolean isPrimary() {
        return this.primary;
    }

    @Override
    public RestrictionColumnDefinition primary() {
        this.primary = true;
        return this;
    }

    @Override
    public RestrictionColumnDefinition notNull() {
        this.nullable = false;
        return this;
    }

    @Override
    public RestrictionColumnDefinition withType(JDBCType type) {
        this.type = Objects.requireNonNull(type, MessageFactory.notNull("type"));
        return this;
    }

    @Override
    public RestrictionColumnDefinition withType(JDBCType type, int size) {
        withType(type);
        if (size <= 0) {
            throw new IllegalArgumentException(MessageFactory.notValid("size", MessageFactory.LESS_EQUALS_ZERO));
        }
        this.size = size;
        return this;
    }

    @Override
    public RestrictionColumnDefinition withType(JDBCType type, int size, int precision) {
        withType(type, size);
        if (precision <= 0) {
            throw new IllegalArgumentException(MessageFactory.notValid("precision", MessageFactory.LESS_EQUALS_ZERO));
        } else if (precision > size) {
            throw new IllegalArgumentException(MessageFactory.notValid("precision", "> size"));
        }
        this.precision = precision;
        return this;
    }

    @Override
    public ColumnDefinition column(String column) {
        return this.parent.column(column);
    }

    @Override
    public void execute(Connection connection) throws SQLException {
        this.parent.execute(connection);
    }

    @Override
    public void execute(DataSource dataSource) throws SQLException {
        this.parent.execute(dataSource);
    }

    @Override
    public String getSql() {
        return this.parent.getSql();
    }

    @Override
    public String getDebugSql() {
        return this.parent.getDebugSql();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(columnName.toUpperCase()).append(" ");
        builder.append(type);
        if (size > 0) {
            builder.append("(").append(size);
            if (precision > 0) {
                builder.append(",").append(precision);
            }
            builder.append(")");
        }
        if (!this.nullable) {
            builder.append(" ").append("NOT NULL");
        }

        return builder.toString();
    }

    @Override
    public int compareTo(ColumnDefinition o) {
        return this.columnName.compareTo(o.getName());
    }
}
