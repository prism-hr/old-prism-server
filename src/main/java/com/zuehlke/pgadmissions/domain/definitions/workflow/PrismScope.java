package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;

import java.util.Map;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.resource.Department;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Program;
import com.zuehlke.pgadmissions.domain.resource.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.System;
import com.zuehlke.pgadmissions.workflow.executors.action.ActionExecutor;
import com.zuehlke.pgadmissions.workflow.executors.action.ApplicationExecutor;
import com.zuehlke.pgadmissions.workflow.executors.action.InstitutionExecutor;
import com.zuehlke.pgadmissions.workflow.executors.action.ProgramExecutor;
import com.zuehlke.pgadmissions.workflow.executors.action.ProjectExecutor;
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
import com.zuehlke.pgadmissions.workflow.transition.processors.ResourceProcessor;
import com.zuehlke.pgadmissions.workflow.transition.processors.postprocessors.ApplicationPostprocessor;
import com.zuehlke.pgadmissions.workflow.transition.processors.postprocessors.ProgramPostprocessor;
import com.zuehlke.pgadmissions.workflow.transition.processors.postprocessors.ProjectPostprocessor;
import com.zuehlke.pgadmissions.workflow.transition.processors.preprocessors.ApplicationPreprocessor;

public enum PrismScope {

    SYSTEM(new PrismScopeDefinition() //
            .withResourceClass(System.class) //
            .withResourceShortCode("SM") //
            .withSearchRepresentationBuilder(SystemSearchRepresentationBuilder.class) //
            .withSocialRepresentationBuilder(SystemSocialRepresentationBuilder.class)),
    INSTITUTION(new PrismScopeDefinition() //
            .withResourceClass(Institution.class) //
            .withResourceShortCode("IN") //
            .withResourceListCustomColumns(new PrismColumnsDefinition() //
                    .withColumn("institution", "title") //
                    .withColumn("institution", "logoImage.id")) //
            .withActionExecutor(InstitutionExecutor.class) //
            .withResourceCreator(InstitutionCreator.class) //
            .withResourcePersister(InstitutionPersister.class) //
            .withSearchRepresentationBuilder(InstitutionSearchRepresentationBuilder.class) //
            .withSocialRepresentationBuilder(ResourceParentSocialRepresentationBuilder.class)), //
    DEPARTMENT(new PrismScopeDefinition() //
            .withResourceClass(Department.class) //
            .withResourceShortCode("DT") //
            .withResourceListCustomColumns(new PrismColumnsDefinition() //
                    .withColumn("institution", "title") //
                    .withColumn("institution", "logoImage.id") //
                    .withColumn("department", "title"))), //
    PROGRAM(new PrismScopeDefinition() //
            .withResourceClass(Program.class) //
            .withResourceShortCode("PM") //
            .withResourceListCustomColumns(new PrismColumnsDefinition() //
                    .withColumn("institution", "title") //
                    .withColumn("institution", "logoImage.id") //
                    .withColumn("department", "title") //
                    .withColumn("program", "title")) //
            .withActionExecutor(ProgramExecutor.class) //
            .withResourceCreator(ProgramCreator.class) //
            .withResourcePersister(ProgramPersister.class) //
            .withResourcePostprocessor(ProgramPostprocessor.class) //
            .withSearchRepresentationBuilder(ProgramSearchRepresentationBuilder.class) //
            .withSocialRepresentationBuilder(ResourceParentSocialRepresentationBuilder.class)), //
    PROJECT(new PrismScopeDefinition() //
            .withResourceClass(Project.class) //
            .withResourceShortCode("PT") //
            .withResourceListCustomColumns(new PrismColumnsDefinition() //
                    .withColumn("institution", "title") //
                    .withColumn("institution", "logoImage.id") //
                    .withColumn("department", "title") //
                    .withColumn("program", "title") //
                    .withColumn("project", "title")) //
            .withActionExecutor(ProjectExecutor.class) //
            .withResourceCreator(ProjectCreator.class) //
            .withResourcePersister(ProjectPersister.class) //
            .withResourcePostprocessor(ProjectPostprocessor.class) //
            .withSearchRepresentationBuilder(ProjectSearchRepresentationBuilder.class) //
            .withSocialRepresentationBuilder(ResourceParentSocialRepresentationBuilder.class)), //
    APPLICATION(new PrismScopeDefinition() //
            .withResourceClass(Application.class) //
            .withResourceShortCode("AN") //
            .withResourceListCustomColumns(new PrismColumnsDefinition() //
                    .withColumn("institution", "title") //
                    .withColumn("institution", "logoImage.id") //
                    .withColumn("department", "title") //
                    .withColumn("program", "title") //
                    .withColumn("project", "title")) //
            .withActionExecutor(ApplicationExecutor.class) //
            .withResourceCreator(ApplicationCreator.class) //
            .withResourcePersister(ApplicationPersister.class) //
            .withResourcePreprocessor(ApplicationPreprocessor.class) //
            .withResourceProcessor(ApplicationProcessor.class) //
            .withResourcePostprocessor(ApplicationPostprocessor.class));

    private PrismScopeDefinition definition;

    private static Map<Class<? extends Resource>, PrismScope> byResourceClass = Maps.newHashMap();

    static {
        for (PrismScope scope : values()) {
            byResourceClass.put(scope.getResourceClass(), scope);
        }
    }

    private PrismScope(PrismScopeDefinition definition) {
        this.definition = definition;
    }

    public Class<? extends Resource> getResourceClass() {
        return definition.getResourceClass();
    }

    public String getShortCode() {
        return definition.getResourceShortCode();
    }

    public HashMultimap<String, String> getResourceListCustomColumns() {
        return definition.getResourceListCustomColumns();
    }

    public Class<? extends ActionExecutor> getActionExecutor() {
        return definition.getActionExecutor();
    }

    public Class<? extends ResourceCreator<?>> getResourceCreator() {
        return definition.getResourceCreator();
    }

    public Class<? extends ResourcePersister> getResourcePersister() {
        return definition.getResourcePersister();
    }

    public Class<? extends ResourceProcessor> getResourcePreprocessor() {
        return definition.getResourcePreprocessor();
    }

    public Class<? extends ResourceProcessor> getResourceProcessor() {
        return definition.getResourceProcessor();
    }

    public Class<? extends ResourceProcessor> getResourcePostprocessor() {
        return definition.getResourcePostprocessor();
    }

    public Class<? extends SearchRepresentationBuilder> getSearchRepresentationBuilder() {
        return definition.getSearchRepresentationBuilder();
    }

    public Class<? extends SocialRepresentationBuilder> getSocialRepresentationBuilder() {
        return definition.getSocialRepresentationBuilder();
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

    private static class PrismScopeDefinition {

        private Class<? extends Resource> resourceClass;

        private String resourceShortCode;

        private PrismColumnsDefinition resourceListCustomColumns;

        private Class<? extends ActionExecutor> actionExecutor;

        private Class<? extends ResourceCreator<?>> resourceCreator;

        private Class<? extends ResourcePersister> resourcePersister;

        private Class<? extends ResourceProcessor> resourcePreprocessor;

        private Class<? extends ResourceProcessor> resourceProcessor;

        private Class<? extends ResourceProcessor> resourcePostprocessor;

        private Class<? extends SearchRepresentationBuilder> searchRepresentationBuilder;

        private Class<? extends SocialRepresentationBuilder> socialRepresentationBuilder;

        public Class<? extends Resource> getResourceClass() {
            return resourceClass;
        }

        public String getResourceShortCode() {
            return resourceShortCode;
        }

        public HashMultimap<String, String> getResourceListCustomColumns() {
            return resourceListCustomColumns.getColumns();
        }

        public Class<? extends ActionExecutor> getActionExecutor() {
            return actionExecutor;
        }

        public Class<? extends ResourceCreator<?>> getResourceCreator() {
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

        public PrismScopeDefinition withResourceClass(Class<? extends Resource> resourceClass) {
            this.resourceClass = resourceClass;
            return this;
        }

        public PrismScopeDefinition withResourceShortCode(String resourceShortCode) {
            this.resourceShortCode = resourceShortCode;
            return this;
        }

        public PrismScopeDefinition withResourceListCustomColumns(PrismColumnsDefinition resourceListCustomColumns) {
            this.resourceListCustomColumns = resourceListCustomColumns;
            return this;
        }

        public PrismScopeDefinition withActionExecutor(Class<? extends ActionExecutor> actionExecutor) {
            this.actionExecutor = actionExecutor;
            return this;
        }

        public PrismScopeDefinition withResourceCreator(Class<? extends ResourceCreator<?>> resourceCreator) {
            this.resourceCreator = resourceCreator;
            return this;
        }

        public PrismScopeDefinition withResourcePersister(Class<? extends ResourcePersister> resourcePersister) {
            this.resourcePersister = resourcePersister;
            return this;
        }

        public PrismScopeDefinition withResourcePreprocessor(Class<? extends ResourceProcessor> resourcePreprocessor) {
            this.resourcePreprocessor = resourcePreprocessor;
            return this;
        }

        public PrismScopeDefinition withResourceProcessor(Class<? extends ResourceProcessor> resourceProcessor) {
            this.resourceProcessor = resourceProcessor;
            return this;
        }

        public PrismScopeDefinition withResourcePostprocessor(Class<? extends ResourceProcessor> resourcePostprocessor) {
            this.resourcePostprocessor = resourcePostprocessor;
            return this;
        }

        public PrismScopeDefinition withSearchRepresentationBuilder(Class<? extends SearchRepresentationBuilder> searchRepresentationBuilder) {
            this.searchRepresentationBuilder = searchRepresentationBuilder;
            return this;
        }

        public PrismScopeDefinition withSocialRepresentationBuilder(Class<? extends SocialRepresentationBuilder> socialRepresentationBuilder) {
            this.socialRepresentationBuilder = socialRepresentationBuilder;
            return this;
        }

    }

    private static class PrismColumnsDefinition {

        private HashMultimap<String, String> definitions = HashMultimap.create();

        public PrismColumnsDefinition withColumn(String table, String column) {
            definitions.put(table, column);
            return this;
        }

        public HashMultimap<String, String> getColumns() {
            return definitions;
        }

    }

}
