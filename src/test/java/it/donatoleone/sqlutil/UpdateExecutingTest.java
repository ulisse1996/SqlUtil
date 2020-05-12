package it.donatoleone.sqlutil;

import it.donatoleone.sqlutil.impl.SqlUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class UpdateExecutingTest extends BaseDBTest {

    @BeforeAll
    public static void init() throws Exception {
        init("/updateTest.sql");
    }

    @Test
    public void shouldUpdateAllRow() {

        try {
            List<Map<String,Object>> values = SqlUtil.select()
                    .from("TABLE1")
                    .readAll(dataSource);

            assertAll(
                    () -> assertEquals(2, values.size()),
                    () -> assertEquals("PRE_UPDATE", values.get(0).get("COL1")),
                    () -> assertEquals("PRE_UPDATE", values.get(1).get("COL1"))
            );

            // Now execute update for all row

            SqlUtil.update("TABLE1")
                    .set("COL1").withValue("AFTER_UPDATE")
                    .execute(dataSource);

            List<Map<String,Object>> updatedValues = SqlUtil.select()
                    .from("TABLE1")
                    .readAll(dataSource);

            assertAll(
                    () -> assertEquals(2, updatedValues.size()),
                    () -> assertEquals("AFTER_UPDATE", updatedValues.get(0).get("COL1")),
                    () -> assertEquals("AFTER_UPDATE", updatedValues.get(1).get("COL1"))
            );
        } catch (Exception ex) {
            fail(ex);
        }
    }

    @Test
    public void shouldUpdateOnlyOneRow() {

        try (Connection connection = dataSource.getConnection()) {
            List<Map<String,Object>> values = SqlUtil.select()
                    .from("TABLE2")
                    .readAll(dataSource);

            assertEquals(2, values.size());

            BigDecimal where = (BigDecimal) values.get(0).get("COL2");
            String update = "UPD_DATE";

            SqlUtil.update("TABLE2")
                    .set("COL1").withValue(update)
                    .where("COL2").isEqualsTo(where)
                    .execute(connection);

            List<Map<String,Object>> updatedValues = SqlUtil.select()
                    .from("TABLE2")
                    .readAll(dataSource);

            // Remove not updated
            updatedValues.removeAll(values);

            assertAll(
                    () -> assertEquals(1, updatedValues.size()),
                    () -> assertEquals(where, updatedValues.get(0).get("COL2")),
                    () -> assertEquals("UPD_DATE", updatedValues.get(0).get("COL1"))
            );
        } catch (Exception ex) {
            fail(ex);
        }
    }

    @Test
    public void shouldUpdateUsingExpression() {
        try {

            List<Map<String,Object>> values = SqlUtil.select()
                    .from("TABLE3")
                    .readAll(dataSource);

            BigDecimal where = (BigDecimal) values.get(0).get("COL2");
            String result = (String) values.get(0).get("COL1");
            String subResult = result.substring(1,4);

            SqlUtil.update("TABLE3")
                    .set("COL1").withExpression("SUBSTRING(COL1,2,3)")
                    .where("COL2").isEqualsTo(where)
                    .execute(dataSource);

            List<Map<String,Object>> updatedValues = SqlUtil.select()
                    .from("TABLE3")
                    .readAll(dataSource);

            updatedValues.removeAll(values);

            assertAll(
                    () -> assertEquals(1, updatedValues.size()),
                    () -> assertEquals(where, updatedValues.get(0).get("COL2")),
                    () -> assertEquals(subResult, updatedValues.get(0).get("COL1"))
            );

        } catch (Exception ex) {
            fail(ex);
        }
    }

    @Test
    public void shouldUpdateUsingSubQuery() {
        try {

            List<Map<String,Object>> values = SqlUtil.select()
                    .from("TABLE4")
                    .readAll(dataSource);

            BigDecimal where = (BigDecimal) values.get(0).get("COL2");
            String result = (String) values.get(0).get("COL1");

            assertNotEquals("FROM_DUAL", result);

            SqlUtil.update("TABLE4")
                    .set("COL1").withResult(
                        SqlUtil.select("COL1")
                            .from("TABLE_DUAL")
                    ).execute(dataSource);

            List<Map<String,Object>> updatedValues = SqlUtil.select()
                    .from("TABLE4")
                    .readAll(dataSource);

            updatedValues.removeAll(values);

            assertAll(
                    () -> assertEquals(1, updatedValues.size()),
                    () -> assertEquals(where, updatedValues.get(0).get("COL2")),
                    () -> assertEquals("FROM_DUAL", updatedValues.get(0).get("COL1"))
            );

        } catch (Exception ex) {
            fail(ex);
        }
    }
}
