package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_ADVERT_CATEGORIES_INCOMPLETE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_ADVERT_DETAILS_INCOMPLETE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_COMPETENCES_INCOMPLETE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_DEPARTMENT_PROGRAMS_INCOMPLETE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_DETAILS_INCOMPLETE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_TARGETS_INCOMPLETE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PrismScopeRequiredSection.ADVERT_CATEGORIES;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PrismScopeRequiredSection.ADVERT_COMPETENCES;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PrismScopeRequiredSection.ADVERT_DETAILS;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PrismScopeRequiredSection.ADVERT_TARGETS;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PrismScopeRequiredSection.DEPARTMENT_PROGRAMS;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PrismScopeRequiredSection.RESOURCE_DETAILS;

import java.util.List;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
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
import com.zuehlke.pgadmissions.workflow.evaluators.DepartmentProgramsEvaluator;
import com.zuehlke.pgadmissions.workflow.evaluators.ResourceAdvertCategoriesEvaluator;
import com.zuehlke.pgadmissions.workflow.evaluators.ResourceAdvertCompetencesEvaluator;
import com.zuehlke.pgadmissions.workflow.evaluators.ResourceAdvertDetailsEvaluator;
import com.zuehlke.pgadmissions.workflow.evaluators.ResourceAdvertTargetsEvaluator;
import com.zuehlke.pgadmissions.workflow.evaluators.ResourceCompletenessEvaluator;
import com.zuehlke.pgadmissions.workflow.evaluators.ResourceDetailsEvaluator;
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

import uk.co.alumeni.prism.api.model.advert.EnumDefinition;

public enum PrismScope implements EnumDefinition<uk.co.alumeni.prism.enums.PrismScope> {

    SYSTEM(new PrismScopeDefinition() //
            .withResourceClass(System.class) //
            .withResourceShortCode("SM")), //
    INSTITUTION(new PrismScopeDefinition() //
            .withResourceClass(Institution.class) //
            .withResourceDTOClass(InstitutionDTO.class) //
            .withResourceShortCode("IN") //
            .withActionExecutor(InstitutionExecutor.class) //
            .withResourceCreator(InstitutionCreator.class) //
            .withResourcePostprocessor(InstitutionPostprocessor.class)), //
    DEPARTMENT(new PrismScopeDefinition() //
            .withResourceClass(Department.class) //
            .withResourceDTOClass(ResourceParentDivisionDTO.class) //
            .withResourceShortCode("DT") //
            .withActionExecutor(DepartmentExecutor.class) //
            .withResourceCreator(DepartmentCreator.class) //
            .withResourcePostprocessor(DepartmentPostprocessor.class)), //
    PROGRAM(new PrismScopeDefinition() //
            .withResourceClass(Program.class) //
            .withResourceDTOClass(ResourceOpportunityDTO.class) //
            .withResourceShortCode("PM") //
            .withActionExecutor(ProgramExecutor.class) //
            .withResourceCreator(ProgramCreator.class) //
            .withResourcePostprocessor(ProgramPostprocessor.class)), //
    PROJECT(new PrismScopeDefinition() //
            .withResourceClass(Project.class) //
            .withResourceDTOClass(ResourceOpportunityDTO.class) //
            .withResourceShortCode("PT") //
            .withActionExecutor(ProjectExecutor.class) //
            .withResourceCreator(ProjectCreator.class) //
            .withResourcePostprocessor(ProjectPostprocessor.class)), //
    APPLICATION(new PrismScopeDefinition() //
            .withResourceClass(Application.class) //
            .withResourceDTOClass(ApplicationDTO.class) //
            .withResourceShortCode("AN") //
            .withActionExecutor(ApplicationExecutor.class) //
            .withResourceCreator(ApplicationCreator.class) //
            .withResourcePopulator(ApplicationPopulator.class) //
            .withResourcePreprocessor(ApplicationPreprocessor.class) //
            .withResourceProcessor(ApplicationProcessor.class) //
            .withResourcePostprocessor(ApplicationPostprocessor.class)), //
    RESUME(new PrismScopeDefinition() //
            .withResourceClass(Resume.class) //
            .withResourceDTOClass(ApplicationDTO.class) //
            .withResourceShortCode("RM") //
            .withActionExecutor(ResumeExecutor.class) //
            .withResourceCreator(ResumeCreator.class) //
            .withResourcePopulator(ResumePopulator.class) //
            .withResourcePreprocessor(ResumePreprocessor.class) //
            .withResourcePostprocessor(ResumePostprocessor.class));

    private PrismScopeDefinition definition;

    private static HashMultimap<PrismScope, PrismScopeRequiredSection> requiredSections = HashMultimap.create();
    
    static {
        requiredSections.putAll(INSTITUTION, getDefaultRequiredSections());
        requiredSections.putAll(DEPARTMENT, Lists.newArrayList(RESOURCE_DETAILS, DEPARTMENT_PROGRAMS, ADVERT_DETAILS, ADVERT_CATEGORIES, ADVERT_COMPETENCES, ADVERT_TARGETS));
        requiredSections.putAll(PROGRAM, getDefaultRequiredSections());
        requiredSections.putAll(PROJECT, getDefaultRequiredSections());
    }

    private PrismScope(PrismScopeDefinition definition) {
        this.definition = definition;
    }

    @Override
    public uk.co.alumeni.prism.enums.PrismScope getDefinition() {
        return uk.co.alumeni.prism.enums.PrismScope.valueOf(name());
    }

    public Class<? extends Resource<?>> getResourceClass() {
        return definition.getResourceClass();
    }

    public Class<?> getResourceDTOClass() {
        return definition.getResourceDTOClass();
    }

    public String getShortCode() {
        return definition.getResourceShortCode();
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

    public String getLowerCamelName() {
        return UPPER_UNDERSCORE.to(LOWER_CAMEL, name());
    }

    public String getUpperCamelName() {
        return UPPER_UNDERSCORE.to(UPPER_CAMEL, name());
    }

    public Set<PrismScopeRequiredSection> getRequiredSections() {
        return requiredSections.get(this);
    }

    public boolean isResourceParentScope() {
        return ResourceParent.class.isAssignableFrom(definition.getResourceClass());
    }

    private static List<PrismScopeRequiredSection> getDefaultRequiredSections() {
        return Lists.newArrayList(RESOURCE_DETAILS, ADVERT_DETAILS, ADVERT_CATEGORIES, ADVERT_COMPETENCES, ADVERT_TARGETS);
    }

    private static class PrismScopeDefinition {

        private Class<? extends Resource<?>> resourceClass;

        private Class<?> resourceDTOClass;

        private String resourceShortCode;

        private Class<? extends ActionExecutor> actionExecutor;

        private Class<? extends ResourceCreator<?>> resourceCreator;

        private Class<? extends ResourcePopulator<?>> resourcePopulator;

        private Class<? extends ResourceProcessor<?>> resourcePreprocessor;

        private Class<? extends ResourceProcessor<?>> resourceProcessor;

        private Class<? extends ResourceProcessor<?>> resourcePostprocessor;

        public Class<? extends Resource<?>> getResourceClass() {
            return resourceClass;
        }

        public String getResourceShortCode() {
            return resourceShortCode;
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

        public PrismScopeDefinition withResourceClass(Class<? extends Resource<?>> resourceClass) {
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

    public enum PrismScopeRequiredSection {

        RESOURCE_DETAILS(ResourceDetailsEvaluator.class, SYSTEM_RESOURCE_DETAILS_INCOMPLETE), //
        DEPARTMENT_PROGRAMS(DepartmentProgramsEvaluator.class, SYSTEM_RESOURCE_DEPARTMENT_PROGRAMS_INCOMPLETE), //
        ADVERT_CATEGORIES(ResourceAdvertCategoriesEvaluator.class, SYSTEM_RESOURCE_ADVERT_CATEGORIES_INCOMPLETE), //
        ADVERT_DETAILS(ResourceAdvertDetailsEvaluator.class, SYSTEM_RESOURCE_ADVERT_DETAILS_INCOMPLETE), //
        ADVERT_COMPETENCES(ResourceAdvertCompetencesEvaluator.class, SYSTEM_RESOURCE_COMPETENCES_INCOMPLETE), //
        ADVERT_TARGETS(ResourceAdvertTargetsEvaluator.class, SYSTEM_RESOURCE_TARGETS_INCOMPLETE);

        private Class<? extends ResourceCompletenessEvaluator<?>> completenessEvaluator;

        private PrismDisplayPropertyDefinition incompleteExplanation;

        private PrismScopeRequiredSection(Class<? extends ResourceCompletenessEvaluator<?>> completenessEvaluator, PrismDisplayPropertyDefinition incompleteExplanation) {
            this.completenessEvaluator = completenessEvaluator;
            this.incompleteExplanation = incompleteExplanation;
        }

        public Class<? extends ResourceCompletenessEvaluator<?>> getCompletenessEvaluator() {
            return completenessEvaluator;
        }

        public PrismDisplayPropertyDefinition getIncompleteExplanation() {
            return incompleteExplanation;
        }

    }

}
