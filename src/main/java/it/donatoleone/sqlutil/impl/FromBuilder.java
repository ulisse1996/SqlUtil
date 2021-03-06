package it.donatoleone.sqlutil.impl;

import it.donatoleone.sqlutil.enums.JoinType;
import it.donatoleone.sqlutil.enums.Ordering;
import it.donatoleone.sqlutil.interfaces.*;
import it.donatoleone.sqlutil.interfaces.select.From;
import it.donatoleone.sqlutil.interfaces.select.Join;
import it.donatoleone.sqlutil.interfaces.select.Select;
import it.donatoleone.sqlutil.interfaces.select.ThrowingFunction;
import it.donatoleone.sqlutil.util.Pair;
import it.donatoleone.sqlutil.util.QueryRunner;
import it.donatoleone.sqlutil.util.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class FromBuilder extends WhereFilterBuilder<From> implements From {

    private final String table;
    private final Select parent;
    private final List<Join> joins;
    private final List<Pair<String[], Ordering>> orders;
    private String tableId;

    FromBuilder(String table, Select select) {
        super();
        this.parent = select;
        this.table = table.toUpperCase();
        this.joins = new ArrayList<>();
        this.orders = new ArrayList<>();
    }

    @Override
    public String getSql() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.join(" ", parent.getSql(), "FROM " + table ))
            .append(this.tableId != null ? " "+tableId : "");
        buildParts(builder, SqlDefinition::getSql);
        return builder.toString();
    }

    @Override
    public String getDebugSql() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.join(" ", parent.getDebugSql(), "FROM " + table))
                .append(this.tableId != null ? " "+tableId : "");
        buildParts(builder, SqlDefinition::getDebugSql);
        return builder.toString();
    }

    private void buildParts(StringBuilder builder, Function<SqlDefinition, String> function) {
        if (!this.joins.isEmpty()) {
            extractJoins(builder, function);
        }
        if (!this.wheres.isEmpty()) {
            extractWheres(builder, function, this.wheres);
        }
        if (!this.compoundWheres.isEmpty()) {
            extractCompound(builder, function, this.compoundWheres, this.wheres.isEmpty());
        }

        if (!this.orders.isEmpty()) {
            extractOrders(builder);
        }
    }

    private void extractOrders(StringBuilder builder) {
        builder.append(" ORDER BY ");
        builder.append(this.orders.stream()
                .map(pair -> {
                    if (pair.getKey().length == 1) {
                        return String.format("%s %s", pair.getKey()[0], pair.getValue().name());
                    }

                    return Arrays.stream(pair.getKey())
                        .map(s -> String.format("%s %s", s, pair.getValue().name()))
                        .collect(Collectors.joining(", "));
                }).collect(Collectors.joining(", ")));
    }

    private void extractJoins(StringBuilder builder, Function<SqlDefinition, String> function) {
        this.joins.forEach(join -> {
            builder.append(" ");
            String sql = function.apply(join);
            if (sql.contains("ONAND ")) {
                sql = StringUtils.replaceSingle(sql);
            }
            sql = sql.replace("( ", "(");
            builder.append(sql);
        });

    }

    @Override
    public From withId(String tableId) {
        this.tableId = tableId;
        return this;
    }

    @Override
    public Join join(JoinType joinType, String table) {
        Join join = StatementFactory.buildJoin(joinType, table, this);
        this.joins.add(join);
        return join;
    }

    @Override
    public From orderBy(Ordering ordering, String... columns) {
        this.orders.add(Pair.immutable(columns, ordering));
        return this;
    }

    @Override
    public From orderBy(Ordering ordering, String column) {
        this.orders.add(Pair.immutable(new String[] {column}, ordering));
        return this;
    }

    @Override
    public List<Object> getParams() {
        // Join
        List<Object> joinValues = this.joins.stream()
                .map(Join::getParams)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        return Stream.concat(joinValues.stream(),
                    extractParams(this.wheres, this.compoundWheres).stream())
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> readSingle(DataSource dataSource) throws SQLException {
        return QueryRunner.select(getSql(), dataSource, this.parent.getColumns(), getParams());
    }

    @Override
    public Map<String, Object> readSingle(Connection connection) throws SQLException {
        return QueryRunner.select(getSql(), connection, this.parent.getColumns(), getParams());
    }

    @Override
    public <R> R readSingle(ThrowingFunction<ResultSet, R, SQLException> mapper, DataSource dataSource) throws SQLException {
        return QueryRunner.select(getSql(), dataSource, getParams(), mapper);
    }

    @Override
    public <R> R readSingle(ThrowingFunction<ResultSet, R, SQLException> mapper, Connection connection) throws SQLException {
        return QueryRunner.select(getSql(), connection, getParams(), mapper);
    }

    @Override
    public Optional<Map<String, Object>> readOptionalSingle(DataSource dataSource) throws SQLException {
        return QueryRunner.optSelect(getSql(), dataSource, this.parent.getColumns(), getParams());
    }

    @Override
    public Optional<Map<String, Object>> readOptionalSingle(Connection connection) throws SQLException {
        return QueryRunner.optSelect(getSql(), connection, this.parent.getColumns(), getParams());
    }

    @Override
    public <R> Optional<R> readOptionalSingle(ThrowingFunction<ResultSet, R, SQLException> mapper, DataSource dataSource) throws SQLException {
        return QueryRunner.optSelect(getSql(), dataSource, getParams(), mapper);
    }

    @Override
    public <R> Optional<R> readOptionalSingle(ThrowingFunction<ResultSet, R, SQLException> mapper, Connection connection) throws SQLException {
        return QueryRunner.optSelect(getSql(), connection, getParams(), mapper);
    }

    @Override
    public List<Map<String, Object>> readAll(DataSource dataSource) throws SQLException {
        return QueryRunner.selectAll(getSql(), dataSource, this.parent.getColumns(), getParams());
    }

    @Override
    public List<Map<String, Object>> readAll(Connection connection) throws SQLException {
        return QueryRunner.selectAll(getSql(), connection, this.parent.getColumns(), getParams());
    }

    @Override
    public <R> List<R> readAll(ThrowingFunction<ResultSet, R, SQLException> mapper, DataSource dataSource) throws SQLException {
        return QueryRunner.selectAll(getSql(), dataSource, getParams(), mapper);
    }

    @Override
    public <R> List<R> readAll(ThrowingFunction<ResultSet, R, SQLException> mapper, Connection connection) throws SQLException {
        return QueryRunner.selectAll(getSql(), connection, getParams(), mapper);
    }

    @Override
    public Stream<Map<String, Object>> stream(DataSource dataSource) throws SQLException {
        return QueryRunner.stream(getSql(), dataSource, this.parent.getColumns(), getParams());
    }

    @Override
    public Stream<Map<String, Object>> stream(Connection connection) throws SQLException {
        return QueryRunner.stream(getSql(), connection, this.parent.getColumns(), getParams(), false);
    }

    @Override
    public <R> Stream<R> stream(ThrowingFunction<ResultSet, R, SQLException> mapper, DataSource dataSource) throws SQLException {
        return QueryRunner.stream(getSql(), dataSource, getParams(), mapper);
    }

    @Override
    public <R> Stream<R> stream(ThrowingFunction<ResultSet, R, SQLException> mapper, Connection connection) throws SQLException {
        return QueryRunner.stream(getSql(), connection, getParams(), mapper, false);
    }

    @Override
    From getReturn() {
        return this;
    }
}
