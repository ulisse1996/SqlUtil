package it.donatoleone.sqlutil.enums;

import it.donatoleone.sqlutil.interfaces.common.CommonOperations;

/**
 * Define how match like operator in {@link CommonOperations}
 */
public enum LikeMatcher {
    FULL_MATCH,
    START_MATCH,
    END_MATCH
}
