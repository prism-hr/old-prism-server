package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScopeCategory.APPLICATION_CATEGORY;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScopeCategory.OPPORTUNITY_CATEGORY;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScopeCategory.ORGANIZATION_CATEGORY;

import java.util.Map;

import com.google.common.base.CaseFormat;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.system.System;
import com.zuehlke.pgadmissions.workflow.resource.creators.ApplicationCreator;
import com.zuehlke.pgadmissions.workflow.resource.creators.InstitutionCreator;
import com.zuehlke.pgadmissions.workflow.resource.creators.ProgramCreator;
import com.zuehlke.pgadmissions.workflow.resource.creators.ProjectCreator;
import com.zuehlke.pgadmissions.workflow.resource.creators.ResourceCreator;
import com.zuehlke.pgadmissions.workflow.resource.persisters.ApplicationPersister;
import com.zuehlke.pgadmissions.workflow.resource.persisters.InstitutionPersister;
import com.zuehlke.pgadmissions.workflow.resource.persisters.ProgramPersister;
import com.zuehlke.pgadmissions.workflow.resource.persisters.ProjectPersister;
import com.zuehlke.pgadmissions.workflow.resource.persisters.ResourcePersister;
import com.zuehlke.pgadmissions.workflow.resourcer.processors.ResourceProcessor;
import com.zuehlke.pgadmissions.workflow.resourcer.processors.postprocessors.ApplicationPostprocessor;
import com.zuehlke.pgadmissions.workflow.resourcer.processors.postprocessors.ProgramPostprocessor;
import com.zuehlke.pgadmissions.workflow.resourcer.processors.postprocessors.ProjectPostprocessor;
import com.zuehlke.pgadmissions.workflow.resourcer.processors.preprocessors.ApplicationPreprocessor;

public enum PrismScope {

    SYSTEM(null, System.class, "SM", null, null, null, null, null, null), //
    INSTITUTION(ORGANIZATION_CATEGORY, Institution.class, "IN", new ColumnDefinition().add("institution", "title").getAll(), null,
            InstitutionCreator.class, InstitutionPersister.class, null, null), //
    PROGRAM(OPPORTUNITY_CATEGORY, Program.class, "PM", new ColumnDefinition().add("institution", "title").add("program", "title").getAll(), null,
            ProgramCreator.class, ProgramPersister.class, null, ProgramPostprocessor.class), //
    PROJECT(OPPORTUNITY_CATEGORY, Project.class, "PT", new ColumnDefinition().add("program", "title").add("project", "title").getAll(), null,
            ProjectCreator.class, ProjectPersister.class, null, ProjectPostprocessor.class), //
    APPLICATION(APPLICATION_CATEGORY, Application.class, "AN", new ColumnDefinition().add("program", "title").add("project", "title").getAll(), null,
            ApplicationCreator.class, ApplicationPersister.class, ApplicationPreprocessor.class, ApplicationPostprocessor.class);

    private static Map<Class<? extends Resource>, PrismScope> byResourceClass = Maps.newHashMap();

    static {
        for (PrismScope scope : values()) {
            byResourceClass.put(scope.getResourceClass(), scope);
        }
    }

    private PrismScopeCategory prismScopeCategory;

    private Class<? extends Resource> resourceClass;

    private String shortCode;

    private HashMultimap<String, String> consoleListCustomColumns;

    private HashMultimap<String, String> reportListCustomColumns;

    private Class<? extends ResourceCreator> resourceCreator;

    private Class<? extends ResourcePersister> resourcePersister;

    private Class<? extends ResourceProcessor> resourcePreprocessor;

    private Class<? extends ResourceProcessor> resourcePostprocessor;

    private PrismScope(PrismScopeCategory prismScopeCategory, Class<? extends Resource> resourceClass, String shortCode,
            HashMultimap<String, String> consoleListCustomColumns, HashMultimap<String, String> reportListCustomColumns,
            Class<? extends ResourceCreator> resourceCreator, Class<? extends ResourcePersister> resourcePersister,
            Class<? extends ResourceProcessor> resourcePreprocessor, Class<? extends ResourceProcessor> resourcePostprocessor) {
        this.prismScopeCategory = prismScopeCategory;
        this.resourceClass = resourceClass;
        this.shortCode = shortCode;
        this.consoleListCustomColumns = consoleListCustomColumns;
        this.reportListCustomColumns = reportListCustomColumns;
        this.resourceCreator = resourceCreator;
        this.resourcePersister = resourcePersister;
        this.resourcePreprocessor = resourcePreprocessor;
        this.resourcePostprocessor = resourcePostprocessor;
    }

    public PrismScopeCategory getPrismScopeCategory() {
        return prismScopeCategory;
    }

    public Class<? extends Resource> getResourceClass() {
        return resourceClass;
    }

    public String getShortCode() {
        return shortCode;
    }

    public HashMultimap<String, String> getConsoleListCustomColumns() {
        return consoleListCustomColumns;
    }

    public HashMultimap<String, String> getReportListCustomColumns() {
        return reportListCustomColumns;
    }

    public Class<? extends ResourceCreator> getResourceCreator() {
        return resourceCreator;
    }

    public Class<? extends ResourcePersister> getResourcePersister() {
        return resourcePersister;
    }
    
    public Class<? extends ResourceProcessor> getResourcePreprocessor() {
        return resourcePreprocessor;
    }

    public Class<? extends ResourceProcessor> getResourcePostprocessor() {
        return resourcePostprocessor;
    }

    public static PrismScope getByResourceClass(Class<? extends Resource> resourceClass) {
        return byResourceClass.get(resourceClass);
    }

    public String getLowerCamelName() {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name());
    }

    public String getUpperCamelName() {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name());
    }

    private static class ColumnDefinition {

        private HashMultimap<String, String> definitions = HashMultimap.create();

        public ColumnDefinition add(String table, String column) {
            definitions.put(table, column);
            return this;
        }

        public HashMultimap<String, String> getAll() {
            return definitions;
        }

    }

}
