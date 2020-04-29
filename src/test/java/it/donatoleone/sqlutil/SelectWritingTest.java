package it.donatoleone.sqlutil;

import it.donatoleone.sqlutil.enums.LikeMatcher;
import it.donatoleone.sqlutil.impl.AliasFactory;
import it.donatoleone.sqlutil.impl.SqlUtil;
import it.donatoleone.sqlutil.impl.WhereFactory;
import it.donatoleone.sqlutil.interfaces.From;
import it.donatoleone.sqlutil.interfaces.Where;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class SelectWritingTest {

    private static final String SIMPLE_SELECT = "SELECT * FROM TABLE";
    private static final String COLUMN_SELECT = "SELECT COL2 , COL1 FROM TABLE";
    private static final String ALIAS_SELECT = "SELECT COL1 AS COLUMN1 , COL2 FROM TABLE";
    private static final String WHERE_EQUALS = ALIAS_SELECT + " WHERE COL3 = ?";
    private static final String WHERE_EQUALS_TWO = ALIAS_SELECT + " WHERE COL3 = ? AND COL4 = ?";
    private static final String WHERE_EQUALS_DEBUG = ALIAS_SELECT + " WHERE COL3 = 3";
    private static final String WHERE_NOT_EQUALS = ALIAS_SELECT + " WHERE COL3 <> ?";
    private static final String WHERE_NOT_EQUALS_DEBUG = ALIAS_SELECT + " WHERE COL3 <> 3";
    private static final String WHERE_GREATER = ALIAS_SELECT + " WHERE COL3 > ?";
    private static final String WHERE_GREATER_DEBUG = ALIAS_SELECT + " WHERE COL3 > 3";
    private static final String WHERE_LESS = ALIAS_SELECT + " WHERE COL3 < ?";
    private static final String WHERE_LESS_DEBUG = ALIAS_SELECT + " WHERE COL3 < 3";
    private static final String WHERE_GREATER_EQUALS = ALIAS_SELECT + " WHERE COL3 >= ?";
    private static final String WHERE_GREATER_EQUALS_DEBUG = ALIAS_SELECT + " WHERE COL3 >= 3";
    private static final String WHERE_LESS_EQUALS = ALIAS_SELECT + " WHERE COL3 <= ?";
    private static final String WHERE_LESS_EQUALS_DEBUG = ALIAS_SELECT + " WHERE COL3 <= 3";
    private static final String WHERE_BETWEEN = ALIAS_SELECT + " WHERE COL3 BETWEEN ? AND ?";
    private static final String WHERE_BETWEEN_DEBUG = ALIAS_SELECT + " WHERE COL3 BETWEEN 50 AND 60";
    private static final String WHERE_IN = ALIAS_SELECT + " WHERE COL3 IN (?,?)";
    private static final String WHERE_IN_DEBUG = ALIAS_SELECT + " WHERE COL3 IN (50,60)";
    private static final String WHERE_LIKE_FULL = ALIAS_SELECT + " WHERE COL3 LIKE '%?%'";
    private static final String WHERE_LIKE_FULL_DEBUG = ALIAS_SELECT + " WHERE COL3 LIKE '%VAL%'";
    private static final String WHERE_LIKE_START = ALIAS_SELECT + " WHERE COL3 LIKE '%?'";
    private static final String WHERE_LIKE_START_DEBUG = ALIAS_SELECT + " WHERE COL3 LIKE '%VAL'";
    private static final String WHERE_LIKE_END = ALIAS_SELECT + " WHERE COL3 LIKE '?%'";
    private static final String WHERE_LIKE_END_DEBUG = ALIAS_SELECT + " WHERE COL3 LIKE 'VAL%'";
    private static final String WHERE_EQUALS_OR = ALIAS_SELECT + " WHERE COL3 = '3' OR COL4 = 4";
    private static final String COMPOUND_WHERE_1 = "SELECT COL2 , COL1 FROM TABLE WHERE (COL3 = ? OR COL4 = ?)";
    private static final String COMPOUND_WHERE_DEBUG_1 = "SELECT COL2 , COL1 FROM TABLE WHERE (COL3 = 3 OR COL4 = 4)";
    private static final String COMPOUND_WHERE_2 = "SELECT COL2 , COL1 FROM TABLE WHERE COL3 = ? OR (COL3 = ? OR COL3 = ?)";
    private static final String COMPOUND_WHERE_3 = "SELECT COL2 , COL1 FROM TABLE WHERE COL3 = ? " +
            "OR (COL1 = ? AND COL2 = ?) AND (COL3 = ? OR COL4 = ?)";

    // SQL Writing Tests

    @Test
    public void shouldReturnSimpleSelect() {
        try {
            assertEquals(
                    SIMPLE_SELECT,
                    SqlUtil.select()
                        .from("TABLE")
                        .getSql()
            );
            assertEquals(
                    SIMPLE_SELECT,
                    SqlUtil.select()
                        .from("TABLE")
                        .getDebugSql()
            );
        } catch (Exception ex) {
            fail(ex);
        }
    }

    @Test
    public void shouldReturnColumnsSelect() {
        try {
            assertEquals(
                COLUMN_SELECT,
                SqlUtil.select("COL1", "COL2")
                    .from("TABLE")
                    .getSql()
            );
            assertEquals(
                ALIAS_SELECT,
                selectColumns()
                    .getSql()
            );

            assertEquals(
                    ALIAS_SELECT,
                    SqlUtil.select(AliasFactory.as("COL1","COLUMN1"),AliasFactory.column("COL2"),
                            AliasFactory.as("COL1", "COLUMN1"))
                            .from("TABLE")
                            .getDebugSql()
            );
        } catch (Exception ex) {
            fail(ex);
        }
    }

    private From selectColumns() {
        return SqlUtil.select(AliasFactory.as("COL1","COLUMN1"),AliasFactory.column("COL2"))
            .from("TABLE");
    }

    @Test
    public void shouldCreateSelectWithAndWheres() {
        try {

            // Equals

            assertEquals(
                    WHERE_EQUALS,
                    selectColumns()
                        .where("COL3").isEqualsTo(3)
                        .getSql()
            );

            assertEquals(
                    WHERE_EQUALS_DEBUG,
                    selectColumns()
                        .where("COL3").isEqualsTo(3)
                        .getDebugSql()
            );

            assertEquals(
                    WHERE_EQUALS_TWO,
                    selectColumns()
                        .where("COL3").isEqualsTo(3)
                        .where("COL4").isEqualsTo(4)
                        .getSql()
            );

            // Not Equals

            assertEquals(
                    WHERE_NOT_EQUALS,
                    selectColumns()
                            .where("COL3").isNotEqualsTo(3)
                            .getSql()
            );

            assertEquals(
                    WHERE_NOT_EQUALS_DEBUG,
                    selectColumns()
                            .where("COL3").isNotEqualsTo(3)
                            .getDebugSql()
            );

            // Greater

            assertEquals(
                    WHERE_GREATER,
                    selectColumns()
                            .where("COL3").isGreaterThan(3)
                            .getSql()
            );

            assertEquals(
                    WHERE_GREATER_DEBUG,
                    selectColumns()
                            .where("COL3").isGreaterThan(3)
                            .getDebugSql()
            );

            // Less

            assertEquals(
                    WHERE_LESS,
                    selectColumns()
                            .where("COL3").isLessThan(3)
                            .getSql()
            );

            assertEquals(
                    WHERE_LESS_DEBUG,
                    selectColumns()
                            .where("COL3").isLessThan(3)
                            .getDebugSql()
            );

            // Greater or Equals
            assertEquals(
                    WHERE_GREATER_EQUALS,
                    selectColumns()
                            .where("COL3").isGreaterOrEqualsTo(3)
                            .getSql()
            );

            assertEquals(
                    WHERE_GREATER_EQUALS_DEBUG,
                    selectColumns()
                            .where("COL3").isGreaterOrEqualsTo(3)
                            .getDebugSql()
            );

            // Less or Equals

            assertEquals(
                    WHERE_LESS_EQUALS,
                    selectColumns()
                            .where("COL3").isLessOrEqualsTo(3)
                            .getSql()
            );

            assertEquals(
                    WHERE_LESS_EQUALS_DEBUG,
                    selectColumns()
                            .where("COL3").isLessOrEqualsTo(3)
                            .getDebugSql()
            );

            // Between

            assertEquals(
                    WHERE_BETWEEN,
                    selectColumns()
                            .where("COL3").between(50,60)
                            .getSql()
            );

            assertEquals(
                    WHERE_BETWEEN_DEBUG,
                    selectColumns()
                            .where("COL3").between(50,60)
                            .getDebugSql()
            );

            // In

            assertEquals(
                    WHERE_IN,
                    selectColumns()
                            .where("COL3").in(Arrays.asList(50,60))
                            .getSql()
            );

            assertEquals(
                    WHERE_IN_DEBUG,
                    selectColumns()
                            .where("COL3").in(Arrays.asList(50,60))
                            .getDebugSql()
            );

            // Like Full

            assertEquals(
                    WHERE_LIKE_FULL,
                    selectColumns()
                            .where("COL3").like("VAL", LikeMatcher.FULL_MATCH)
                            .getSql()
            );

            assertEquals(
                    WHERE_LIKE_FULL_DEBUG,
                    selectColumns()
                            .where("COL3").like("VAL", LikeMatcher.FULL_MATCH)
                            .getDebugSql()
            );

            // Like Start

            assertEquals(
                    WHERE_LIKE_START,
                    selectColumns()
                            .where("COL3").like("VAL", LikeMatcher.START_MATCH)
                            .getSql()
            );

            assertEquals(
                    WHERE_LIKE_START_DEBUG,
                    selectColumns()
                            .where("COL3").like("VAL", LikeMatcher.START_MATCH)
                            .getDebugSql()
            );

            // Like End

            assertEquals(
                    WHERE_LIKE_END,
                    selectColumns()
                            .where("COL3").like("VAL", LikeMatcher.END_MATCH)
                            .getSql()
            );

            assertEquals(
                    WHERE_LIKE_END_DEBUG,
                    selectColumns()
                            .where("COL3").like("VAL", LikeMatcher.END_MATCH)
                            .getDebugSql()
            );
        } catch (Exception ex) {
            fail(ex);
        }
    }

    @Test
    public void shouldCreateSelectWithOrWhere() {
        assertEquals(
                WHERE_EQUALS_OR,
                selectColumns()
                        .where("COL3").isEqualsTo("3")
                        .orWhere("COL4").isEqualsTo(4)
                        .getDebugSql()
        );
    }

    @Test
    public void shouldCreateSelectWithCompoundWhere() {
        assertEquals(
                COMPOUND_WHERE_DEBUG_1,
                SqlUtil.select("COL1", "COL2")
                        .from("TABLE")
                        .where(
                                WhereFactory.compoundWhere(
                                        WhereFactory.where("COL3").isEqualsTo(3),
                                        WhereFactory.orWhere("COL4").isEqualsTo(4)
                                )
                        )
                        .getDebugSql()
        );

        assertEquals(
                COMPOUND_WHERE_1,
                SqlUtil.select("COL1", "COL2")
                        .from("TABLE")
                        .where(
                                WhereFactory.compoundWhere(
                                        WhereFactory.where("COL3").isEqualsTo(3),
                                        WhereFactory.orWhere("COL4").isEqualsTo(4)
                                )
                        )
                        .getSql()
        );

        assertEquals(
                COMPOUND_WHERE_2,
                SqlUtil.select("COL1", "COL2")
                        .from("TABLE")
                        .where("COL3").isEqualsTo(3)
                        .orWhere(
                                WhereFactory.compoundWhere(
                                        WhereFactory.where("COL3").isEqualsTo(2),
                                        WhereFactory.orWhere("COL3").isEqualsTo(4)
                                )
                        ).getSql()
        );

        assertEquals(
                COMPOUND_WHERE_3,
                SqlUtil.select("COL1","COL2")
                        .from("TABLE")
                        .where("COL3").isEqualsTo(3)
                        .orWhere(
                                WhereFactory.compoundWhere(
                                        WhereFactory.where("COL1").isEqualsTo(1),
                                        WhereFactory.where("COL2").isEqualsTo(2)
                                )
                        ).where(
                               WhereFactory.compoundWhere(
                                       WhereFactory.where("COL3").isEqualsTo(3),
                                       WhereFactory.orWhere("COL4").isEqualsTo(4)
                               )
                        ).getSql()
        );
    }
}
