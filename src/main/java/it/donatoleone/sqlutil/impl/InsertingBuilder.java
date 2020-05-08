package it.donatoleone.sqlutil.impl;

import it.donatoleone.sqlutil.interfaces.From;
import it.donatoleone.sqlutil.interfaces.Insert;
import it.donatoleone.sqlutil.interfaces.InsertingValue;
import it.donatoleone.sqlutil.util.MessageFactory;
import it.donatoleone.sqlutil.util.StringUtils;

import java.util.Objects;

final class InsertingBuilder implements InsertingValue {

    private final Insert parent;
    private final String column;
    private Object value;
    private String expression;
    private From query;

    InsertingBuilder(String column, Insert parent) {
        this.column = Objects.requireNonNull(column, MessageFactory.notNull("column"));
        this.parent = parent;
    }

    @Override
    public Insert withValue(Object value) {
        this.value = Objects.requireNonNull(value, MessageFactory.notNull("value"));
        return this.parent;
    }

    @Override
    public Insert withExpression(String expression) {
        this.expression = Objects.requireNonNull(expression, MessageFactory.notNull("expression"));
        return this.parent;
    }

    @Override
    public Insert withResult(From query) {
        this.query = Objects.requireNonNull(query, MessageFactory.notNull("subQuery"));
        return this.parent;
    }

    @Override
    public String getColumn() {
        return this.column;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public String getSql() {
        if (value != null) {
            return "?";
        }

        if (expression != null) {
            return expression;
        }

        return query.getSql();
    }

    @Override
    public String getDebugSql() {
        if (value != null) {
            return StringUtils.asString(value);
        }

        if (expression != null) {
            return expression;
        }

        return query.getDebugSql();
    }
}
