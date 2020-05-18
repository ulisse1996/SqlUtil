package it.donatoleone.sqlutil.impl;

import it.donatoleone.sqlutil.interfaces.create.ColumnDefinition;
import it.donatoleone.sqlutil.interfaces.create.CreateTable;
import it.donatoleone.sqlutil.util.MessageFactory;
import it.donatoleone.sqlutil.util.QueryRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

final class CreateTableBuilder implements CreateTable {

    private final String table;
    private final Set<ColumnDefinition> columns;

    CreateTableBuilder(String table) {
        this.table = Objects.requireNonNull(table, MessageFactory.notNull("table"));
        this.columns = new TreeSet<>();
    }

    @Override
    public ColumnDefinition column(String column) {
        ColumnDefinition columnDefinition = StatementFactory.buildColumn(column, this);
        this.columns.add(columnDefinition);
        return columnDefinition;
    }

    @Override
    public void execute(DataSource dataSource) throws SQLException {
        QueryRunner.execute(getSql(), Collections.emptyList(), dataSource);
    }

    @Override
    public void execute(Connection connection) throws SQLException {
        QueryRunner.execute(getSql(), Collections.emptyList(), connection);
    }

    @Override
    public String getSql() {
        return asSqlString();
    }

    @Override
    public String getDebugSql() {
        return asSqlString();
    }

    private String asSqlString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE ").append(table);
        builder.append(" (");
        builder.append(this.columns.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", ")));
        String primary = getPrimaryKey();
        if (!primary.isEmpty()) {
            builder.append(", PRIMARY KEY (").append(primary).append(")");
        }
        builder.append(")");
        return builder.toString();
    }

    private String getPrimaryKey() {
        return this.columns.stream()
                .filter(ColumnDefinition::isPrimary)
                .map(ColumnDefinition::getName)
                .collect(Collectors.joining(","));
    }
}
