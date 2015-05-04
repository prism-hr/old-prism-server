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
import com.zuehlke.pgadmissions.workflow.executors.action.ActionExecutor;
import com.zuehlke.pgadmissions.workflow.executors.action.ApplicationExecutor;
import com.zuehlke.pgadmissions.workflow.executors.action.InstitutionExecutor;
import com.zuehlke.pgadmissions.workflow.executors.action.ProgramExecutor;
import com.zuehlke.pgadmissions.workflow.executors.action.ProjectExecutor;
import com.zuehlke.pgadmissions.workflow.transition.creators.ApplicationCreator;
import com.zuehlke.pgadmissions.workflow.transition.creators.InstitutionCreator;
import com.zuehlke.pgadmissions.workflow.transition.creators.ProgramCreator;
import com.zuehlke.pgadmissions.workflow.transition.creators.ProjectCreator;
import com.zuehlke.pgadmissions.workflow.transition.creators.ResourceCreator;
import com.zuehlke.pgadmissions.workflow.transition.persisters.ApplicationPersister;
import com.zuehlke.pgadmissions.workflow.transition.persisters.InstitutionPersister;
import com.zuehlke.pgadmissions.workflow.transition.persisters.ProgramPersister;
import com.zuehlke.pgadmissions.workflow.transition.persisters.ProjectPersister;
import com.zuehlke.pgadmissions.workflow.transition.persisters.ResourcePersister;
import com.zuehlke.pgadmissions.workflow.transition.processors.ApplicationProcessor;
import com.zuehlke.pgadmissions.workflow.transition.processors.ResourceProcessor;
import com.zuehlke.pgadmissions.workflow.transition.processors.postprocessors.ApplicationPostprocessor;
import com.zuehlke.pgadmissions.workflow.transition.processors.postprocessors.InstitutionPostprocessor;
import com.zuehlke.pgadmissions.workflow.transition.processors.postprocessors.ProgramPostprocessor;
import com.zuehlke.pgadmissions.workflow.transition.processors.postprocessors.ProjectPostprocessor;
import com.zuehlke.pgadmissions.workflow.transition.processors.preprocessors.ApplicationPreprocessor;

public enum PrismScope {

    SYSTEM(null, System.class, "SM", null, null, null, null, null, null, null, null), //
    INSTITUTION(ORGANIZATION_CATEGORY, Institution.class, "IN", new ColumnDefinition().add("institution", "title").getAll(), null,
            InstitutionExecutor.class, InstitutionCreator.class, InstitutionPersister.class, null, InstitutionPostprocessor.class, null), //
    PROGRAM(OPPORTUNITY_CATEGORY, Program.class, "PM", new ColumnDefinition().add("institution", "title").add("program", "title").getAll(), null,
            ProgramExecutor.class, ProgramCreator.class, ProgramPersister.class, null, null, ProgramPostprocessor.class), //
    PROJECT(OPPORTUNITY_CATEGORY, Project.class, "PT", new ColumnDefinition().add("program", "title").add("project", "title").getAll(), null,
            ProjectExecutor.class, ProjectCreator.class, ProjectPersister.class, null, null, ProjectPostprocessor.class), //
    APPLICATION(APPLICATION_CATEGORY, Application.class, "AN", new ColumnDefinition().add("program", "title").add("project", "title").getAll(), null,
            ApplicationExecutor.class, ApplicationCreator.class, ApplicationPersister.class, ApplicationPreprocessor.class, ApplicationProcessor.class,
            ApplicationPostprocessor.class);

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

    private Class<? extends ActionExecutor> actionExecutor;

    private Class<? extends ResourceCreator> resourceCreator;

    private Class<? extends ResourcePersister> resourcePersister;

    private Class<? extends ResourceProcessor> resourcePreprocessor;

    private Class<? extends ResourceProcessor> resourceProcessor;

    private Class<? extends ResourceProcessor> resourcePostprocessor;

    private PrismScope(PrismScopeCategory prismScopeCategory, Class<? extends Resource> resourceClass, String shortCode,
            HashMultimap<String, String> consoleListCustomColumns, HashMultimap<String, String> reportListCustomColumns,
            Class<? extends ActionExecutor> actionExecutor, Class<? extends ResourceCreator> resourceCreator,
            Class<? extends ResourcePersister> resourcePersister, Class<? extends ResourceProcessor> resourcePreprocessor,
            Class<? extends ResourceProcessor> resourceProcessor, Class<? extends ResourceProcessor> resourcePostprocessor) {
        this.prismScopeCategory = prismScopeCategory;
        this.resourceClass = resourceClass;
        this.shortCode = shortCode;
        this.consoleListCustomColumns = consoleListCustomColumns;
        this.reportListCustomColumns = reportListCustomColumns;
        this.actionExecutor = actionExecutor;
        this.resourceCreator = resourceCreator;
        this.resourcePersister = resourcePersister;
        this.resourcePreprocessor = resourcePreprocessor;
        this.resourceProcessor = resourceProcessor;
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

    public Class<? extends ActionExecutor> getActionExecutor() {
        return actionExecutor;
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

    public Class<? extends ResourceProcessor> getResourceProcessor() {
        return resourceProcessor;
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
