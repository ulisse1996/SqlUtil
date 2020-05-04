package it.donatoleone.sqlutil;

import it.donatoleone.sqlutil.enums.JoinType;
import it.donatoleone.sqlutil.impl.AliasFactory;
import it.donatoleone.sqlutil.impl.OnFactory;
import it.donatoleone.sqlutil.impl.SqlUtil;
import it.donatoleone.sqlutil.impl.WhereFactory;
import it.donatoleone.sqlutil.interfaces.Alias;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class SelectExecutingTest extends BaseDBTest {

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
                                    WhereFactory.orWhere("COL1").isLessThan(2)
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
                    .where("COL1").isEqualsTo(2.5)
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
}
