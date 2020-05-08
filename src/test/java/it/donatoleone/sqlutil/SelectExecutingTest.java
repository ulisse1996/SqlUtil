package it.donatoleone.sqlutil;

import it.donatoleone.sqlutil.enums.JoinType;
import it.donatoleone.sqlutil.enums.Ordering;
import it.donatoleone.sqlutil.exceptions.SQLRuntimeException;
import it.donatoleone.sqlutil.impl.AliasFactory;
import it.donatoleone.sqlutil.impl.OnFactory;
import it.donatoleone.sqlutil.impl.SqlUtil;
import it.donatoleone.sqlutil.impl.WhereFactory;
import it.donatoleone.sqlutil.interfaces.ThrowingFunction;
import it.donatoleone.sqlutil.util.Pair;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class SelectExecutingTest extends BaseDBTest {

    private static final ThrowingFunction<ResultSet, CustomObject, SQLException> MAPPER = (rs) ->
        new CustomObject(rs.getLong("COL1"),
                rs.getLong("COL2"),
                rs.getString("COL3"));

    @BeforeAll
    private static void init() throws Exception {
        init("/selectTest.sql");
    }

    @Test
    public void shouldReturnOneCompleteRecord() {
        try (Connection connection = dataSource.getConnection()) {
            Map<String,Object> values = SqlUtil.select()
                    .from("TAB1")
                    .where("COL1").isEqualsTo(2)
                    .readSingle(dataSource);
            assertAll(
                    () -> assertEquals(3, values.size()),
                    () -> assertEquals(BigDecimal.valueOf(2), values.get("COL1")),
                    () -> assertEquals(BigDecimal.valueOf(3), values.get("COL2")),
                    () -> assertEquals("TEST", values.get("COL3"))
            );

            Map<String,Object> values2 = SqlUtil.select()
                    .from("TAB1")
                    .readSingle(connection);
            assertAll(
                    () -> assertEquals(3, values2.size()),
                    () -> assertEquals(BigDecimal.valueOf(2), values2.get("COL1")),
                    () -> assertEquals(BigDecimal.valueOf(3), values2.get("COL2")),
                    () -> assertEquals("TEST", values2.get("COL3"))
            );
        } catch (Exception ex) {
            fail(ex);
        }
    }

    @Test
    public void shouldReturnOneRecordWith2Column() {
        try {
            Map<String,Object> values = SqlUtil.select("COL1", "COL2")
                    .from("TAB1")
                    .readSingle(dataSource);
            assertAll(
                    () -> assertEquals(2, values.size()),
                    () -> assertEquals(BigDecimal.valueOf(2), values.get("COL1")),
                    () -> assertEquals(BigDecimal.valueOf(3), values.get("COL2"))
            );
        } catch (Exception ex) {
            fail(ex);
        }
    }

    @Test
    public void shouldReturnEmptyRecord() {
        try (Connection connection = dataSource.getConnection()) {
            assertFalse(
                    SqlUtil.select()
                        .from("TAB1")
                        .where("COL1").isEqualsTo(10)
                        .readOptionalSingle(dataSource)
                        .isPresent()
            );

            assertFalse(
                    SqlUtil.select()
                            .from("TAB1")
                            .where("COL1").isEqualsTo(10)
                            .readOptionalSingle(connection)
                            .isPresent()
            );
        } catch (Exception ex) {
            fail(ex);
        }
    }

    @Test
    public void shouldReturnRecordWithWhere() {
        try {
            Map<String, Object> values = SqlUtil.select()
                    .from("TAB1")
                    .where("COL1").isEqualsTo(2)
                    .where("COL3").isEqualsTo("TEST")
                    .readSingle(dataSource);
            assertAll(
                    () -> assertEquals(3, values.size()),
                    () -> assertEquals(BigDecimal.valueOf(2), values.get("COL1")),
                    () -> assertEquals(BigDecimal.valueOf(3), values.get("COL2")),
                    () -> assertEquals("TEST", values.get("COL3"))
            );
        } catch (Exception ex) {
            fail(ex);
        }
    }

    @Test
    public void shouldReturnRecordWithIn() {
        try {
            assertFalse(
                    SqlUtil.select()
                        .from("TAB1")
                        .where("COL1").in(Arrays.asList(2,3))
                        .readAll(dataSource)
                        .isEmpty()
            );
        } catch (Exception ex) {
            fail(ex);
        }
    }

    @Test
    public void shouldReturnRecordWithJoin() {
        try {
            Map<String,Object> values = SqlUtil.select("COL1", "COL2", "COL3")
                    .from("TAB1")
                    .join(JoinType.INNER_JOIN, "TAB2")
                    .on("COL1").isEqualsToColumn("COL4")
                    .readSingle(dataSource);
            assertAll(
                    () -> assertEquals(3, values.size()),
                    () -> assertEquals(BigDecimal.valueOf(2), values.get("COL1")),
                    () -> assertEquals(BigDecimal.valueOf(3), values.get("COL2")),
                    () -> assertEquals("TEST", values.get("COL3"))
            );
        } catch (Exception ex) {
            fail(ex);
        }
    }

    @Test
    public void shouldReturnZeroRecordWithCompoundJoin() {
        try {
            assertFalse(
                    SqlUtil.select()
                    .from("TAB1")
                    .join(JoinType.INNER_JOIN, "TAB2")
                    .on(
                            OnFactory.compoundOn(
                                    OnFactory.on("COL1").isEqualsToColumn("COL4"),
                                    OnFactory.on("COL1").isEqualsTo(4)
                            )
                    )
                    .readOptionalSingle(dataSource)
                    .isPresent()
            );
        } catch (Exception ex) {
            fail(ex);
        }
    }

    @Test
    public void shouldReturnRecordWithWhereCompound() {
        try {
            assertTrue(
                    SqlUtil.select()
                    .from("TAB1")
                    .where(
                            WhereFactory.compoundWhere(
                                    WhereFactory.where("COL1").isEqualsTo(2),
                                    WhereFactory.orWhere("COL1").isLessThan(2),
                                    WhereFactory.orWhere("COL1").isLessThan(Short.MIN_VALUE)
                            )
                    )
                    .readOptionalSingle(dataSource)
                    .isPresent()
            );
        } catch (Exception ex) {
            fail(ex);
        }
    }

    @Test
    public void shouldMapParamsAndReturnValues() {
        try {
            Map<String, Object> values = SqlUtil.select()
                    .from("TAB3")
                    .where(
                            WhereFactory.compoundWhere(
                                    WhereFactory.where("COL1").isEqualsTo(2.5),
                                    WhereFactory.orWhere("COL1").isEqualsTo(2.5f)
                            )
                    )
                    .where("COL2").isLessOrEqualsTo(new Timestamp(System.currentTimeMillis()))
                    .where("COL3").isLessOrEqualsTo(new Date(System.currentTimeMillis()))
                    .where("COL4").isEqualsTo('C')
                    .readSingle(dataSource);
            assertAll(
                    () -> assertEquals(4, values.size()),
                    () -> assertEquals(new BigDecimal("2.5"), values.get("COL1")),
                    () -> assertTrue(Timestamp.class.isAssignableFrom(values.get("COL2").getClass())),
                    () -> assertTrue(Timestamp.class.isAssignableFrom(values.get("COL3").getClass())),
                    () -> assertEquals("C", values.get("COL4"))
            );
        } catch (Exception ex) {
            fail(ex);
        }
    }

    @Test
    public void shouldReturnTwoRecords() {
        try (Connection connection = dataSource.getConnection()) {

            List<Map<String, Object>> values = SqlUtil.select()
                    .from("TAB1")
                    .readAll(connection);

            List<Map<String, Object>> values2 = SqlUtil.select()
                    .from("TAB1")
                    .readAll(dataSource);

            assertAll(
                    () -> assertEquals(values, values2),
                    () -> assertEquals(2, values.size())
            );

        } catch (Exception ex) {
            fail(ex);
        }
    }

    @Test
    public void shouldReturnColumnWithAliases() {
        try {
            Map<String, Object> values = SqlUtil.select(AliasFactory.as("COL1", "COLUMN1"))
                    .from("TAB1")
                    .where("COL1").isEqualsTo(2)
                    .readSingle(dataSource);
            assertAll(
                    () -> assertEquals(1, values.size()),
                    () -> assertEquals(BigDecimal.valueOf(2), values.get("COLUMN1"))
            );
        } catch (Exception ex) {
            fail(ex);
        }
    }

    @Test
    public void shouldMapBooleanTimeByte() {
        try {
            Map<String,Object> values = SqlUtil.select()
                    .from("TAB_TYPE")
                    .where("COL3").isEqualsTo(true)
                    .readSingle(dataSource);
            assertAll(
                    () -> assertEquals(3, values.size()),
                    () -> assertTrue(values.get("COL1") instanceof Time),
                    () -> assertTrue(values.get("COL2") instanceof byte[]),
                    () -> assertTrue(values.get("COL3") instanceof Boolean)
            );
        } catch (Exception ex) {
            fail(ex);
        }
    }

    @Test
    public void shouldMapComplexNumberTypeAndReturnSameType() {
        try {
            Map<String, Object> values = SqlUtil.select("COL1")
                    .from("TAB1")
                    .where("COL1").isEqualsTo(BigDecimal.valueOf(2))
                    .readSingle(dataSource);

            Map<String, Object> values2 = SqlUtil.select("COL1")
                    .from("TAB1")
                    .where("COL1").isEqualsTo(2)
                    .readSingle(dataSource);

            Map<String, Object> values3 = SqlUtil.select("COL1")
                    .from("TAB1")
                    .where("COL1").isEqualsTo(2L)
                    .readSingle(dataSource);

            Map<String, Object> values4 = SqlUtil.select("COL1")
                    .from("TAB1")
                    .where("COL1").isEqualsTo(BigInteger.valueOf(2L))
                    .readSingle(dataSource);
            BigDecimal val = new BigDecimal(2);
            assertAll(
                    () -> assertEquals(val,values.get("COL1")),
                    () -> assertEquals(val,values2.get("COL1")),
                    () -> assertEquals(val,values3.get("COL1")),
                    () -> assertEquals(val,values4.get("COL1"))
            );
        } catch (Exception ex) {
            fail(ex);
        }
    }

    @Test
    public void shouldReturnAStreamResultSet() {
        try (Connection connection = dataSource.getConnection()) {
            List<Map<String,Object>> values = SqlUtil.select()
                    .from("TAB1")
                    .stream(dataSource)
                    .collect(Collectors.toList());
            assertEquals(2, values.size());

            List<Map<String,Object>> values2 = SqlUtil.select()
                    .from("TAB1")
                    .stream(connection)
                    .filter(map -> "TEST".equalsIgnoreCase((String) map.get("COL3")))
                    .collect(Collectors.toList());

            assertFalse(connection.isClosed());
            assertEquals(1, values2.size());
        } catch (Exception ex) {
            fail(ex);
        }
    }

    @Test
    public void shouldReturnOrderedResultSet() {
        try {
            List<Map<String,Object>> values = SqlUtil.select()
                    .from("TAB1")
                    .orderBy(Ordering.ASC, "COL3")
                    .readAll(dataSource);
            assertAll(
                    () -> assertEquals(2, values.size()),
                    () -> assertTrue("TEST".equalsIgnoreCase((String) values.get(0).get("COL3"))),
                    () -> assertTrue("TEST2".equalsIgnoreCase((String) values.get(1).get("COL3")))
            );

            List<Map<String,Object>> values2 = SqlUtil.select()
                    .from("TAB1")
                    .orderBy(Ordering.DESC, "COL3")
                    .readAll(dataSource);
            assertAll(
                    () -> assertEquals(2, values.size()),
                    () -> assertTrue("TEST2".equalsIgnoreCase((String) values2.get(0).get("COL3"))),
                    () -> assertTrue("TEST".equalsIgnoreCase((String) values2.get(1).get("COL3")))
            );
        } catch (Exception ex) {
            fail(ex);
        }
    }

    @Test
    public void shouldUseCustomerMapper() {
        try (Connection connection = dataSource.getConnection()) {

            CustomObject defaultObj = new CustomObject(2,3, "TEST");
            CustomObject defaultObj2 = new CustomObject(3,4,"TEST2");

            CustomObject object1 = SqlUtil.select()
                    .from("TAB1")
                    .readSingle(MAPPER, dataSource);

            CustomObject object2 = SqlUtil.select()
                    .from("TAB1")
                    .readSingle(MAPPER, connection);

            assertEquals(defaultObj, object1);
            assertEquals(defaultObj, object2);

            List<CustomObject> objects = SqlUtil.select()
                    .from("TAB1")
                    .readAll(MAPPER, dataSource);

            List<CustomObject> objects2 = SqlUtil.select()
                    .from("TAB1")
                    .readAll(MAPPER, connection);

            assertAll(
                    () -> assertEquals(2, objects.size()),
                    () -> assertTrue(objects.contains(defaultObj)),
                    () -> assertTrue(objects.contains(defaultObj2))
            );

            assertAll(
                    () -> assertEquals(2, objects2.size()),
                    () -> assertTrue(objects2.contains(defaultObj)),
                    () -> assertTrue(objects2.contains(defaultObj2))
            );

            Optional<CustomObject> optionalCustomObject = SqlUtil.select()
                    .from("TAB1")
                    .where("COL1").isEqualsTo(-1)
                    .readOptionalSingle(MAPPER, dataSource);

            Optional<CustomObject> optionalCustomObject2 = SqlUtil.select()
                    .from("TAB1")
                    .where("COL1").isEqualsTo(-1)
                    .readOptionalSingle(MAPPER, connection);

            assertFalse(optionalCustomObject.isPresent());
            assertFalse(optionalCustomObject2.isPresent());

            List<CustomObject> objectList = SqlUtil.select()
                    .from("TAB1")
                    .stream(MAPPER, dataSource)
                    .collect(Collectors.toList());
            List<CustomObject> objectList2 = SqlUtil.select()
                    .from("TAB1")
                    .stream(MAPPER, connection)
                    .collect(Collectors.toList());

            assertEquals(objects, objectList);
            assertEquals(objects, objectList2);

        } catch (Exception ex) {
            fail(ex);
        }
    }

    @Test
    public void shouldHandleAndThrowSQLException() {
        try {

            ResultSet rs = mock(ResultSet.class);
            PreparedStatement pr = mock(PreparedStatement.class);
            Connection connection = mock(Connection.class);
            ResultSetMetaData metaData = mock(ResultSetMetaData.class);

            when(connection.prepareStatement(anyString())).thenReturn(pr);
            when(pr.executeQuery()).thenReturn(rs);
            when(rs.next()).thenReturn(true);
            when(rs.getMetaData()).thenReturn(metaData);
            when(metaData.getColumnCount()).thenReturn(30);
            when(metaData.getColumnName(1)).thenThrow(SQLException.class);

            assertThrows(SQLException.class,
                    () -> SqlUtil.select("COL1", "COL2")
                    .from("TAB1")
                    .readSingle(connection));

            metaData = mock(ResultSetMetaData.class);
            when(rs.getMetaData()).thenReturn(metaData);
            when(metaData.getColumnCount()).thenReturn(30);
            when(metaData.getColumnName(1)).thenReturn("NAME");
            when(metaData.getColumnLabel(1)).thenReturn("NAME");
            when(metaData.getColumnType(1)).thenThrow(SQLException.class);

            assertThrows(SQLException.class,
                    () -> SqlUtil.select("COL1", "COL2")
                            .from("TAB1")
                            .readSingle(connection));

            assertThrows(SQLException.class,
                    () -> SqlUtil.select("COL1", "COL2")
                            .from("TAB1")
                            .readAll(connection));

            DataSource dataSource = mock(DataSource.class);
            when(dataSource.getConnection()).thenReturn(connection);

            assertThrows(SQLRuntimeException.class,
                    () -> SqlUtil.select("COL1", "COL2")
                            .from("TAB1")
                            .stream(dataSource)
                            .forEach(System.out::print));

            when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);

            assertThrows(SQLException.class,
                    () -> SqlUtil.select("COL1", "COL2")
                            .from("TAB1")
                            .stream(dataSource)
                            .forEach(System.out::print));

            assertThrows(SQLException.class,
                    () -> SqlUtil.select("COL1", "COL2")
                            .from("TAB1")
                            .stream(MAPPER, dataSource)
                            .forEach(System.out::print));

            Connection connection2 = mock(Connection.class);
            pr = mock(PreparedStatement.class);
            when(connection2.prepareStatement(anyString())).thenReturn(pr);
            doThrow(SQLException.class).when(pr).setString(anyInt(),anyString());

            assertThrows(SQLException.class,
                    () -> SqlUtil.select("COL1", "COL2")
                            .from("TAB1")
                            .where("COL1").isEqualsTo("2")
                            .readAll(connection2));

        } catch (Exception ex) {
            fail(ex);
        }
    }

    @Test
    public void shouldDate() {
        try {
            Pair<DataSource, ResultSet> mocked = mockResult();
            ResultSetMetaData metaData = mock(ResultSetMetaData.class);
            when(mocked.getValue().next()).thenReturn(true);
            when(mocked.getValue().getMetaData()).thenReturn(metaData);
            when(metaData.getColumnCount()).thenReturn(1);
            when(metaData.getColumnType(1)).thenReturn(Types.DATE);
            when(metaData.getColumnLabel(1)).thenReturn("COL1");
            when(metaData.getColumnName(1)).thenReturn("COL1");
            when(mocked.getValue().getDate("COL1")).thenReturn(new Date(System.currentTimeMillis()));

            Map<String, Object> values = SqlUtil.select("COL1")
                    .from("TAB1")
                    .readSingle(mocked.getKey());
            assertAll(
                    () -> assertEquals(1, values.size()),
                    () -> assertTrue(values.get("COL1") instanceof Date)
            );
        } catch (Exception ex) {
            fail(ex);
        }
    }

    public static class CustomObject {

        private final long col1;
        private final long col2;
        private final String col3;

        public CustomObject(long col1, long col2, String col3) {
            this.col1 = col1;
            this.col2 = col2;
            this.col3 = col3;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CustomObject that = (CustomObject) o;
            return col1 == that.col1 &&
                    col2 == that.col2 &&
                    Objects.equals(col3, that.col3);
        }

        @Override
        public int hashCode() {
            return Objects.hash(col1, col2, col3);
        }
    }
}
