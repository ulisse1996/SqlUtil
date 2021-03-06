package it.donatoleone.sqlutil.exceptions;

import java.sql.SQLException;

/**
 * Wrap a simple {@link SQLException} in a {@link RuntimeException}
 */
public class SQLRuntimeException extends RuntimeException {

    public SQLRuntimeException(SQLException ex) {
        super(ex);
    }

    public SQLRuntimeException(Throwable ex) {
        if (ex instanceof SQLRuntimeException) {
            this.initCause(ex.getCause());
        }
    }
}
