package it.donatoleone.sqlutil;

import it.donatoleone.sqlutil.impl.SqlUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class InsertExecutingTest extends BaseDBTest {

    @BeforeAll
    private static void init() throws Exception {
        init("/insertTest.sql");
    }

    @Test
    public void shouldInsertOneRecord() {
        try (Connection connection = dataSource.getConnection()) {
            SqlUtil.insertInto("TABLE1INSERT")
                    .insert("COL1").withValue("TEST")
                    .insert("COL2").withValue(2)
                    .execute(connection);

            assertTrue(
                    SqlUtil.select()
                        .from("TABLE1INSERT")
                        .where("COL1").isEqualsTo("TEST")
                        .where("COL2").isEqualsTo(2)
                        .readOptionalSingle(dataSource)
                        .isPresent()
            );

            SqlUtil.insertInto("TABLE1INSERT")
                    .insert("COL1").withValue("TEST2")
                    .insert("COL2").withValue(3)
                    .execute(dataSource);

            assertTrue(
                    SqlUtil.select()
                            .from("TABLE1INSERT")
                            .where("COL1").isEqualsTo("TEST2")
                            .where("COL2").isEqualsTo(3)
                            .readOptionalSingle(dataSource)
                            .isPresent()
            );
        } catch (Exception ex) {
            fail(ex);
        }
    }
}
