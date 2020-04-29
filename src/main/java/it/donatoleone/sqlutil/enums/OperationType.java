package it.donatoleone.sqlutil.enums;

public enum OperationType {

    EQUALS("=", true),
    NOT_EQUALS("<>", true),
    GREATER(">", true),
    LESS("<", true),
    GREATER_EQUALS(">=", true),
    LESS_EQUALS("<=", true),
    BETWEEN("", false),
    IN("", false),
    LIKE("", false);

    private final String val;
    private final boolean simpleOperator;

    OperationType(String val, boolean simpleOperator) {
        this.val = val;
        this.simpleOperator = simpleOperator;
    }

    public String stringValue() {
        return this.val;
    }

    public boolean isSimple() {
        return this.simpleOperator;
    }
}
