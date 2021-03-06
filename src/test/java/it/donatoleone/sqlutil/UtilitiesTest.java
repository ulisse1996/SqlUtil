package it.donatoleone.sqlutil;

import it.donatoleone.sqlutil.util.MessageFactory;
import it.donatoleone.sqlutil.util.Pair;
import it.donatoleone.sqlutil.util.StringUtils;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

public class UtilitiesTest {

    @Test
    public void shouldReturnStringFromNumber() {
        Number myNumber = new Number() {
            @Override
            public int intValue() {
                return 0;
            }

            @Override
            public long longValue() {
                return 0;
            }

            @Override
            public float floatValue() {
                return 0;
            }

            @Override
            public double doubleValue() {
                return 0;
            }

            @Override
            public String toString() {
                return String.valueOf(intValue());
            }
        };
        assertAll(
                () -> assertEquals("2.0", StringUtils.toString(2d)),
                () -> assertEquals("3.0", StringUtils.toString(3f)),
                () -> assertEquals("3", StringUtils.toString(3L)),
                () -> assertEquals("3", StringUtils.toString(3)),
                () -> assertEquals("0", StringUtils.toString((short) 0)),
                () -> assertEquals("2", StringUtils.toString(BigDecimal.valueOf(2))),
                () -> assertEquals("2", StringUtils.toString(BigInteger.valueOf(2))),
                () -> assertEquals("0", StringUtils.toString(myNumber))
        );
    }

    @Test
    public void shouldBlockImmutableChange() {
        try {
            Pair<String,String> pair = Pair.immutable("","");
            pair.setValue("");
        } catch (Exception ex) {
            assertTrue(ex instanceof IllegalArgumentException);
            assertEquals(MessageFactory.immutable(), ex.getMessage());
        }

        Pair<String,String> pair = Pair.mutable("","");
        assertEquals("mut", pair.setValue("mut"));
    }
}
