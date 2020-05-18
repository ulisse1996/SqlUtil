package it.donatoleone.sqlutil.interfaces.create;

import it.donatoleone.sqlutil.interfaces.SqlDefinition;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public interface CreateTable extends SqlDefinition {

    ColumnDefinition column(String column);

    void execute(DataSource dataSource) throws SQLException;
    void execute(Connection connection) throws SQLException;
}
