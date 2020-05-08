package it.donatoleone.sqlutil.impl;

import it.donatoleone.sqlutil.interfaces.Alias;
import it.donatoleone.sqlutil.interfaces.From;
import it.donatoleone.sqlutil.interfaces.Select;
import it.donatoleone.sqlutil.util.MessageFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class SelectBuilder implements Select {

    private final Set<Alias> aliases;
    private final List<String> columns;
    private boolean selectAll;

    SelectBuilder() {
        this.aliases = Collections.unmodifiableSet(Collections.emptySet());
        this.columns = Collections.unmodifiableList(Collections.emptyList());
        selectAll = true;
    }

    SelectBuilder(Alias... aliases) {
        this.aliases = Collections.unmodifiableSet(
                new HashSet<>(
                        Arrays.asList(
                            Objects.requireNonNull(aliases, MessageFactory.notNull("aliases"))
                        )
                )
        );
        this.columns = Collections.unmodifiableList(Collections.emptyList());
    }

    SelectBuilder(String... columns) {
        this.columns = Collections.unmodifiableList(
                Arrays.asList(
                        Objects.requireNonNull(columns, MessageFactory.notNull("columns"))
                )
        );
        this.aliases = Collections.unmodifiableSet(Collections.emptySet());
    }

    public From from(String table) {
        return StatementFactory.buildFrom(
                Objects.requireNonNull(table, MessageFactory.notNull("table")),
                this
        );
    }

    @Override
    public List<String> getColumns() {
        if (columns.isEmpty() && aliases.isEmpty()) {
            return Collections.emptyList();
        } else if (!columns.isEmpty()) {
            return this.columns;
        } else {
            return this.aliases
                    .stream()
                    .map(Alias::getAlias)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public String getSql() {
        return getString(aliases.stream()
            .map(Alias::getSql));
    }

    @Override
    public String getDebugSql() {
        return getString(aliases.stream()
                .map(Alias::getDebugSql));
    }

    private String getString(Stream<String> stringStream) {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT ");
        if (selectAll) {
            return builder.append("*")
                    .toString();
        }
        if (!aliases.isEmpty()) {
            return builder.append(
                    stringStream
                        .collect(Collectors.joining(" , "))
            ).toString();
        }
        return builder.append(
                String.join(" , ", columns)
        ).toString();
    }
}
