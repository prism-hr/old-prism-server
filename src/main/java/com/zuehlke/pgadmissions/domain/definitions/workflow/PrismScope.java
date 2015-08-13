package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_ADVERT_CATEGORIES_HEADER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_ADVERT_CATEGORIES_INCOMPLETE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_ADVERT_HEADER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_ADVERT_INCOMPLETE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_APPLICATION_FORM_HEADER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_CLOSING_DATES_HEADER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_COMPETENCES_HEADER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_COMPETENCES_INCOMPLETE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_CONFIGURATION_HEADER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_EMAIL_TEMPLATES_HEADER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_FEES_PAYMENTS_HEADER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_MANAGE_USERS_HEADER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_STATISTICS_HEADER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_SUMMARY_HEADER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_SUMMARY_INCOMPLETE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_TARGETS_HEADER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_TARGETS_INCOMPLETE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_TIMELINE_HEADER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_TRANSLATIONS_HEADER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_USER_BOUNCES_HEADER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_WORKFLOW_HEADER;

import java.util.Map;

import uk.co.alumeni.prism.api.model.advert.EnumDefinition;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Program;
import com.zuehlke.pgadmissions.domain.resource.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.System;
import com.zuehlke.pgadmissions.domain.resource.department.Department;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.InstitutionDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceOpportunityDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceParentDivisionDTO;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSectionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSectionsRepresentation;
import com.zuehlke.pgadmissions.workflow.evaluators.ResourceAdvertCategoriesEvaluator;
import com.zuehlke.pgadmissions.workflow.evaluators.ResourceAdvertCompetencesEvaluator;
import com.zuehlke.pgadmissions.workflow.evaluators.ResourceAdvertEvaluator;
import com.zuehlke.pgadmissions.workflow.evaluators.ResourceAdvertTargetsEvaluator;
import com.zuehlke.pgadmissions.workflow.evaluators.ResourceSummaryEvaluator;
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
            .withActionExecutor(InstitutionExecutor.class) //
            .withResourceCreator(InstitutionCreator.class)), //
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
            .withResourcePersister(ApplicationPopulator.class) //
            .withResourcePreprocessor(ApplicationPreprocessor.class) //
            .withResourceProcessor(ApplicationProcessor.class) //
            .withResourcePostprocessor(ApplicationPostprocessor.class));

    private PrismScopeDefinition definition;

    private static Map<PrismScope, ResourceSectionsRepresentation> resourceSections = Maps.newHashMap();

    static {
        resourceSections.put(SYSTEM, new ResourceSectionsRepresentation() //
                .withSection(new ResourceSectionRepresentation() //
                        .withDisplayProperty(SYSTEM_RESOURCE_STATISTICS_HEADER)) //
                .withSections(getResourceConfigurationSections()));

        resourceSections.put(INSTITUTION, getResourceParentSections());
        resourceSections.put(DEPARTMENT, getResourceParentSections());
        resourceSections.put(PROGRAM, getResourceOpportunitySections());
        resourceSections.put(PROJECT, getResourceOpportunitySections());

        resourceSections.put(APPLICATION, new ResourceSectionsRepresentation() //
                .withSection(new ResourceSectionRepresentation() //
                        .withDisplayProperty(SYSTEM_RESOURCE_APPLICATION_FORM_HEADER)) //
                .withSection(new ResourceSectionRepresentation() //
                        .withDisplayProperty(SYSTEM_RESOURCE_TIMELINE_HEADER)));
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

    public ResourceSectionsRepresentation getSections() {
        return resourceSections.get(this);
    }

    private static ResourceSectionsRepresentation getResourceParentSections() {
        return getResourceParentSections(new ResourceSectionsRepresentation());
    }

    private static ResourceSectionsRepresentation getResourceOpportunitySections() {
        return getResourceParentSections(new ResourceSectionsRepresentation() //
                .withSection(new ResourceSectionRepresentation() //
                        .withDisplayProperty(SYSTEM_RESOURCE_FEES_PAYMENTS_HEADER))
                .withSection(new ResourceSectionRepresentation() //
                        .withDisplayProperty(SYSTEM_RESOURCE_CLOSING_DATES_HEADER)));
    }

    private static ResourceSectionsRepresentation getResourceParentSections(ResourceSectionsRepresentation advertSections) {
        return new ResourceSectionsRepresentation() //
                .withSection(new ResourceSectionRepresentation() //
                        .withDisplayProperty(SYSTEM_RESOURCE_SUMMARY_HEADER) //
                        .withCompletenessEvaluator(ResourceSummaryEvaluator.class) //
                        .withIncompleteExplanation(SYSTEM_RESOURCE_SUMMARY_INCOMPLETE))
                .withSection(new ResourceSectionRepresentation() //
                        .withDisplayProperty(SYSTEM_RESOURCE_ADVERT_HEADER) //
                        .withCompletenessEvaluator(ResourceAdvertEvaluator.class) //
                        .withIncompleteExplanation(SYSTEM_RESOURCE_ADVERT_INCOMPLETE) //
                        .withSubsections(new ResourceSectionsRepresentation() //
                                .withSection(new ResourceSectionRepresentation() //
                                        .withDisplayProperty(SYSTEM_RESOURCE_ADVERT_CATEGORIES_HEADER) //
                                        .withCompletenessEvaluator(ResourceAdvertCategoriesEvaluator.class) //
                                        .withIncompleteExplanation(SYSTEM_RESOURCE_ADVERT_CATEGORIES_INCOMPLETE))
                                .withSections(advertSections))) //
                .withSection(new ResourceSectionRepresentation() //
                        .withDisplayProperty(SYSTEM_RESOURCE_TARGETS_HEADER) //
                        .withCompletenessEvaluator(ResourceAdvertTargetsEvaluator.class) //
                        .withIncompleteExplanation(SYSTEM_RESOURCE_TARGETS_INCOMPLETE))
                .withSection(new ResourceSectionRepresentation() //
                        .withDisplayProperty(SYSTEM_RESOURCE_COMPETENCES_HEADER) //
                        .withCompletenessEvaluator(ResourceAdvertCompetencesEvaluator.class)
                        .withIncompleteExplanation(SYSTEM_RESOURCE_COMPETENCES_INCOMPLETE)) //
                .withSection(new ResourceSectionRepresentation() //
                        .withDisplayProperty(SYSTEM_RESOURCE_STATISTICS_HEADER)) //
                .withSection(new ResourceSectionRepresentation() //
                        .withDisplayProperty(SYSTEM_RESOURCE_TIMELINE_HEADER)) //
                .withSection(new ResourceSectionRepresentation() //
                        .withDisplayProperty(SYSTEM_RESOURCE_CONFIGURATION_HEADER) //
                        .withSubsections(getResourceConfigurationSections()));
    }

    private static ResourceSectionsRepresentation getResourceConfigurationSections() {
        return new ResourceSectionsRepresentation() //
                .withSection(new ResourceSectionRepresentation() //
                        .withDisplayProperty(SYSTEM_RESOURCE_MANAGE_USERS_HEADER)) //
                .withSection(new ResourceSectionRepresentation() //
                        .withDisplayProperty(SYSTEM_RESOURCE_USER_BOUNCES_HEADER)) //
                .withSection(new ResourceSectionRepresentation() //
                        .withDisplayProperty(SYSTEM_RESOURCE_EMAIL_TEMPLATES_HEADER)) //
                .withSection(new ResourceSectionRepresentation() //
                        .withDisplayProperty(SYSTEM_RESOURCE_TRANSLATIONS_HEADER)) //
                .withSection(new ResourceSectionRepresentation() //
                        .withDisplayProperty(SYSTEM_RESOURCE_WORKFLOW_HEADER));
    }

}
