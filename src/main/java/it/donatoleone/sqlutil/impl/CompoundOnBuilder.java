package it.donatoleone.sqlutil.impl;

import it.donatoleone.sqlutil.interfaces.CompoundOn;
import it.donatoleone.sqlutil.interfaces.LimitedOn;
import it.donatoleone.sqlutil.interfaces.SqlQuery;
import it.donatoleone.sqlutil.util.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

final class CompoundOnBuilder implements CompoundOn {

    private final List<LimitedOn> ons;

    CompoundOnBuilder(LimitedOn[] ons) {
        if (ons == null || ons.length == 0) {
            throw new IllegalArgumentException("Can't use empty compound on !");
        }
        this.ons = Arrays.asList(ons);
    }

    @Override
    public String getSql() {
        return doSqlBuild(SqlQuery::getSql);
    }

    @Override
    public String getDebugSql() {
        return doSqlBuild(SqlQuery::getDebugSql);
    }

    private String doSqlBuild(Function<SqlQuery, String> function) {
        if (ons.size() == 1) {
            return StringUtils.replaceSingle(function.apply(ons.get(0)));
        } else {
            LimitedOn on = ons.get(0);
            String firstOn = StringUtils.replaceSingle(function.apply(on));
            List<String> strings = ons.subList(1, ons.size())
                    .stream()
                    .map(function)
                    .map(String::trim)
                    .collect(Collectors.toList());
            strings.add(0, firstOn);
            return " (" + String.join(" ", strings) + ")";
        }
    }

    @Override
    public List<Object> getParams() {
        return this.ons.stream()
                .map(LimitedOn::getParams)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
