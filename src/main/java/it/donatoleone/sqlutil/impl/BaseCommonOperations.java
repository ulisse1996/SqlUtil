package it.donatoleone.sqlutil.impl;

import it.donatoleone.sqlutil.enums.LikeMatcher;
import it.donatoleone.sqlutil.enums.OperationType;
import it.donatoleone.sqlutil.interfaces.common.CommonOperations;
import it.donatoleone.sqlutil.interfaces.common.LimitedWhere;
import it.donatoleone.sqlutil.interfaces.select.From;
import it.donatoleone.sqlutil.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Base class for {@link CommonOperations} builder implementation
 *
 * @see BaseWhere
 * @see BaseOn
 * @param <N> return type of operation
 */
abstract class BaseCommonOperations<N> implements CommonOperations<N> {

    private static final String LIKE_FORMAT = "%s %s LIKE '%s'";
    protected String column;
    protected Object value;
    protected List<Object> values;
    protected OperationType operation;
    protected LikeMatcher matcher;
    protected boolean or;

    /**
     * Get return element after an operation build
     *
     * @see From
     * @see LimitedWhere
     * @return return Type
     */
    abstract N getReturn();

    @Override
    public N isEqualsTo(Object value) {
        this.value = value;
        this.operation = OperationType.EQUALS;
        return getReturn();
    }

    @Override
    public N isNotEqualsTo(Object value) {
        this.value = value;
        this.operation = OperationType.NOT_EQUALS;
        return getReturn();
    }

    @Override
    public N isLessThan(Object value) {
        this.value = value;
        this.operation = OperationType.LESS;
        return getReturn();
    }

    @Override
    public N isGreaterThan(Object value) {
        this.value = value;
        this.operation = OperationType.GREATER;
        return getReturn();
    }

    @Override
    public N isGreaterOrEqualsTo(Object value) {
        this.value = value;
        this.operation = OperationType.GREATER_EQUALS;
        return getReturn();
    }

    @Override
    public N isLessOrEqualsTo(Object value) {
        this.value = value;
        this.operation = OperationType.LESS_EQUALS;
        return getReturn();
    }

    @Override
    public <T> N between(T val1, T val2) {
        this.values = Arrays.asList(val1, val2);
        this.operation = OperationType.BETWEEN;
        return getReturn();
    }

    @Override
    public <T> N in(Iterable<T> values) {
        this.values = new ArrayList<>();
        values.spliterator().forEachRemaining(this.values::add);
        this.values = Collections.unmodifiableList(this.values);
        this.operation = OperationType.IN;
        return getReturn();
    }

    @Override
    public N like(String value, LikeMatcher matcher) {
        this.value = asLike(value, matcher);
        this.operation = OperationType.LIKE;
        this.matcher = matcher;
        return getReturn();
    }

    private String asLike(String value, LikeMatcher matcher) {
        switch (matcher) {

            case FULL_MATCH:
                return "%"+value+"%";
            case START_MATCH:
                return "%"+value;
            case END_MATCH:
                return value+"%";
            default: throw new IllegalArgumentException("Can't find likeMather match");
        }
    }

    protected List<Object> getParamsList() {
        if (value != null) {
            return Collections.singletonList(value);
        }
        return this.values;
    }

    @Override
    public String getSql() {
        if (operation.isSimple()) {
            return String.format("%s %s %s %s", decodeWhereType(), this.column, operation.stringValue(), "?");
        }

        return getOperationMapping(false);
    }

    @Override
    public String getDebugSql() {
        if (operation.isSimple()) {
            return String.format("%s %s %s %s", decodeWhereType(), this.column, operation.stringValue(),
                    StringUtils.asString(this.value));
        }

        return getOperationMapping(true);
    }

    protected String decodeWhereType() {
        return or ? "OR" : "AND";
    }

    private String getOperationMapping(boolean debug) {
        switch (this.operation) {

            case BETWEEN:
                if (debug) {
                    return String.format("%s %s BETWEEN %s AND %s", decodeWhereType(), this.column, StringUtils.asString(this.values.get(0)),
                            StringUtils.asString(this.values.get(1)));
                } else {
                    return String.format("%s %s BETWEEN ? AND ?", decodeWhereType(), this.column);
                }
            case IN:
                if (debug) {
                    return String.format("%s %s IN (%s)", decodeWhereType(), this.column,
                            this.values.stream()
                                    .map(StringUtils::asString)
                                    .collect(Collectors.joining(",")));
                } else {
                    return String.format("%s %s IN (%s)", decodeWhereType(), this.column, String.join(",",
                            Collections.nCopies(this.values.size(), "?")));
                }
            case LIKE:
                return likeMatcher(debug);
            default: throw new IllegalArgumentException("Can't find Operation Type");
        }
    }

    private String likeMatcher(boolean debug) {
        if (debug) {
            return String.format(LIKE_FORMAT, decodeWhereType(), this.column, this.value);
        } else {
            switch (this.matcher) {
                case FULL_MATCH:
                    return String.format(LIKE_FORMAT, decodeWhereType(), this.column, "%?%");
                case START_MATCH:
                    return String.format(LIKE_FORMAT, decodeWhereType(), this.column, "%?");
                case END_MATCH:
                    return String.format(LIKE_FORMAT, decodeWhereType(), this.column, "?%");
                default: throw new IllegalArgumentException("Can't find Matcher Type");
            }
        }
    }
}
