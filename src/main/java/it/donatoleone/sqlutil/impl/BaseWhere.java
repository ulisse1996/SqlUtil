package it.donatoleone.sqlutil.impl;

import it.donatoleone.sqlutil.enums.OperationType;
import it.donatoleone.sqlutil.interfaces.From;
import it.donatoleone.sqlutil.interfaces.Where;
import it.donatoleone.sqlutil.util.MessageFactory;

import java.util.Objects;

/**
 * Extended Implementation of {@link it.donatoleone.sqlutil.interfaces.CommonOperations} used
 * on {@link Where} cause
 * @param <N>
 */
abstract class BaseWhere<N> extends BaseCommonOperations<N> implements Where<N> {

    protected From subQuery;

    BaseWhere(String column, From from, boolean or) {
        this(column, or);
        this.from = from;
    }

    BaseWhere(String column, boolean or) {
        this.column = Objects.requireNonNull(column, MessageFactory.notNull("column"));
        this.or = or;
    }

    @Override
    public void exists(From subQuery) {
        this.subQuery = subQuery;
        this.operation = OperationType.EXISTS;
    }

    @Override
    public String getSql() {
        if (operation.equals(OperationType.EXISTS)) {
            return extractExists(false);
        }
        return super.getSql();
    }

    @Override
    public String getDebugSql() {
        if (operation.equals(OperationType.EXISTS)) {
            return extractExists(true);
        }
        return super.getDebugSql();
    }

    private String extractExists(boolean debug) {
        if (debug) {
            return String.format("%s EXISTS (%s)", decodeWhereType(),
                    this.subQuery.getDebugSql());
        } else {
            return String.format("%s EXISTS (%s)", decodeWhereType(),
                    this.subQuery.getSql());
        }
    }
}
