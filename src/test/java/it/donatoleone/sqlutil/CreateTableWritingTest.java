package it.donatoleone.sqlutil;

import it.donatoleone.sqlutil.impl.SqlUtil;
import it.donatoleone.sqlutil.interfaces.create.CreateTable;
import org.junit.jupiter.api.Test;

import java.sql.JDBCType;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CreateTableWritingTest {

    private static final String CREATE_TABLE_SIMPLE = "CREATE TABLE SIMPLE (COLUMN1 VARCHAR(255), COLUMN2 NUMERIC(5))";
    private static final String CREATE_TABLE_CONSTRAIN = "CREATE TABLE CONST (COLUMN1 VARCHAR(5) NOT NULL, COLUMN2 NUMERIC(10,2) NOT NULL, PRIMARY KEY (COLUMN1))";

    @Test
    public void shouldWriteSimpleTable() {

        assertEquals(
                CREATE_TABLE_SIMPLE,
                SqlUtil.createTable("SIMPLE")
                    .column("COLUMN1").withType(JDBCType.VARCHAR, 255)
                    .column("COLUMN2").withType(JDBCType.NUMERIC, 5)
                    .getSql()
        );

        assertEquals(
                CREATE_TABLE_SIMPLE,
                SqlUtil.createTable("SIMPLE")
                        .column("COLUMN1").withType(JDBCType.VARCHAR, 255)
                        .column("COLUMN2").withType(JDBCType.NUMERIC, 5)
                        .getDebugSql()
        );
    }

    @Test
    public void shouldWriteComplexCreate() {

        assertEquals(
                CREATE_TABLE_CONSTRAIN,
                SqlUtil.createTable("CONST")
                    .column("COLUMN1").withType(JDBCType.VARCHAR, 5).notNull().primary()
                    .column("COLUMN2").withType(JDBCType.NUMERIC, 10, 2).notNull()
                    .getSql()
        );

        assertEquals(
                CREATE_TABLE_CONSTRAIN,
                SqlUtil.createTable("CONST")
                    .column("COLUMN1").withType(JDBCType.VARCHAR, 5).notNull().primary()
                    .column("COLUMN2").withType(JDBCType.NUMERIC, 10, 2).notNull()
                    .getDebugSql()
        );
    }

    @Test
    public void shouldStopBadColumnDefinitions() {

        assertThrows(IllegalArgumentException.class,
                () -> SqlUtil.createTable("TABLE")
                    .column("COLUMN1").withType(JDBCType.VARCHAR, -1)
        );

        assertThrows(IllegalArgumentException.class,
                () -> SqlUtil.createTable("TABLE")
                        .column("COLUMN1").withType(JDBCType.VARCHAR, 1, -1)
        );

        assertThrows(IllegalArgumentException.class,
                () -> SqlUtil.createTable("TABLE")
                        .column("COLUMN1").withType(JDBCType.VARCHAR, 5, 10)
        );
    }
}
