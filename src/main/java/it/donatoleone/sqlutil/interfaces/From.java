package it.donatoleone.sqlutil.interfaces;

import it.donatoleone.sqlutil.enums.JoinType;
import it.donatoleone.sqlutil.enums.Ordering;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public interface From extends SqlQuery {

    // Query Methods

    Where<From> where(String column);
    From whereExists(From subQuery);
    From orWhereExists(From subQuery);
    From withId(String tableId);
    Where<From> orWhere(String column);
    From where(CompoundWhere where);
    From orWhere(CompoundWhere where);
    Join join(JoinType joinType, String table);
    From orderBy(Ordering ordering, String column);
    From orderBy(Ordering ordering, String... columns);

    // Execute Methods

    Map<String,Object> readSingle(DataSource dataSource) throws SQLException;
    Map<String,Object> readSingle(Connection connection) throws SQLException;
    Optional<Map<String,Object>> readOptionalSingle(DataSource dataSource) throws SQLException;
    Optional<Map<String,Object>> readOptionalSingle(Connection connection) throws SQLException;
    List<Map<String,Object>> readAll(DataSource dataSource) throws SQLException;
    List<Map<String,Object>> readAll(Connection connection) throws SQLException;
    Stream<Map<String,Object>> stream(DataSource dataSource) throws SQLException;
    Stream<Map<String,Object>> stream(Connection connection) throws SQLException;
}
