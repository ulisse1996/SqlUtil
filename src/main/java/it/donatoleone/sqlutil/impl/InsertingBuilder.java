package it.donatoleone.sqlutil.impl;

import it.donatoleone.sqlutil.interfaces.insert.Insert;
import it.donatoleone.sqlutil.interfaces.insert.InsertingValue;
import it.donatoleone.sqlutil.interfaces.insert.LimitedInsert;
import it.donatoleone.sqlutil.interfaces.insert.LimitedInsertingValue;
import it.donatoleone.sqlutil.interfaces.select.From;
import it.donatoleone.sqlutil.util.MessageFactory;
import it.donatoleone.sqlutil.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

final class InsertingBuilder implements InsertingValue, LimitedInsertingValue {

    private final Insert parent;
    private final List<String> columns;
    private Object value;
    private String expression;
    private From query;

    InsertingBuilder(String column, Insert parent) {
        this.columns = Collections.singletonList(
                Objects.requireNonNull(column, MessageFactory.notNull("column")));
        this.parent = parent;
    }

    InsertingBuilder(String[] columns, Insert parent) {
        this.columns = Arrays.asList(columns);
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
    public LimitedInsert withResult(From query) {
        this.query = Objects.requireNonNull(query, MessageFactory.notNull("subQuery"));
        return this.parent;
    }

    @Override
    public List<String> getColumns() {
        return this.columns;
    }

    @Override
    public List<Object> getValue() {
        if (query != null) {
            return query.getParams();
        }
        return value != null ? Collections.singletonList(value) : Collections.emptyList();
    }

    @Override
    public boolean isSelectInsert() {
        return this.query != null;
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
