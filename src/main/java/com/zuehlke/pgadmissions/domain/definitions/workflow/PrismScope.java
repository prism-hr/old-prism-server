package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;

import java.util.Map;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Program;
import com.zuehlke.pgadmissions.domain.resource.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.resource.Resume;
import com.zuehlke.pgadmissions.domain.resource.System;
import com.zuehlke.pgadmissions.domain.resource.department.Department;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.InstitutionDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceOpportunityDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceParentDivisionDTO;
import com.zuehlke.pgadmissions.workflow.executors.action.ActionExecutor;
import com.zuehlke.pgadmissions.workflow.executors.action.ApplicationExecutor;
import com.zuehlke.pgadmissions.workflow.executors.action.DepartmentExecutor;
import com.zuehlke.pgadmissions.workflow.executors.action.InstitutionExecutor;
import com.zuehlke.pgadmissions.workflow.executors.action.ProgramExecutor;
import com.zuehlke.pgadmissions.workflow.executors.action.ProjectExecutor;
import com.zuehlke.pgadmissions.workflow.executors.action.ResumeExecutor;
import com.zuehlke.pgadmissions.workflow.transition.creators.ApplicationCreator;
import com.zuehlke.pgadmissions.workflow.transition.creators.DepartmentCreator;
import com.zuehlke.pgadmissions.workflow.transition.creators.InstitutionCreator;
import com.zuehlke.pgadmissions.workflow.transition.creators.ProgramCreator;
import com.zuehlke.pgadmissions.workflow.transition.creators.ProjectCreator;
import com.zuehlke.pgadmissions.workflow.transition.creators.ResourceCreator;
import com.zuehlke.pgadmissions.workflow.transition.creators.ResumeCreator;
import com.zuehlke.pgadmissions.workflow.transition.populators.ApplicationPopulator;
import com.zuehlke.pgadmissions.workflow.transition.populators.ResourcePopulator;
import com.zuehlke.pgadmissions.workflow.transition.populators.ResumePopulator;
import com.zuehlke.pgadmissions.workflow.transition.processors.ApplicationProcessor;
import com.zuehlke.pgadmissions.workflow.transition.processors.ResourceProcessor;
import com.zuehlke.pgadmissions.workflow.transition.processors.postprocessors.ApplicationPostprocessor;
import com.zuehlke.pgadmissions.workflow.transition.processors.postprocessors.DepartmentPostprocessor;
import com.zuehlke.pgadmissions.workflow.transition.processors.postprocessors.InstitutionPostprocessor;
import com.zuehlke.pgadmissions.workflow.transition.processors.postprocessors.ProgramPostprocessor;
import com.zuehlke.pgadmissions.workflow.transition.processors.postprocessors.ProjectPostprocessor;
import com.zuehlke.pgadmissions.workflow.transition.processors.postprocessors.ResumePostprocessor;
import com.zuehlke.pgadmissions.workflow.transition.processors.preprocessors.ApplicationPreprocessor;
import com.zuehlke.pgadmissions.workflow.transition.processors.preprocessors.ResumePreprocessor;

import jersey.repackaged.com.google.common.collect.Maps;
import uk.co.alumeni.prism.api.model.advert.EnumDefinition;

public enum PrismScope implements EnumDefinition<uk.co.alumeni.prism.enums.PrismScope> {

    SYSTEM(PrismScopeCategory.SYSTEM, "SM", //
            new PrismScopeDefinition() //
                    .withResourceClass(System.class)), //
    INSTITUTION(PrismScopeCategory.ORGANIZATION, "IN", //
            new PrismScopeDefinition() //
                    .withResourceClass(Institution.class) //
                    .withResourceDTOClass(InstitutionDTO.class) //
                    .withActionExecutor(InstitutionExecutor.class) //
                    .withResourceCreator(InstitutionCreator.class) //
                    .withResourcePostprocessor(InstitutionPostprocessor.class)), //
    DEPARTMENT(PrismScopeCategory.ORGANIZATION, "DT",
            new PrismScopeDefinition() //
                    .withResourceClass(Department.class) //
                    .withResourceDTOClass(ResourceParentDivisionDTO.class) //
                    .withActionExecutor(DepartmentExecutor.class) //
                    .withResourceCreator(DepartmentCreator.class) //
                    .withResourcePostprocessor(DepartmentPostprocessor.class)), //
    PROGRAM(PrismScopeCategory.OPPORTUNITY, "PM", 
            new PrismScopeDefinition() //
                    .withResourceClass(Program.class) //
                    .withResourceDTOClass(ResourceOpportunityDTO.class) //
                    .withActionExecutor(ProgramExecutor.class) //
                    .withResourceCreator(ProgramCreator.class) //
            .withResourcePostprocessor(ProgramPostprocessor.class)), //
    PROJECT(PrismScopeCategory.OPPORTUNITY, "PT", 
            new PrismScopeDefinition() //
                    .withResourceClass(Project.class) //
                    .withResourceDTOClass(ResourceOpportunityDTO.class) //
                    .withActionExecutor(ProjectExecutor.class) //
                    .withResourceCreator(ProjectCreator.class) //
                    .withResourcePostprocessor(ProjectPostprocessor.class)), //
    APPLICATION(PrismScopeCategory.APPLICATION, "AN", //
            new PrismScopeDefinition() //
                    .withResourceClass(Application.class) //
                    .withResourceDTOClass(ApplicationDTO.class) //
                    .withActionExecutor(ApplicationExecutor.class) //
                    .withResourceCreator(ApplicationCreator.class) //
                    .withResourcePopulator(ApplicationPopulator.class) //
                    .withResourcePreprocessor(ApplicationPreprocessor.class) //
                    .withResourceProcessor(ApplicationProcessor.class) //
                    .withResourcePostprocessor(ApplicationPostprocessor.class)), //
    RESUME(PrismScopeCategory.APPLICATION, "RE", //
            new PrismScopeDefinition() //
                    .withResourceClass(Resume.class) //
                    .withResourceDTOClass(ApplicationDTO.class) //
                    .withActionExecutor(ResumeExecutor.class) //
                    .withResourceCreator(ResumeCreator.class) //
                    .withResourcePopulator(ResumePopulator.class) //
                    .withResourcePreprocessor(ResumePreprocessor.class) //
                    .withResourcePostprocessor(ResumePostprocessor.class));

    private PrismScopeCategory scopeCategory;
    
    private String shortCode;
    
    private PrismScopeDefinition definition;

    private static Map<PrismScope, PrismScope> parentScopes = Maps.newHashMap();

    static {
        PrismScope parentScope = null;
        for (PrismScope scope : values()) {
            if (parentScope == null) {
                parentScopes.put(scope, scope);
            } else {
                parentScopes.put(scope, parentScope);
            }
            parentScope = scope;
        }
    }

    private PrismScope(PrismScopeCategory scopeCategory, String shortCode, PrismScopeDefinition definition) {
        this.scopeCategory = scopeCategory;
        this.shortCode = shortCode;
        this.definition = definition;
    }

    public PrismScopeCategory getScopeCategory() {
        return scopeCategory;
    }

    public String getShortCode() {
        return shortCode;
    }

    public Class<? extends Resource> getResourceClass() {
        return definition.getResourceClass();
    }

    public Class<?> getResourceDTOClass() {
        return definition.getResourceDTOClass();
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
    
    @Override
    public uk.co.alumeni.prism.enums.PrismScope getDefinition() {
        return uk.co.alumeni.prism.enums.PrismScope.valueOf(name());
    }

    public PrismScope getParentScope() {
        return parentScopes.get(this);
    }

    public String getLowerCamelName() {
        return UPPER_UNDERSCORE.to(LOWER_CAMEL, name());
    }

    public String getUpperCamelName() {
        return UPPER_UNDERSCORE.to(UPPER_CAMEL, name());
    }

    public boolean isResourceParentScope() {
        return ResourceParent.class.isAssignableFrom(definition.getResourceClass());
    }

    private static class PrismScopeDefinition {

        private Class<? extends Resource> resourceClass;

        private Class<?> resourceDTOClass;

        private Class<? extends ActionExecutor> actionExecutor;

        private Class<? extends ResourceCreator<?>> resourceCreator;

        private Class<? extends ResourcePopulator<?>> resourcePopulator;

        private Class<? extends ResourceProcessor<?>> resourcePreprocessor;

        private Class<? extends ResourceProcessor<?>> resourceProcessor;

        private Class<? extends ResourceProcessor<?>> resourcePostprocessor;

        public Class<? extends Resource> getResourceClass() {
            return resourceClass;
        }

        public Class<?> getResourceDTOClass() {
            return resourceDTOClass;
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

        public PrismScopeDefinition withActionExecutor(Class<? extends ActionExecutor> actionExecutor) {
            this.actionExecutor = actionExecutor;
            return this;
        }

        public PrismScopeDefinition withResourceCreator(Class<? extends ResourceCreator<?>> resourceCreator) {
            this.resourceCreator = resourceCreator;
            return this;
        }

        public PrismScopeDefinition withResourcePopulator(Class<? extends ResourcePopulator<?>> resourcePopulator) {
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

}
