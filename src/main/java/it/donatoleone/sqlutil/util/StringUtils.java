package it.donatoleone.sqlutil.util;

import java.math.BigDecimal;
import java.math.BigInteger;

public class StringUtils {

    private StringUtils() {}

    public static String toString(Number val) {
        if (Double.class.isAssignableFrom(val.getClass())) {
            return String.valueOf(val.doubleValue());
        } else if (Float.class.isAssignableFrom(val.getClass())) {
            return String.valueOf(val.floatValue());
        } else if (Long.class.isAssignableFrom(val.getClass())) {
            return String.valueOf(val.longValue());
        } else if (Integer.class.isAssignableFrom(val.getClass())) {
            return String.valueOf(val.intValue());
        } else if (Short.class.isAssignableFrom(val.getClass())) {
            return String.valueOf(val.shortValue());
        } else if (BigDecimal.class.isAssignableFrom(val.getClass())) {
            return ((BigDecimal) val).toPlainString();
        } else if (BigInteger.class.isAssignableFrom(val.getClass())){
            return val.toString();
        }

        return val.toString();
    }
}
