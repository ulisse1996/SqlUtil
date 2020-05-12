package it.donatoleone.sqlutil.impl;

import it.donatoleone.sqlutil.interfaces.*;
import it.donatoleone.sqlutil.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

abstract class WhereFilterBuilder<T> implements WhereFilter<T> {

    private static final String WHERE_STR = " WHERE";
    protected final List<Where<T>> wheres;
    protected final List<CompoundWhere> compoundWheres;

    abstract T getReturn();

    WhereFilterBuilder() {
        this.wheres = new ArrayList<>();
        this.compoundWheres = new ArrayList<>();
    }

    @Override
    public Where<T> where(String column) {
        Where<T> where = StatementFactory.buildWhere(column, getReturn());
        this.wheres.add(where);
        return where;
    }

    @Override
    public T whereExists(From subQuery) {
        Where<T> where = StatementFactory.buildWhere(getReturn());
        where.exists(subQuery);
        this.wheres.add(where);
        return getReturn();
    }

    @Override
    public T orWhereExists(From subQuery) {
        Where<T> where = StatementFactory.buildOrWhere(getReturn());
        where.exists(subQuery);
        this.wheres.add(where);
        return getReturn();
    }

    @Override
    public Where<T> orWhere(String column) {
        Where<T> where = StatementFactory.buildOrWhere(column, getReturn());
        this.wheres.add(where);
        return where;
    }

    @Override
    public T where(CompoundWhere where) {
        where.setOr(false);
        this.compoundWheres.add(where);
        return getReturn();
    }

    @Override
    public T orWhere(CompoundWhere where) {
        where.setOr(true);
        this.compoundWheres.add(where);
        return getReturn();
    }

    public void extractCompound(StringBuilder builder, Function<SqlQuery, String> function,
                                       List<CompoundWhere> compoundWheres,
                                       boolean whereEmpty) {
        if (!whereEmpty) {
            buildInside(builder, function, compoundWheres);
        } else {
            compoundWheres.forEach(compoundWhere -> {
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

    protected void extractWheres(StringBuilder builder, Function<SqlQuery,String> function, List<Where<T>> wheres) {
        if (wheres.size() != 1) {
            builder.append(WHERE_STR).append(
                    function.apply(wheres.get(0))
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

    protected List<Object> extractParams(List<Where<T>> wheres, List<CompoundWhere> compoundWheres) {
        Stream<Object> whereValues = wheres.stream()
                .map(Where::getParams)
                .flatMap(Collection::stream);

        // Compound Where
        Stream<Object> compoundValues = compoundWheres.stream()
                .map(CompoundWhere::getParams)
                .flatMap(Collection::stream);

        return Stream.concat(whereValues, compoundValues)
                .collect(Collectors.toList());
    }
}
