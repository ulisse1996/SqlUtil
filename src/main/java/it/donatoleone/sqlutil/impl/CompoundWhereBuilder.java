package it.donatoleone.sqlutil.impl;

import it.donatoleone.sqlutil.interfaces.CompoundWhere;
import it.donatoleone.sqlutil.interfaces.LimitedWhere;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Create a {@link CompoundWhere} that group a set of {@link it.donatoleone.sqlutil.interfaces.Where} cause
 */
final class CompoundWhereBuilder implements CompoundWhere {

    private boolean or;
    private final List<LimitedWhere> wheres;

    public CompoundWhereBuilder(LimitedWhere[] wheres) {
        if (wheres == null || wheres.length == 0) {
            throw new IllegalArgumentException("Can't use empty compound where !");
        }
        this.wheres = Arrays.asList(wheres);
    }

    @Override
    public String getSql() {
        return String.format("%s (%s)",decodeWhereType(),
                this.wheres.stream().map(LimitedWhere::getSql).collect(Collectors.joining(" ")));
    }

    private String decodeWhereType() {
        return or ? "OR" : "AND";
    }

    @Override
    public String getDebugSql() {
        return String.format("%s (%s)",decodeWhereType(),
                this.wheres.stream().map(LimitedWhere::getDebugSql).collect(Collectors.joining(" ")));
    }

    @Override
    public void setOr(boolean or) {
        this.or = or;
    }

    @Override
    public List<Object> getParams() {
        return this.wheres.stream()
                .map(LimitedWhere::getParams)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}