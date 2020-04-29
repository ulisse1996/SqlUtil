package it.donatoleone.sqlutil.impl;

import it.donatoleone.sqlutil.interfaces.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

final class FromBuilder implements From {

    public static final String WHERE_STR = " WHERE";
    private final String table;
    private final Select parent;
    private final List<Where<From>> wheres;
    private final List<CompoundWhere> compoundWheres;

    FromBuilder(String table, Select select) {
        this.parent = select;
        this.table = table.toUpperCase();
        this.wheres = new ArrayList<>();
        this.compoundWheres = new ArrayList<>();
    }

    @Override
    public String getSql() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.join(" ", parent.getSql(), "FROM " + table));
        if (!this.wheres.isEmpty()) {
            extractWheres(builder, Where::getSql);
        }
        if (!this.compoundWheres.isEmpty()) {
            extractCompound(builder, StringSequence::getSql, this.wheres.isEmpty());
        }
        return builder.toString();
    }

    @Override
    public String getDebugSql() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.join(" ", parent.getDebugSql(), "FROM " + table));
        if (!this.wheres.isEmpty()) {
            extractWheres(builder, Where::getDebugSql);
        }
        if (!this.compoundWheres.isEmpty()) {
            extractCompound(builder, StringSequence::getDebugSql, this.wheres.isEmpty());
        }
        return builder.toString();
    }

    private void extractCompound(StringBuilder builder, Function<CompoundWhere, String> function, boolean whereEmpty) {
        if (!whereEmpty) {
            buildInside(builder, function, this.compoundWheres);
        } else {
            this.compoundWheres.forEach(compoundWhere -> {
                builder.append(WHERE_STR);
                String sql = replaceFirstBeforeParenthesis(function.apply(compoundWhere));
                builder.append(
                        replaceSingle(sql)
                );
            });
        }
    }

    private String replaceSingle(String sql) {
        int indexAnd = sql.indexOf("AND");
        int indexOr = sql.indexOf("OR");
        if (indexAnd < indexOr || indexOr == -1) {
            return sql.replaceFirst("AND","")
                    .replace("( ","(");
        } else {
            return sql.replaceFirst("OR","")
                    .replace("( ", "");
        }
    }

    private void buildInside(StringBuilder builder, Function<CompoundWhere, String> function,
                             List<CompoundWhere> wheres) {
        for (CompoundWhere where : wheres) {
            String sql = function.apply(where);
            builder.append(" ");
            builder.append(
                    replaceFirstAfterParenthesis(sql)
            );
        }
    }

    private String replaceFirstAfterParenthesis(String string) {
        String afterParenthesis = string.substring(string.indexOf('('));
        return string.substring(0,string.indexOf('(')) + replaceSingle(afterParenthesis);
    }

    private String replaceFirstBeforeParenthesis(String string) {
        String beforeParenthesis = string.substring(0,string.indexOf('('));
        return beforeParenthesis.replaceFirst("AND","")
                .replaceFirst("OR","") + string.substring(string.indexOf('('));
    }

    private void extractWheres(StringBuilder builder, Function<Where<?>,String> function) {
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
}
