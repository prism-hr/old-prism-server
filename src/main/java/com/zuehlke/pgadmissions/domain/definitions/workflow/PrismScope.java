package com.zuehlke.pgadmissions.domain.definitions.workflow;

import java.util.HashMap;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.system.System;

public enum PrismScope {

    SYSTEM(System.class, 1, "SM", null, null, null), //
    INSTITUTION(Institution.class, 2, "IN", 50, new ColumnDefinition().add("institution", "title").getAll(), null), //
    PROGRAM(Program.class, 3, "PM", 50, new ColumnDefinition().add("institution", "title").add("program", "title").getAll(), null), //
    PROJECT(Project.class, 4, "PT", 50, new ColumnDefinition().add("program", "title").add("project", "title").getAll(), null), //
    APPLICATION(Application.class, 5, "AN", 50, new ColumnDefinition().add("program", "title").add("project", "title").getAll(), null);

    private Class<? extends Resource> resourceClass;

    private Integer precedence;

    private String shortCode;

    private Integer maxConsoleListRecords;

    private HashMultimap<String, String> consoleListCustomColumns;

    private HashMultimap<String, String> reportListCustomColumns;

    private static final HashMap<Class<? extends Resource>, PrismScope> resourceScopes = Maps.newHashMap();

    static {
        resourceScopes.put(System.class, SYSTEM);
        resourceScopes.put(Institution.class, INSTITUTION);
        resourceScopes.put(Program.class, PROGRAM);
        resourceScopes.put(Project.class, PROJECT);
        resourceScopes.put(Application.class, APPLICATION);
    }

    private PrismScope(Class<? extends Resource> resourceClass, int precedence, String shortCode, Integer maxConsoleListRecords,
            HashMultimap<String, String> consoleListColumns, HashMultimap<String, String> reportListColumns) {
        this.resourceClass = resourceClass;
        this.precedence = precedence;
        this.shortCode = shortCode;
        this.maxConsoleListRecords = maxConsoleListRecords;
        this.consoleListCustomColumns = consoleListColumns;
        this.reportListCustomColumns = reportListColumns;
    }

    public Class<? extends Resource> getResourceClass() {
        return resourceClass;
    }

    public Integer getPrecedence() {
        return precedence;
    }

    public String getShortCode() {
        return shortCode;
    }

    public final Integer getMaxConsoleListRecords() {
        return maxConsoleListRecords;
    }

    public final HashMultimap<String, String> getConsoleListCustomColumns() {
        return consoleListCustomColumns;
    }

    public final HashMultimap<String, String> getReportListCustomColumns() {
        return reportListCustomColumns;
    }

    public static PrismScope getResourceScope(Class<? extends Resource> resourceClass) {
        return resourceScopes.get(resourceClass);
    }

    public String getLowerCaseName() {
        return resourceClass.getSimpleName().toLowerCase();
    }

    private static class ColumnDefinition {

        private final HashMultimap<String, String> definitions = HashMultimap.create();

        public ColumnDefinition add(String table, String column) {
            definitions.put(table, column);
            return this;
        }

        public HashMultimap<String, String> getAll() {
            return definitions;
        }

    }

}
