package it.donatoleone.sqlutil.interfaces.update;

import it.donatoleone.sqlutil.interfaces.SqlDefinition;
import it.donatoleone.sqlutil.interfaces.common.WhereFilter;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public interface Update extends SqlDefinition, WhereFilter<Update> {

    Setter set(String column);

    void execute(DataSource dataSource) throws SQLException;
    void execute(Connection connection) throws SQLException;
}
