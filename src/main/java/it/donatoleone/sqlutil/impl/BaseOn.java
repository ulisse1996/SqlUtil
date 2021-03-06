package it.donatoleone.sqlutil.impl;

import it.donatoleone.sqlutil.interfaces.common.CommonOperations;
import it.donatoleone.sqlutil.interfaces.select.On;

import java.util.Collections;
import java.util.List;

/**
 * Extended base implementation of {@link CommonOperations} used
 * for {@link On} cause
 *
 * @see OnBuilder
 * @see LimitedOnBuilder
 * @param <N> return type of operations
 */
abstract class BaseOn<N> extends BaseCommonOperations<N> implements On<N> {

    private boolean columnComparison;
    private String comparedColumn;

    BaseOn(String column) {
        this.column = column;
    }

    BaseOn(String column, boolean or) {
        this.column = column;
        this.or = or;
    }

    @Override
    public N isEqualsToColumn(String column) {
        this.columnComparison = true;
        this.comparedColumn = column;
        return getReturn();
    }

    @Override
    public String getDebugSql() {
        if (this.columnComparison) {
            return getColumnComparison();
        }

        return super.getDebugSql();
    }

    @Override
    public String getSql() {
        if (this.columnComparison) {
            return getColumnComparison();
        }

        return super.getSql();
    }

    @Override
    public List<Object> getParams() {
        if (this.columnComparison) {
            return Collections.emptyList();
        }

        return this.getParamsList();
    }

    private String getColumnComparison() {
        return String.format("%s %s = %s", decodeWhereType(), this.column, this.comparedColumn);
    }
}
