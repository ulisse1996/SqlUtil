package it.donatoleone.sqlutil.impl;

import it.donatoleone.sqlutil.interfaces.*;
import it.donatoleone.sqlutil.util.QueryRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class UpdateBuilder extends WhereFilterBuilder<Update> implements Update {

    private final String table;
    private final List<Setter> setters;

    UpdateBuilder(String table) {
        super();
        this.table = table;
        this.setters = new ArrayList<>();
    }

    @Override
    Update getReturn() {
        return this;
    }

    @Override
    public Setter set(String column) {
        Setter setter = StatementFactory.buildSetter(column, this);
        this.setters.add(setter);
        return setter;
    }

    private List<Object> getParams() {
        // Setter
        List<Object> setterValues = this.setters.stream()
                .map(Setter::getParams)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        return Stream.concat(setterValues.stream(),
                    extractParams(this.wheres, this.compoundWheres).stream())
                .collect(Collectors.toList());
    }

    @Override
    public void execute(DataSource dataSource) throws SQLException {
        QueryRunner.execute(getSql(), getParams(), dataSource);
    }

    @Override
    public void execute(Connection connection) throws SQLException {
        QueryRunner.execute(getSql(), getParams(), connection);
    }

    private void buildParts(StringBuilder builder, Function<SqlQuery, String> function) {
        if (!this.wheres.isEmpty()) {
            extractWheres(builder, function, this.wheres);
        }
        if (!this.compoundWheres.isEmpty()) {
            extractCompound(builder, function, this.compoundWheres, this.wheres.isEmpty());
        }
    }

    private void buildSetter(StringBuilder builder, Function<SqlQuery, String> function) {
        if (this.setters.size() == 1) {
            this.setters.forEach(set -> builder.append(function.apply(set)));
        } else {
            // Remove other "SET"
            Setter first = this.setters.get(0);
            builder.append(function.apply(first)).append(",")
                    .append(
                            this.setters.subList(1, setters.size())
                                .stream()
                                .map(function)
                                .map(s -> s.replaceFirst("SET",""))
                                .collect(Collectors.joining(","))
                    );
        }
    }

    @Override
    public String getSql() {
        StringBuilder builder = new StringBuilder();
        builder.append("UPDATE ").append(table).append(" ");
        buildSetter(builder, SqlQuery::getSql);
        buildParts(builder, SqlQuery::getSql);
        return builder.toString();
    }

    @Override
    public String getDebugSql() {
        StringBuilder builder = new StringBuilder();
        builder.append("UPDATE ").append(table).append(" ");
        buildSetter(builder, SqlQuery::getDebugSql);
        buildParts(builder, SqlQuery::getDebugSql);
        return builder.toString();
    }
}
