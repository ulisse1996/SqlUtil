package it.donatoleone.sqlutil.impl;

import it.donatoleone.sqlutil.interfaces.Insert;
import it.donatoleone.sqlutil.interfaces.InsertingValue;
import it.donatoleone.sqlutil.interfaces.SqlQuery;
import it.donatoleone.sqlutil.util.MessageFactory;
import it.donatoleone.sqlutil.util.QueryRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

final class InsertBuilder implements Insert {

    private final String table;
    private final List<InsertingValue> insertingValues;

    InsertBuilder(String table) {
        this.table = Objects.requireNonNull(table, MessageFactory.notNull(table));
        this.insertingValues = new ArrayList<>();
    }

    @Override
    public InsertingValue insert(String column) {
        InsertingValue value = StatementFactory.buildInserting(column, this);
        this.insertingValues.add(value);
        return value;
    }

    @Override
    public void execute(DataSource dataSource) throws SQLException {
        QueryRunner.executeInsert(
                getSql(),
                this.insertingValues.stream().map(InsertingValue::getValue)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()),
                dataSource
        );
    }

    @Override
    public void execute(Connection connection) throws SQLException {
        QueryRunner.executeInsert(
                getSql(),
                this.insertingValues.stream().map(InsertingValue::getValue)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()),
                connection
        );
    }

    @Override
    public String getSql() {
        return String.format("INSERT INTO %s (%s) VALUES (%s)",
                this.table,
                this.insertingValues.stream().map(InsertingValue::getColumn).collect(Collectors.joining(",")),
                this.insertingValues.stream().map(SqlQuery::getSql).collect(Collectors.joining(",")));
    }

    @Override
    public String getDebugSql() {
        return String.format("INSERT INTO %s (%s) VALUES (%s)",
                this.table,
                this.insertingValues.stream().map(InsertingValue::getColumn).collect(Collectors.joining(",")),
                this.insertingValues.stream().map(SqlQuery::getDebugSql).collect(Collectors.joining(",")));
    }
}