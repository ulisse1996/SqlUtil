package it.donatoleone.sqlutil.interfaces.create;

public interface RestrictionColumnDefinition extends CreateTable {

    RestrictionColumnDefinition primary();
    RestrictionColumnDefinition notNull();
}
