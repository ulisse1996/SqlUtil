package it.donatoleone.sqlutil.impl;

import it.donatoleone.sqlutil.interfaces.select.From;
import it.donatoleone.sqlutil.interfaces.update.Setter;
import it.donatoleone.sqlutil.interfaces.update.Update;
import it.donatoleone.sqlutil.util.MessageFactory;
import it.donatoleone.sqlutil.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

final class SetterBuilder implements Setter {

    public static final String SET_FORMAT = "SET %s = %s";
    private final String column;
    private final Update parent;
    private Object value;
    private String expression;
    private From query;

    SetterBuilder(String column, Update parent) {
        this.column = Objects.requireNonNull(column, MessageFactory.notNull("column"));
        this.parent = parent;
    }

    @Override
    public Update withValue(Object value) {
        this.value = value;
        return this.parent;
    }

    @Override
    public Update withExpression(String expression) {
        this.expression = expression;
        return this.parent;
    }

    @Override
    public Update withResult(From query) {
        this.query = query;
        return this.parent;
    }

    @Override
    public List<Object> getParams() {
        if (value != null) {
            return Collections.singletonList(value);
        }

        if (query != null) {
            return query.getParams();
        }

        return Collections.emptyList();
    }

    @Override
    public String getSql() {
        if (value != null) {
            return String.format("SET %s = ?", column);
        }

        if (expression != null) {
            return String.format(SET_FORMAT, column, expression);
        }

        return String.format("SET %s = (%s)", column, query.getSql());
    }

    @Override
    public String getDebugSql() {
        if (value != null) {
            return String.format(SET_FORMAT, column, StringUtils.asString(value));
        }

        if (expression != null) {
            return String.format(SET_FORMAT, column, expression);
        }

        return String.format("SET %s = (%s)", column, query.getDebugSql());
    }
}
