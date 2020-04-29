package it.donatoleone.sqlutil.impl;

import it.donatoleone.sqlutil.interfaces.Alias;
import it.donatoleone.sqlutil.util.MessageFactory;

import java.util.Objects;

public final class AliasFactory {

    private AliasFactory() {}

    public static Alias column(String column) {
        return new AliasImpl(column);
    }

    public static Alias as(String column, String alias) {
        return new AliasImpl(column, alias);
    }

    private static class AliasImpl implements Alias {

        private final String key;
        private final String value;

        private AliasImpl(String key) {
            this(key, null);
        }

        private AliasImpl(String key, String value) {
            this.key = Objects.requireNonNull(key, MessageFactory.notNull("column"));
            this.value = value;
        }

        @Override
        public String getKey() {
            return this.key;
        }

        @Override
        public String getAlias() {
            return this.value;
        }

        @Override
        public String getSql() {
            return getKey() + (getAlias() != null ? " AS " + value : "");
        }

        @Override
        public String getDebugSql() {
            return getSql();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AliasImpl alias = (AliasImpl) o;
            return Objects.equals(key, alias.key) &&
                    Objects.equals(value, alias.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, value);
        }
    }
}
