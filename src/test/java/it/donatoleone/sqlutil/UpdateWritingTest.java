package it.donatoleone.sqlutil;

import it.donatoleone.sqlutil.impl.SqlUtil;
import it.donatoleone.sqlutil.impl.WhereFactory;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

public class UpdateWritingTest {

    private static final String SIMPLE_UPDATE = "UPDATE TABLE1 SET COL1 = ?";
    private static final String SIMPLE_UPDATE_DEBUG = "UPDATE TABLE1 SET COL1 = 3";
    private static final String UPDATE_WHERE_EXISTS = SIMPLE_UPDATE + " WHERE EXISTS (SELECT AL2 FROM TAB1 WHERE AL2 = ?)";
    private static final String UPDATE_WHERE_EXISTS_DEBUG = SIMPLE_UPDATE_DEBUG + " WHERE EXISTS (SELECT AL2 FROM TAB1 WHERE AL2 = 3)";
    private static final String MULTIPLE_UPDATE = "UPDATE TABLE1 SET COL1 = ?, COL2 = ?";
    private static final String MULTIPLE_UPDATE_DEBUG = "UPDATE TABLE1 SET COL1 = 3, COL2 = 4";
    private static final String UPDATE_WITH_WHERE = "UPDATE TABLE1 SET COL1 = ? WHERE COL2 = ?";
    private static final String UPDATE_WITH_WHERE_DEBUG = "UPDATE TABLE1 SET COL1 = 3 WHERE COL2 = 4";
    private static final String UPDATE_WITH_EXPRESSION = "UPDATE TABLE1 SET COL1 = ?, COL2 = CURRENT_DATE";
    private static final String UPDATE_WITH_EXPRESSION_DEBUG = "UPDATE TABLE1 SET COL1 = 3, COL2 = CURRENT_DATE";
    private static final String COMPLEX_UPDATE = "UPDATE TABLE1 SET COL1 = (SELECT COLUMN1 FROM TABLE2 WHERE COLUMN1 = ?), COL2 = CURRENT_DATE WHERE (COL1 = ? OR COL1 = ?)";
    private static final String COMPLEX_UPDATE_DEBUG = "UPDATE TABLE1 SET COL1 = (SELECT COLUMN1 FROM TABLE2 WHERE COLUMN1 = 1), COL2 = CURRENT_DATE WHERE (COL1 = 3 OR COL1 = 4)";

    @Test
    public void shouldWriteSimpleUpdate() {

        assertEquals(
                SIMPLE_UPDATE,
                SqlUtil.update("TABLE1")
                    .set("COL1").withValue(3)
                    .getSql()
        );

        assertEquals(
                SIMPLE_UPDATE_DEBUG,
                SqlUtil.update("TABLE1")
                        .set("COL1").withValue(3)
                        .getDebugSql()
        );
    }


    @Test
    public void shouldWriteUpdateWithWhere() {

        assertEquals(
                UPDATE_WITH_WHERE,
                SqlUtil.update("TABLE1")
                        .set("COL1").withValue(3)
                        .where("COL2").isEqualsTo(4)
                        .getSql()
        );

        assertEquals(
                UPDATE_WITH_WHERE_DEBUG,
                SqlUtil.update("TABLE1")
                        .set("COL1").withValue(3)
                        .where("COL2").isEqualsTo(4)
                        .getDebugSql()
        );
    }

    @Test
    public void shouldWriteMultipleUpdateSet() {

        assertEquals(
                MULTIPLE_UPDATE,
                SqlUtil.update("TABLE1")
                        .set("COL1").withValue(3)
                        .set("COL2").withValue(4)
                        .getSql()
        );

        assertEquals(
                MULTIPLE_UPDATE_DEBUG,
                SqlUtil.update("TABLE1")
                        .set("COL1").withValue(3)
                        .set("COL2").withValue(4)
                        .getDebugSql()
        );
    }

    @Test
    public void shouldWriteUpdateWithSetExpression() {

        assertEquals(
                UPDATE_WITH_EXPRESSION,
                SqlUtil.update("TABLE1")
                        .set("COL1").withValue(3)
                        .set("COL2").withExpression("CURRENT_DATE")
                        .getSql()
        );

        assertEquals(
                UPDATE_WITH_EXPRESSION_DEBUG,
                SqlUtil.update("TABLE1")
                        .set("COL1").withValue(3)
                        .set("COL2").withExpression("CURRENT_DATE")
                        .getDebugSql()
        );
    }

    @Test
    public void shouldWriteComplexUpdate() {

        assertEquals(
                COMPLEX_UPDATE,
                SqlUtil.update("TABLE1")
                    .set("COL1").withResult(
                        SqlUtil.select("COLUMN1")
                            .from("TABLE2")
                            .where("COLUMN1").isEqualsTo(1)
                    )
                    .set("COL2").withExpression("CURRENT_DATE")
                    .where(
                        WhereFactory.compoundWhere(
                            WhereFactory.where("COL1").isEqualsTo(3),
                            WhereFactory.orWhere("COL1").isEqualsTo(4)
                        )
                    )
                    .getSql()
        );

        assertEquals(
                COMPLEX_UPDATE_DEBUG,
                SqlUtil.update("TABLE1")
                    .set("COL1").withResult(
                        SqlUtil.select("COLUMN1")
                            .from("TABLE2")
                            .where("COLUMN1").isEqualsTo(1)
                    )
                    .set("COL2").withExpression("CURRENT_DATE")
                    .where(
                        WhereFactory.compoundWhere(
                            WhereFactory.where("COL1").isEqualsTo(3),
                            WhereFactory.orWhere("COL1").isEqualsTo(4)
                        )
                    )
                    .getDebugSql()
        );
    }

    @Test
    public void shouldCreateUpdateWithWhereExists() {

        assertEquals(
                UPDATE_WHERE_EXISTS,
                SqlUtil.update("TABLE1")
                        .set("COL1").withValue(3)
                        .whereExists(
                                SqlUtil.select("AL2").from("TAB1")
                                        .where("AL2").isEqualsTo(3)
                        )
                        .getSql()
        );

        assertEquals(
                UPDATE_WHERE_EXISTS_DEBUG,
                SqlUtil.update("TABLE1")
                        .set("COL1").withValue(3)
                        .whereExists(
                                SqlUtil.select("AL2").from("TAB1")
                                        .where("AL2").isEqualsTo(3)
                        )
                        .getDebugSql()
        );
    }
}
