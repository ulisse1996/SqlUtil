package it.donatoleone.sqlutil.util;

import java.math.BigDecimal;
import java.math.BigInteger;

public class StringUtils {

    private StringUtils() {}

    public static String asString(Object value) {
        if (value instanceof Number) {
            return StringUtils.toString((Number) value);
        }

        if (value instanceof String) {
            return String.format("'%s'", value);
        }

        return value.toString();
    }

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

    public static String replaceSingle(String sql) {
        int indexAnd = sql.indexOf("AND");
        int indexOr = sql.indexOf("OR");
        if (indexAnd < indexOr || indexOr == -1) {
            return sql.replaceFirst("AND","")
                    .replace("( ","(");
        } else {
            return sql.replaceFirst("OR","")
                    .replace("( ", "(");
        }
    }

    public static String replaceFirstAfterParenthesis(String string) {
        String afterParenthesis = string.substring(string.indexOf('('));
        return string.substring(0,string.indexOf('(')) + replaceSingle(afterParenthesis);
    }

    public static String replaceFirstBeforeParenthesis(String string) {
        String beforeParenthesis = string.substring(0,string.indexOf('('));
        return beforeParenthesis.replaceFirst("AND","")
                .replaceFirst("OR","") + string.substring(string.indexOf('('));
    }
}
