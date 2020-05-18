package it.donatoleone.sqlutil.impl;

import it.donatoleone.sqlutil.enums.JoinType;
import it.donatoleone.sqlutil.interfaces.*;
import it.donatoleone.sqlutil.interfaces.select.CompoundOn;
import it.donatoleone.sqlutil.interfaces.select.From;
import it.donatoleone.sqlutil.interfaces.select.Join;
import it.donatoleone.sqlutil.interfaces.select.On;

import java.util.List;
import java.util.function.Function;

final class JoinBuilder implements Join {

    private final JoinType joinType;
    private final String table;
    private final From from;
    private On<From> singleOn;
    private CompoundOn compoundOn;

    JoinBuilder(JoinType joinType, From from, String table) {
        this.joinType = joinType;
        this.from = from;
        this.table = table;
    }

    @Override
    public On<From> on(String column) {
        this.singleOn = StatementFactory.buildOn(column, this.from);
        return this.singleOn;
    }

    @Override
    public From on(CompoundOn compoundOn) {
        this.compoundOn = compoundOn;
        return this.from;
    }

    @Override
    public String getSql() {
        return doSqlBuild(SqlDefinition::getSql);
    }

    private String doSqlBuild(Function<SqlDefinition, String> function) {
        StringBuilder builder = new StringBuilder();
        builder.append(joinType.getValue())
                .append(" ")
                .append(this.table)
                .append(" ON");
        if (singleOn != null) {
            builder.append(function.apply(this.singleOn));
        } else {
            builder.append(function.apply(this.compoundOn));
        }
        return builder.toString();
    }

    @Override
    public String getDebugSql() {
        return doSqlBuild(SqlDefinition::getDebugSql);
    }

    @Override
    public List<Object> getParams() {
        if (singleOn != null) {
            return this.singleOn.getParams();
        }

        return this.compoundOn.getParams();
    }
}
