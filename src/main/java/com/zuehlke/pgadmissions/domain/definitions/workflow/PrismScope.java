package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScopeCategory.APPLICATION_CATEGORY;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScopeCategory.OPPORTUNITY_CATEGORY;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScopeCategory.ORGANIZATION_CATEGORY;

import java.util.Map;

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
import com.zuehlke.pgadmissions.workflow.resource.representation.ApplicationRepresentationEnricher;
import com.zuehlke.pgadmissions.workflow.resource.representation.InstitutionRepresentationEnricher;
import com.zuehlke.pgadmissions.workflow.resource.representation.ProgramRepresentationEnricher;
import com.zuehlke.pgadmissions.workflow.resource.representation.ProjectRepresentationEnricher;
import com.zuehlke.pgadmissions.workflow.resource.representation.ResourceRepresentationEnricher;
import com.zuehlke.pgadmissions.workflow.resource.seo.search.InstitutionSearchRepresentationBuilder;
import com.zuehlke.pgadmissions.workflow.resource.seo.search.ProgramSearchRepresentationBuilder;
import com.zuehlke.pgadmissions.workflow.resource.seo.search.ProjectSearchRepresentationBuilder;
import com.zuehlke.pgadmissions.workflow.resource.seo.search.SearchRepresentationBuilder;
import com.zuehlke.pgadmissions.workflow.resource.seo.search.SystemSearchRepresentationBuilder;
import com.zuehlke.pgadmissions.workflow.resource.seo.social.ResourceParentSocialRepresentationBuilder;
import com.zuehlke.pgadmissions.workflow.resource.seo.social.SocialRepresentationBuilder;
import com.zuehlke.pgadmissions.workflow.resource.seo.social.SystemSocialRepresentationBuilder;
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
import com.zuehlke.pgadmissions.workflow.transition.processors.InstitutionProcessor;
import com.zuehlke.pgadmissions.workflow.transition.processors.ProgramProcessor;
import com.zuehlke.pgadmissions.workflow.transition.processors.ProjectProcessor;
import com.zuehlke.pgadmissions.workflow.transition.processors.ResourceProcessor;
import com.zuehlke.pgadmissions.workflow.transition.processors.postprocessors.ApplicationPostprocessor;
import com.zuehlke.pgadmissions.workflow.transition.processors.postprocessors.InstitutionPostprocessor;
import com.zuehlke.pgadmissions.workflow.transition.processors.postprocessors.ProgramPostprocessor;
import com.zuehlke.pgadmissions.workflow.transition.processors.postprocessors.ProjectPostprocessor;
import com.zuehlke.pgadmissions.workflow.transition.processors.preprocessors.ApplicationPreprocessor;
import com.zuehlke.pgadmissions.workflow.transition.processors.preprocessors.ProgramPreprocessor;
import com.zuehlke.pgadmissions.workflow.transition.processors.preprocessors.ProjectPreprocessor;

public enum PrismScope {

    SYSTEM(null, System.class, "SM", null, null, null, null, null, null, null, null, SystemSearchRepresentationBuilder.class, //
            SystemSocialRepresentationBuilder.class, null), //
    INSTITUTION(ORGANIZATION_CATEGORY, Institution.class, "IN", //
            new ColumnDefinition().add("institution", "title").add("institution", "logoImage.id").getAll(), null, //
            InstitutionExecutor.class, InstitutionCreator.class, InstitutionPersister.class, InstitutionProcessor.class, InstitutionPostprocessor.class, null, //
            InstitutionSearchRepresentationBuilder.class, ResourceParentSocialRepresentationBuilder.class, InstitutionRepresentationEnricher.class), //
    PROGRAM(OPPORTUNITY_CATEGORY, Program.class, "PM", //
            new ColumnDefinition().add("institution", "title").add("institution", "logoImage.id").add("partner", "title").add("partner", "logoImage.id")
                    .add("program", "title").getAll(), null, //
            ProgramExecutor.class, ProgramCreator.class, ProgramPersister.class, ProgramPreprocessor.class, ProgramProcessor.class, ProgramPostprocessor.class, //
            ProgramSearchRepresentationBuilder.class, ResourceParentSocialRepresentationBuilder.class, ProgramRepresentationEnricher.class), //
    PROJECT(OPPORTUNITY_CATEGORY, Project.class, "PT", //
            new ColumnDefinition().add("institution", "title").add("institution", "logoImage.id").add("partner", "title").add("partner", "logoImage.id")
                    .add("program", "title").add("project", "title").getAll(), null, //
            ProjectExecutor.class, ProjectCreator.class, ProjectPersister.class, ProjectPreprocessor.class, ProjectProcessor.class, ProjectPostprocessor.class, //
            ProjectSearchRepresentationBuilder.class, ResourceParentSocialRepresentationBuilder.class, ProjectRepresentationEnricher.class), //
    APPLICATION(APPLICATION_CATEGORY, Application.class, "AN", //
            new ColumnDefinition().add("institution", "title").add("institution", "logoImage.id").add("partner", "title").add("partner", "logoImage.id")
                    .add("program", "title").add("project", "title").getAll(), null, //
            ApplicationExecutor.class, ApplicationCreator.class, ApplicationPersister.class, ApplicationPreprocessor.class, ApplicationProcessor.class, //
            ApplicationPostprocessor.class, null, null, ApplicationRepresentationEnricher.class);

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

    private Class<? extends SearchRepresentationBuilder> searchRepresentationBuilder;

    private Class<? extends SocialRepresentationBuilder> socialRepresentationBuilder;

    private Class<? extends ResourceRepresentationEnricher> resourceRepresentationEnricher;

    private PrismScope(PrismScopeCategory prismScopeCategory, Class<? extends Resource> resourceClass, String shortCode,
            HashMultimap<String, String> consoleListCustomColumns, HashMultimap<String, String> reportListCustomColumns,
            Class<? extends ActionExecutor> actionExecutor, Class<? extends ResourceCreator> resourceCreator,
            Class<? extends ResourcePersister> resourcePersister, Class<? extends ResourceProcessor> resourcePreprocessor,
            Class<? extends ResourceProcessor> resourceProcessor, Class<? extends ResourceProcessor> resourcePostprocessor,
            Class<? extends SearchRepresentationBuilder> searchRepresentationBuilder, Class<? extends SocialRepresentationBuilder> socialRepresentationBuilder,
            Class<? extends ResourceRepresentationEnricher> resourceRepresentationEnricher) {
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
        this.searchRepresentationBuilder = searchRepresentationBuilder;
        this.socialRepresentationBuilder = socialRepresentationBuilder;
        this.resourceRepresentationEnricher = resourceRepresentationEnricher;
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

    public Class<? extends SearchRepresentationBuilder> getSearchRepresentationBuilder() {
        return searchRepresentationBuilder;
    }

    public Class<? extends SocialRepresentationBuilder> getSocialRepresentationBuilder() {
        return socialRepresentationBuilder;
    }

    public Class<? extends ResourceRepresentationEnricher> getResourceRepresentationEnricher() {
        return resourceRepresentationEnricher;
    }

    public static PrismScope getByResourceClass(Class<? extends Resource> resourceClass) {
        return byResourceClass.get(resourceClass);
    }

    public String getLowerCamelName() {
        return UPPER_UNDERSCORE.to(LOWER_CAMEL, name());
    }

    public String getUpperCamelName() {
        return UPPER_UNDERSCORE.to(UPPER_CAMEL, name());
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
