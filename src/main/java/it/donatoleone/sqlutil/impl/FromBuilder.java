package it.donatoleone.sqlutil.impl;

import it.donatoleone.sqlutil.enums.JoinType;
import it.donatoleone.sqlutil.interfaces.*;
import it.donatoleone.sqlutil.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

final class FromBuilder implements From {

    private static final String WHERE_STR = " WHERE";
    private final String table;
    private final Select parent;
    private final List<Where<From>> wheres;
    private final List<CompoundWhere> compoundWheres;
    private final List<Join> joins;
    private String tableId;

    FromBuilder(String table, Select select) {
        this.parent = select;
        this.table = table.toUpperCase();
        this.wheres = new ArrayList<>();
        this.compoundWheres = new ArrayList<>();
        this.joins = new ArrayList<>();
    }

    @Override
    public String getSql() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.join(" ", parent.getSql(), "FROM " + table ))
            .append(this.tableId != null ? " "+tableId : "");
        buildParts(builder, SqlQuery::getSql);
        return builder.toString();
    }

    @Override
    public String getDebugSql() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.join(" ", parent.getDebugSql(), "FROM " + table))
                .append(this.tableId != null ? " "+tableId : "");
        buildParts(builder, SqlQuery::getDebugSql);
        return builder.toString();
    }

    private void buildParts(StringBuilder builder, Function<SqlQuery, String> function) {
        if (!this.joins.isEmpty()) {
            extractJoins(builder, function);
        }
        if (!this.wheres.isEmpty()) {
            extractWheres(builder, function);
        }
        if (!this.compoundWheres.isEmpty()) {
            extractCompound(builder, function, this.wheres.isEmpty());
        }
    }

    private void extractJoins(StringBuilder builder, Function<SqlQuery, String> function) {
        this.joins.forEach(join -> {
            builder.append(" ");
            builder.append(
                    StringUtils.replaceSingle(function.apply(join)));
        });

    }

    private void extractCompound(StringBuilder builder, Function<SqlQuery, String> function, boolean whereEmpty) {
        if (!whereEmpty) {
            buildInside(builder, function, this.compoundWheres);
        } else {
            this.compoundWheres.forEach(compoundWhere -> {
                builder.append(WHERE_STR);
                String sql = StringUtils.replaceFirstBeforeParenthesis(function.apply(compoundWhere));
                builder.append(
                        StringUtils.replaceSingle(sql)
                );
            });
        }
    }

    private void buildInside(StringBuilder builder, Function<SqlQuery, String> function,
                             List<CompoundWhere> wheres) {
        for (CompoundWhere where : wheres) {
            String sql = function.apply(where);
            builder.append(" ");
            builder.append(
                    StringUtils.replaceFirstAfterParenthesis(sql)
            );
        }
    }

    private void extractWheres(StringBuilder builder, Function<SqlQuery,String> function) {
        if (this.wheres.size() != 1) {
            builder.append(WHERE_STR).append(
                    function.apply(this.wheres.get(0))
                            .replaceFirst("AND", "")
                            .replaceFirst("OR", "")
            ).append(" ");
            builder.append(wheres.subList(1, wheres.size())
                    .stream()
                    .map(function)
                    .collect(Collectors.joining(" ")));
        } else {
            builder.append(WHERE_STR).append(
                    function.apply(wheres.get(0))
                            .replaceFirst("AND", "")
                            .replaceFirst("OR", "")
            );
        }
    }

    @Override
    public Where<From> where(String column) {
        Where<From> where = StatementFactory.buildWhere(column, this);
        this.wheres.add(where);
        return where;
    }

    @Override
    public From whereExists(From subQuery) {
        Where<From> where = StatementFactory.buildWhere(this);
        where.exists(subQuery);
        this.wheres.add(where);
        return this;
    }

    @Override
    public From orWhereExists(From subQuery) {
        Where<From> where = StatementFactory.buildOrWhere(this);
        where.exists(subQuery);
        this.wheres.add(where);
        return this;
    }

    @Override
    public From withId(String tableId) {
        this.tableId = tableId;
        return this;
    }

    @Override
    public Where<From> orWhere(String column) {
        Where<From> where = StatementFactory.buildOrWhere(column, this);
        this.wheres.add(where);
        return where;
    }

    @Override
    public From where(CompoundWhere where) {
        where.setOr(false);
        this.compoundWheres.add(where);
        return this;
    }

    @Override
    public From orWhere(CompoundWhere where) {
        where.setOr(true);
        this.compoundWheres.add(where);
        return this;
    }

    @Override
    public Join join(JoinType joinType, String table) {
        Join join = StatementFactory.buildJoin(joinType, table, this);
        this.joins.add(join);
        return join;
    }
}
