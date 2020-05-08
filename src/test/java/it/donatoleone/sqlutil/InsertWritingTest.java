package it.donatoleone.sqlutil;

import it.donatoleone.sqlutil.impl.SqlUtil;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

public class InsertWritingTest {

    private static final String SIMPLE_INSERT = "INSERT INTO TABLE1 (COL1,COL2) VALUES (?,?)";
    private static final String SIMPLE_INSERT_DEBUG = "INSERT INTO TABLE1 (COL1,COL2) VALUES (3,3)";
    private static final String EXPRESSION_INSERT = "INSERT INTO TABLE1 (COL1) VALUES (TO_DATE('2020-06-05', 'YYYY-MM-DD'))";
    private static final String FROM_INSERT_DEBUG = "INSERT INTO TABLE1 (COL1) VALUES (SELECT COL3 FROM TABLE2 WHERE COL4 = 1)";
    private static final String FROM_INSERT = "INSERT INTO TABLE1 (COL1) VALUES (SELECT COL3 FROM TABLE2 WHERE COL4 = ?)";

    @Test
    public void shouldCreateSimpleInsert() {

        assertEquals(
                SIMPLE_INSERT,
                SqlUtil.insertInto("TABLE1")
                    .insert("COL1").withValue(3)
                    .insert("COL2").withValue(3)
                    .getSql()
        );

        assertEquals(
                SIMPLE_INSERT_DEBUG,
                SqlUtil.insertInto("TABLE1")
                        .insert("COL1").withValue(3)
                        .insert("COL2").withValue(3)
                        .getDebugSql()
        );
    }

    @Test
    public void shouldCreateExpressionInsert() {

        assertEquals(
                EXPRESSION_INSERT,
                SqlUtil.insertInto("TABLE1")
                    .insert("COL1").withExpression("TO_DATE('2020-06-05', 'YYYY-MM-DD')")
                    .getSql()
        );

        assertEquals(
                EXPRESSION_INSERT,
                SqlUtil.insertInto("TABLE1")
                    .insert("COL1").withExpression("TO_DATE('2020-06-05', 'YYYY-MM-DD')")
                    .getDebugSql()
        );
    }

    @Test
    public void shouldCreateSubQueryInsert() {

        assertEquals(
                FROM_INSERT,
                SqlUtil.insertInto("TABLE1")
                    .insert("COL1").withResult(
                            SqlUtil.select("COL3")
                                .from("TABLE2")
                                .where("COL4").isEqualsTo(1)
                ).getSql()
        );

        assertEquals(
                FROM_INSERT_DEBUG,
                SqlUtil.insertInto("TABLE1")
                        .insert("COL1").withResult(
                        SqlUtil.select("COL3")
                                .from("TABLE2")
                                .where("COL4").isEqualsTo(1)
                ).getDebugSql()
        );
    }
}
