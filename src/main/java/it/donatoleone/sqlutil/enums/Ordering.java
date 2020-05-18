package it.donatoleone.sqlutil.enums;

import it.donatoleone.sqlutil.interfaces.select.From;

/**
 * Define Ordering Type used in {@link From#orderBy(Ordering, String)} and
 * {@link From#orderBy(Ordering, String...)}
 */
public enum Ordering {
    ASC,
    DESC
}
