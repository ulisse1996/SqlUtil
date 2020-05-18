package it.donatoleone.sqlutil;

import it.donatoleone.sqlutil.impl.SqlUtil;
import it.donatoleone.sqlutil.interfaces.create.ColumnDefinition;
import it.donatoleone.sqlutil.interfaces.create.CreateTable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.JDBCType;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class CreateTableExecutingTest extends BaseDBTest {

    @BeforeAll
    public static void init() throws Exception {
        init("");
    }

    @Test
    public void shouldCreateTables() {
        try (Connection connection = dataSource.getConnection()) {

            SqlUtil.createTable("TABLE_TEST_1")
                    .column("COL1").withType(JDBCType.VARCHAR, 10)
                    .execute(connection);

            SqlUtil.createTable("TABLE_TEST_2")
                    .column("COL1").withType(JDBCType.VARCHAR, 10)
                    .execute(dataSource);

            SqlUtil.insertInto("TABLE_TEST_1")
                    .insert("COL1").withValue("20")
                    .execute(dataSource);

            SqlUtil.insertInto("TABLE_TEST_2")
                    .insert("COL1").withValue("20")
                    .execute(dataSource);

            Map<String,Object> values = new HashMap<String,Object>() {{
               put("COL1", "20");
            }};

            assertEquals(
                    values, assertDoesNotThrow(
                    () -> SqlUtil.select()
                        .from("TABLE_TEST_1")
                        .readSingle(dataSource)
            ));

            assertEquals(
                    values, assertDoesNotThrow(
                        () -> SqlUtil.select()
                                .from("TABLE_TEST_2")
                                .readSingle(dataSource)
                    ));

        } catch (Exception ex) {
            fail(ex);
        }
    }

    @Test
    public void shouldUniqueColumnDefinition() {
        try {
            CreateTable table = SqlUtil.createTable("TABLE_TEST_3")
                    .column("COL1").withType(JDBCType.VARCHAR, 20)
                    .column("COL1").withType(JDBCType.VARCHAR, 20);

            assertEquals(1, getColumns(table).size());
        } catch (Exception ex) {
            fail(ex);
        }
    }

    @SuppressWarnings("unchecked")
    private Set<ColumnDefinition> getColumns(CreateTable table) throws Exception {
        Field parentField = table.getClass().getDeclaredField("parent");
        parentField.setAccessible(true);
        Object parent = parentField.get(table);
        Field columnsField = parent.getClass().getDeclaredField("columns");
        columnsField.setAccessible(true);
        return (Set<ColumnDefinition>) columnsField.get(parent);
    }
}
