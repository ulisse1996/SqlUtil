package it.donatoleone.sqlutil.impl;

import it.donatoleone.sqlutil.interfaces.*;
import it.donatoleone.sqlutil.interfaces.insert.Insert;
import it.donatoleone.sqlutil.interfaces.insert.Inserted;
import it.donatoleone.sqlutil.interfaces.insert.InsertingValue;
import it.donatoleone.sqlutil.interfaces.insert.LimitedInsertingValue;
import it.donatoleone.sqlutil.util.MessageFactory;
import it.donatoleone.sqlutil.util.QueryRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

final class InsertBuilder implements Insert {

    private final String table;
    private final List<Inserted> insertingValues;

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
    public LimitedInsertingValue insert(String... columns) {
        LimitedInsertingValue value = StatementFactory.buildInserting(this, columns);
        this.insertingValues.add(value);
        return value;
    }

    @Override
    public void execute(DataSource dataSource) throws SQLException {
        QueryRunner.execute(
                getSql(),
                this.insertingValues.stream().flatMap(in -> in.getValue().stream())
                    .collect(Collectors.toList()),
                dataSource
        );
    }

    @Override
    public void execute(Connection connection) throws SQLException {
        QueryRunner.execute(
                getSql(),
                this.insertingValues.stream().flatMap(in -> in.getValue().stream())
                        .collect(Collectors.toList()),
                connection
        );
    }

    private boolean isSubQuery() {
        return this.insertingValues.size() == 1
                && this.insertingValues.get(0).isSelectInsert();
    }

    @Override
    public String getSql() {
        if (isSubQuery()) {
            return getSql("INSERT INTO %s (%s) %s", SqlDefinition::getSql);
        }
        return getSql("INSERT INTO %s (%s) VALUES (%s)", SqlDefinition::getSql);
    }

    @Override
    public String getDebugSql() {
        if (isSubQuery()) {
            return getSql("INSERT INTO %s (%s) %s", SqlDefinition::getDebugSql);
        }
        return String.format("INSERT INTO %s (%s) VALUES (%s)",
                this.table,
                this.insertingValues.stream().flatMap(in -> in.getColumns().stream()).collect(Collectors.joining(",")),
                this.insertingValues.stream().map(SqlDefinition::getDebugSql).collect(Collectors.joining(",")));
    }

    private String getSql(String format, Function<SqlDefinition, String> function) {
        return String.format(format,
                this.table,
                this.insertingValues.stream().flatMap(in -> in.getColumns().stream()).collect(Collectors.joining(",")),
                this.insertingValues.stream().map(function).collect(Collectors.joining(",")));
    }
}
