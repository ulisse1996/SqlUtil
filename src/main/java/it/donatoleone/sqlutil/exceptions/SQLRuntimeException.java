package it.donatoleone.sqlutil.exceptions;

import java.sql.SQLException;

public class SQLRuntimeException extends RuntimeException {

    public SQLRuntimeException(SQLException ex) {
        super(ex);
    }
}
