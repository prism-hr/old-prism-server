package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;

import java.util.Map;

import uk.co.alumeni.prism.api.model.advert.EnumDefinition;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Program;
import com.zuehlke.pgadmissions.domain.resource.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.System;
import com.zuehlke.pgadmissions.domain.resource.department.Department;
import com.zuehlke.pgadmissions.rest.dto.InstitutionDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceOpportunityDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceParentDivisionDTO;
import com.zuehlke.pgadmissions.workflow.executors.action.ActionExecutor;
import com.zuehlke.pgadmissions.workflow.executors.action.ApplicationExecutor;
import com.zuehlke.pgadmissions.workflow.executors.action.DepartmentExecutor;
import com.zuehlke.pgadmissions.workflow.executors.action.InstitutionExecutor;
import com.zuehlke.pgadmissions.workflow.executors.action.ProgramExecutor;
import com.zuehlke.pgadmissions.workflow.executors.action.ProjectExecutor;
import com.zuehlke.pgadmissions.workflow.transition.creators.ApplicationCreator;
import com.zuehlke.pgadmissions.workflow.transition.creators.DepartmentCreator;
import com.zuehlke.pgadmissions.workflow.transition.creators.InstitutionCreator;
import com.zuehlke.pgadmissions.workflow.transition.creators.ProgramCreator;
import com.zuehlke.pgadmissions.workflow.transition.creators.ProjectCreator;
import com.zuehlke.pgadmissions.workflow.transition.creators.ResourceCreator;
import com.zuehlke.pgadmissions.workflow.transition.populators.ApplicationPopulator;
import com.zuehlke.pgadmissions.workflow.transition.populators.ResourcePopulator;
import com.zuehlke.pgadmissions.workflow.transition.processors.ApplicationProcessor;
import com.zuehlke.pgadmissions.workflow.transition.processors.ResourceProcessor;
import com.zuehlke.pgadmissions.workflow.transition.processors.postprocessors.ApplicationPostprocessor;
import com.zuehlke.pgadmissions.workflow.transition.processors.postprocessors.DepartmentPostprocessor;
import com.zuehlke.pgadmissions.workflow.transition.processors.postprocessors.ProgramPostprocessor;
import com.zuehlke.pgadmissions.workflow.transition.processors.postprocessors.ProjectPostprocessor;
import com.zuehlke.pgadmissions.workflow.transition.processors.preprocessors.ApplicationPreprocessor;

public enum PrismScope implements EnumDefinition<uk.co.alumeni.prism.enums.PrismScope> {

    SYSTEM(new PrismScopeDefinition() //
            .withResourceClass(System.class) //
            .withResourceShortCode("SM")),
    INSTITUTION(new PrismScopeDefinition() //
            .withResourceClass(Institution.class) //
            .withResourceDTOClass(InstitutionDTO.class) //
            .withResourceShortCode("IN") //
            .withResourceListCustomColumns(new PrismColumnsDefinition() //
                    .withColumn("institution", "name") //
                    .withColumn("institution", "logoImage.id")) //
            .withActionExecutor(InstitutionExecutor.class) //
            .withResourceCreator(InstitutionCreator.class)), //
    DEPARTMENT(new PrismScopeDefinition() //
            .withResourceClass(Department.class) //
            .withResourceDTOClass(ResourceParentDivisionDTO.class) //
            .withResourceShortCode("DT") //
            .withResourceListCustomColumns(new PrismColumnsDefinition() //
                    .withColumn("institution", "name") //
                    .withColumn("institution", "logoImage.id") //
                    .withColumn("department", "name")) //
            .withActionExecutor(DepartmentExecutor.class) //
            .withResourceCreator(DepartmentCreator.class) //
            .withResourcePostprocessor(DepartmentPostprocessor.class)), //
    PROGRAM(new PrismScopeDefinition() //
            .withResourceClass(Program.class) //
            .withResourceDTOClass(ResourceOpportunityDTO.class) //
            .withResourceShortCode("PM") //
            .withResourceListCustomColumns(new PrismColumnsDefinition() //
                    .withColumn("institution", "name") //
                    .withColumn("institution", "logoImage.id") //
                    .withColumn("department", "name") //
                    .withColumn("program", "name")) //
            .withActionExecutor(ProgramExecutor.class) //
            .withResourceCreator(ProgramCreator.class) //
            .withResourcePostprocessor(ProgramPostprocessor.class)), //
    PROJECT(new PrismScopeDefinition() //
            .withResourceClass(Project.class) //
            .withResourceDTOClass(ResourceOpportunityDTO.class) //
            .withResourceShortCode("PT") //
            .withResourceListCustomColumns(new PrismColumnsDefinition() //
                    .withColumn("institution", "name") //
                    .withColumn("institution", "logoImage.id") //
                    .withColumn("department", "name") //
                    .withColumn("program", "name") //
                    .withColumn("project", "name")) //
            .withActionExecutor(ProjectExecutor.class) //
            .withResourceCreator(ProjectCreator.class) //
            .withResourcePostprocessor(ProjectPostprocessor.class)), //
    APPLICATION(new PrismScopeDefinition() //
            .withResourceClass(Application.class) //
            .withResourceDTOClass(ApplicationDTO.class) //
            .withResourceShortCode("AN") //
            .withResourceListCustomColumns(new PrismColumnsDefinition() //
                    .withColumn("institution", "name") //
                    .withColumn("institution", "logoImage.id") //
                    .withColumn("department", "name") //
                    .withColumn("program", "name") //
                    .withColumn("project", "name")) //
            .withActionExecutor(ApplicationExecutor.class) //
            .withResourceCreator(ApplicationCreator.class) //
            .withResourcePersister(ApplicationPopulator.class) //
            .withResourcePreprocessor(ApplicationPreprocessor.class) //
            .withResourceProcessor(ApplicationProcessor.class) //
            .withResourcePostprocessor(ApplicationPostprocessor.class));

    private PrismScopeDefinition definition;

    private static Map<Class<? extends Resource>, PrismScope> byResourceClass = Maps.newHashMap();

    static {
        for (PrismScope scope : values()) {
            Class<?> resourceClass = scope.getResourceClass();
            if (byResourceClass.containsKey(resourceClass)) {
                throw new Error();
            }
            byResourceClass.put(scope.getResourceClass(), scope);
        }
    }

    private PrismScope(PrismScopeDefinition definition) {
        this.definition = definition;
    }

    @Override
    public uk.co.alumeni.prism.enums.PrismScope getDefinition() {
        return uk.co.alumeni.prism.enums.PrismScope.valueOf(name());
    }

    public Class<? extends Resource> getResourceClass() {
        return definition.getResourceClass();
    }

    public Class<?> getResourceDTOClass() {
        return definition.getResourceDTOClass();
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

    public Class<? extends ResourcePopulator<?>> getResourcePopulator() {
        return definition.getResourcePopulator();
    }

    public Class<? extends ResourceProcessor<?>> getResourcePreprocessor() {
        return definition.getResourcePreprocessor();
    }

    public Class<? extends ResourceProcessor<?>> getResourceProcessor() {
        return definition.getResourceProcessor();
    }

    public Class<? extends ResourceProcessor<?>> getResourcePostprocessor() {
        return definition.getResourcePostprocessor();
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

        private Class<?> resourceDTOClass;

        private String resourceShortCode;

        private PrismColumnsDefinition resourceListCustomColumns;

        private Class<? extends ActionExecutor> actionExecutor;

        private Class<? extends ResourceCreator<?>> resourceCreator;

        private Class<? extends ResourcePopulator<?>> resourcePopulator;

        private Class<? extends ResourceProcessor<?>> resourcePreprocessor;

        private Class<? extends ResourceProcessor<?>> resourceProcessor;

        private Class<? extends ResourceProcessor<?>> resourcePostprocessor;

        public Class<? extends Resource> getResourceClass() {
            return resourceClass;
        }

        public String getResourceShortCode() {
            return resourceShortCode;
        }

        public Class<?> getResourceDTOClass() {
            return resourceDTOClass;
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

        public Class<? extends ResourcePopulator<?>> getResourcePopulator() {
            return resourcePopulator;
        }

        public Class<? extends ResourceProcessor<?>> getResourcePreprocessor() {
            return resourcePreprocessor;
        }

        public Class<? extends ResourceProcessor<?>> getResourceProcessor() {
            return resourceProcessor;
        }

        public Class<? extends ResourceProcessor<?>> getResourcePostprocessor() {
            return resourcePostprocessor;
        }

        public PrismScopeDefinition withResourceClass(Class<? extends Resource> resourceClass) {
            this.resourceClass = resourceClass;
            return this;
        }

        public PrismScopeDefinition withResourceDTOClass(Class<?> resourceDTOClass) {
            this.resourceDTOClass = resourceDTOClass;
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

        public PrismScopeDefinition withResourcePersister(Class<? extends ResourcePopulator<?>> resourcePopulator) {
            this.resourcePopulator = resourcePopulator;
            return this;
        }

        public PrismScopeDefinition withResourcePreprocessor(Class<? extends ResourceProcessor<?>> resourcePreprocessor) {
            this.resourcePreprocessor = resourcePreprocessor;
            return this;
        }

        public PrismScopeDefinition withResourceProcessor(Class<? extends ResourceProcessor<?>> resourceProcessor) {
            this.resourceProcessor = resourceProcessor;
            return this;
        }

        public PrismScopeDefinition withResourcePostprocessor(Class<? extends ResourceProcessor<?>> resourcePostprocessor) {
            this.resourcePostprocessor = resourcePostprocessor;
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
