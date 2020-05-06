package it.donatoleone.sqlutil.util;

import it.donatoleone.sqlutil.exceptions.SQLRuntimeException;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class QueryRunner {

    private QueryRunner() {}

    public static Map<String,Object> select(String sql, DataSource datasource, Set<String> columns, List<Object> params)
            throws SQLException {
        try (Connection connection = datasource.getConnection()) {
            return select(sql, connection, columns, params);
        }
    }

    public static Map<String,Object> select(String sql, Connection connection, Set<String> columns, List<Object> params)
            throws SQLException {
        ResultSet rs = null;
        try (PreparedStatement pr = connection.prepareStatement(sql)) {
            initParams(pr, params);
            rs = pr.executeQuery();
            if (rs.next()) {
                return mapValue(rs, columns);
            } else {
                return new HashMap<>();
            }
        } catch (SQLRuntimeException ex) {
            throw (SQLException) ex.getCause();
        } finally {
            silentClose(rs);
        }
    }

    public static List<Map<String,Object>> selectAll(String sql, DataSource datasource, Set<String> columns, List<Object> params)
            throws SQLException {
        try (Connection connection = datasource.getConnection()) {
            return selectAll(sql, connection, columns, params);
        }
    }

    public static List<Map<String,Object>> selectAll(String sql, Connection connection, Set<String> columns, List<Object> params)
            throws SQLException {
        ResultSet rs = null;
        List<Map<String,Object>> values = new ArrayList<>();
        try (PreparedStatement pr = connection.prepareStatement(sql)) {
            initParams(pr, params);
            rs = pr.executeQuery();
            while (rs.next()) {
                values.add(mapValue(rs, columns));
            }
            return values;
        } catch (SQLRuntimeException ex) {
            throw (SQLException) ex.getCause();
        } finally {
            silentClose(rs);
        }
    }

    public static Optional<Map<String,Object>> optSelect(String sql, DataSource dataSource, Set<String> column,
                                                         List<Object> params) throws SQLException {
        return fromMap(select(sql, dataSource, column, params));
    }

    public static Optional<Map<String,Object>> optSelect(String sql, Connection connection, Set<String> column,
                                                         List<Object> params) throws SQLException {
        return fromMap(select(sql, connection, column, params));
    }

    private static Optional<Map<String,Object>> fromMap(Map<String,Object> values) {
        if (values.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(values);
    }

    private static Map<String,Object> mapValue(ResultSet rs, Set<String> columns) throws SQLException {
        if (columns.isEmpty()) {
            return mapAllValue(rs);
        } else {
            Map<String,Object> values = new HashMap<>();
            Map<String, JDBCType> resultColumns = getMetaData(rs);
            for (String column : columns) {
                JDBCType type = resultColumns.get(column.toUpperCase());
                values.put(column, decode(column, type, rs));
            }
            return values;
        }
    }

    private static Map<String, JDBCType> getMetaData(ResultSet rs) throws SQLException {
        return IntStream.rangeClosed(1, rs.getMetaData().getColumnCount())
                .boxed()
                .collect(Collectors.toMap(i -> {
                            try {
                                String name =  rs.getMetaData().getColumnName(i);
                                String label = rs.getMetaData().getColumnLabel(i);

                                // If is alias , return alias name
                                if (!name.equals(label)) {
                                    return label;
                                }

                                return name;
                            } catch (SQLException ex) {
                                throw new SQLRuntimeException(ex);
                            }
                        },
                        i -> {
                            try {
                                return JDBCType.valueOf(rs.getMetaData().getColumnType(i));
                            } catch (SQLException ex) {
                                throw new SQLRuntimeException(ex);
                            }
                        }));
    }

    private static Map<String,Object> mapAllValue(ResultSet rs) throws SQLException {
        Map<String, Object> values = new HashMap<>();
        int columnCount = rs.getMetaData().getColumnCount();
        int i = 1;
        while (i <= columnCount) {
            String name = rs.getMetaData().getColumnName(i);
            JDBCType type = JDBCType.valueOf(rs.getMetaData().getColumnType(i));
            values.put(name, decode(name, type, rs));
            i++;
        }
        return values;
    }

    private static Object decode(String name, JDBCType type, ResultSet rs) throws SQLException {

        switch (type) {
            case BIT:
            case TINYINT:
            case SMALLINT:
            case INTEGER:
            case BIGINT:
            case FLOAT:
            case REAL:
            case DOUBLE:
            case NUMERIC:
            case DECIMAL:
                return rs.getBigDecimal(name);
            case CHAR:
            case VARCHAR:
            case LONGVARCHAR:
            case NCHAR:
            case NVARCHAR:
            case LONGNVARCHAR:
            case CLOB:
            case NCLOB:
                return rs.getString(name);
            case DATE:
                return rs.getDate(name);
            case TIME:
            case TIME_WITH_TIMEZONE:
                return rs.getTime(name);
            case TIMESTAMP:
            case TIMESTAMP_WITH_TIMEZONE:
                return rs.getTimestamp(name);
            case BINARY:
            case VARBINARY:
            case LONGVARBINARY:
            case BLOB:
                return rs.getBytes(name);
            case BOOLEAN:
                return rs.getBoolean(name);
            case SQLXML:
                return rs.getSQLXML(name);
            case JAVA_OBJECT:
            case DISTINCT:
            case STRUCT:
            case ARRAY:
            case REF:
            case DATALINK:
            case ROWID:
            case REF_CURSOR:
                throw new IllegalArgumentException("Not implemented");
            default:
                throw new IllegalArgumentException("Can't map value");
        }
    }

    private static void initParams(PreparedStatement pr, List<Object> params) {
        IntStream.range(0, params.size())
                .forEach(i -> {
                    try {
                        setParam(i + 1, pr, params.get(i));
                    } catch (SQLException ex) {
                        throw new SQLRuntimeException(ex);
                    }
                });
    }

    private static void setParam(int index, PreparedStatement pr, Object p) throws SQLException {
        Class<?> paramKlass = p.getClass();
        if (isWrapper(paramKlass)) {
            setPrimitive(index , pr, p);
        } else if (String.class.isAssignableFrom(paramKlass)) {
            pr.setString(index, (String) p);
        } else if (Date.class.isAssignableFrom(paramKlass)) {
            pr.setDate(index, (Date) p);
        } else if (Timestamp.class.isAssignableFrom(paramKlass)) {
            pr.setTimestamp(index, (Timestamp) p);
        } else if (Number.class.isAssignableFrom(paramKlass)) {
            setNumber(index, pr, p);
        }
    }

    private static boolean isWrapper(Class<?> klass) {
        return Integer.class.isAssignableFrom(klass) ||
                Long.class.isAssignableFrom(klass) ||
                Float.class.isAssignableFrom(klass) ||
                Double.class.isAssignableFrom(klass) ||
                Byte.class.isAssignableFrom(klass) ||
                Boolean.class.isAssignableFrom(klass) ||
                Character.class.isAssignableFrom(klass) ||
                Short.class.isAssignableFrom(klass);
    }

    private static void setPrimitive(int index, PreparedStatement pr, Object p) throws SQLException {
        Class<?> paramKlass = p.getClass();
        if (Integer.class.isAssignableFrom(paramKlass)) {
            pr.setInt(index, (int) p);
        } else if (Long.class.isAssignableFrom(paramKlass)) {
            pr.setLong(index, (long) p);
        } else if (Float.class.isAssignableFrom(paramKlass)) {
            pr.setFloat(index, (float) p);
        } else if (Double.class.isAssignableFrom(paramKlass)) {
            pr.setDouble(index, (double) p);
        } else if (Byte.class.isAssignableFrom(paramKlass)) {
            pr.setByte(index, (byte) p);
        } else if (Boolean.class.isAssignableFrom(paramKlass)) {
            pr.setBoolean(index, (boolean) p);
        } else if (Character.class.isAssignableFrom(paramKlass)) {
            pr.setString(index, new String(new char[] {(char) p}));
        } else if (Short.class.isAssignableFrom(paramKlass)) {
            pr.setShort(index, (short) p);
        }
    }

    private static void setNumber(int index, PreparedStatement pr, Object p) throws SQLException {
        Class<?> paramKlass = p.getClass();
        if (BigDecimal.class.isAssignableFrom(paramKlass)) {
            pr.setBigDecimal(index, (BigDecimal) p);
        } else if (BigInteger.class.isAssignableFrom(paramKlass)) {
            pr.setLong(index, ((BigInteger) p).longValue());
        }
    }

    private static void silentClose(AutoCloseable... closeables) {
        Arrays.stream(closeables)
                .forEach(c -> {
                    try {
                        c.close();
                    } catch (Exception ignored) {
                        // Ignored
                    }
                });
    }

    public static Stream<Map<String, Object>> stream(String sql, DataSource dataSource, Set<String> columns,
                                                     List<Object> params) throws SQLException {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            return stream(sql, connection, columns, params, true);
        } catch (SQLException ex) {
            silentClose(connection);
            throw ex;
        }
    }

    public static Stream<Map<String, Object>> stream(String sql, Connection connection, Set<String> columns,
                                                     List<Object> params, boolean fromDatasource) throws SQLException {

        WrappedCloseables wrappedCloseables = WrappedCloseables.EMPTY;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            initParams(preparedStatement, params);
            resultSet = preparedStatement.executeQuery();

            if (fromDatasource) {
                wrappedCloseables = new WrappedCloseables(connection, preparedStatement, resultSet);
            } else {
                wrappedCloseables = new WrappedCloseables(preparedStatement, resultSet);
            }
            WrappedCloseables finalWrappedCloseables = wrappedCloseables;
            ResultSet finalResultSet = resultSet;
            return StreamSupport.stream(new Spliterators.AbstractSpliterator<Map<String,Object>>(
                    Long.MAX_VALUE, Spliterator.ORDERED) {
                @Override
                public boolean tryAdvance(Consumer<? super Map<String,Object>> action) {
                    try {
                        if (finalResultSet.next()) {
                            action.accept(mapValue(finalResultSet, columns));
                            return true;
                        }

                        return false;
                    } catch (SQLException ex) {
                        finalWrappedCloseables.close();
                        throw new SQLRuntimeException(ex);
                    }
                }
            }, false).onClose(wrappedCloseables::close);
        } catch (SQLException ex) {
            wrappedCloseables.close();
            silentClose(resultSet, preparedStatement);
            throw ex;
        }
    }
    
    private static class WrappedCloseables {
        
        private final AutoCloseable[] closeables;
        private static final WrappedCloseables EMPTY = new WrappedCloseables();
        
        private WrappedCloseables(AutoCloseable... autoCloseables) {
            this.closeables = autoCloseables;
        }

        public void close() {
            silentClose(closeables);
        }
    }
}
