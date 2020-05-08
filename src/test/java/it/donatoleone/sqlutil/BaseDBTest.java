package it.donatoleone.sqlutil;

import it.donatoleone.sqlutil.util.Pair;
import org.hsqldb.jdbc.JDBCDataSourceFactory;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.Mock;
import org.mockito.Mockito;

import javax.sql.DataSource;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Properties;
import java.util.function.Predicate;

public abstract class BaseDBTest {

    protected static DataSource dataSource;

    @BeforeAll
    public static void init() throws Exception {
        Class.forName("org.hsqldb.jdbc.JDBCDriver");
        Properties prop = new Properties();
        prop.put("url", "jdbc:hsqldb:mem:test;sql.syntax_ora=true");
        prop.put("user", "sa");
        prop.put("password", "");
        dataSource = JDBCDataSourceFactory.createDataSource(prop);
        initDb();
    }

    private static void initDb() throws Exception {
        String sql = String.join("", Files.readAllLines(
                Paths.get(BaseDBTest.class.getResource("/test.sql").toURI())));
        Arrays.stream(sql.split(";"))
                .map(String::trim)
                .filter(((Predicate<String>)String::isEmpty).negate())
                .forEach(s -> {
                    System.out.println("Executing " + s.trim());
                    try (Connection cn = dataSource.getConnection();
                         PreparedStatement pr = cn.prepareStatement(s.trim())) {
                        pr.execute();
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                });
    }

    protected Pair<DataSource, ResultSet> mockResult() throws SQLException {
        DataSource dataSource = Mockito.mock(DataSource.class);
        Connection connection = Mockito.mock(Connection.class);
        PreparedStatement preparedStatement = Mockito.mock(PreparedStatement.class);
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(preparedStatement);
        Mockito.when(preparedStatement.executeQuery()).thenReturn(resultSet);
        return Pair.immutable(dataSource, resultSet);
    }
}
