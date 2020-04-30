package it.donatoleone.sqlutil.enums;

import it.donatoleone.sqlutil.util.StringValue;

public enum JoinType implements StringValue {

    INNER_JOIN("JOIN"),
    LEFT_JOIN("LEFT JOIN"),
    RIGHT_JOIN("RIGHT JOIN"),
    FULL_JOIN("FULL JOIN");

    private final String value;

    @Override
    public String getValue() {
        return this.value;
    }

    JoinType(String value) {
        this.value = value;
    }
}
