package it.donatoleone.sqlutil.interfaces;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public interface LimitedInsert extends SqlQuery{

    void execute(DataSource dataSource) throws SQLException;
    void execute(Connection connection) throws SQLException;

}
