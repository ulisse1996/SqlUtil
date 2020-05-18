package it.donatoleone.sqlutil.interfaces.insert;

import it.donatoleone.sqlutil.interfaces.SqlDefinition;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public interface LimitedInsert extends SqlDefinition {

    void execute(DataSource dataSource) throws SQLException;
    void execute(Connection connection) throws SQLException;

}
