package uk.co.alumeni.prism.domain.definitions.workflow;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;
import static java.time.Month.APRIL;
import static java.time.Month.OCTOBER;
import static org.apache.commons.lang.ArrayUtils.contains;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static uk.co.alumeni.prism.domain.definitions.PrismOpportunityCategory.EXPERIENCE;
import static uk.co.alumeni.prism.domain.definitions.PrismOpportunityCategory.PERSONAL_DEVELOPMENT;
import static uk.co.alumeni.prism.domain.definitions.PrismOpportunityCategory.STUDY;
import static uk.co.alumeni.prism.domain.definitions.PrismOpportunityCategory.WORK;
import static uk.co.alumeni.prism.domain.definitions.PrismResourceContext.EMPLOYER;
import static uk.co.alumeni.prism.domain.definitions.PrismResourceContext.UNIVERSITY;

import java.time.Month;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jersey.repackaged.com.google.common.collect.Maps;
import uk.co.alumeni.prism.api.model.advert.EnumDefinition;
import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition;
import uk.co.alumeni.prism.domain.definitions.PrismLocalizableDefinition;
import uk.co.alumeni.prism.domain.definitions.PrismOpportunityCategory;
import uk.co.alumeni.prism.domain.definitions.PrismResourceContext;
import uk.co.alumeni.prism.domain.resource.Department;
import uk.co.alumeni.prism.domain.resource.Institution;
import uk.co.alumeni.prism.domain.resource.Program;
import uk.co.alumeni.prism.domain.resource.Project;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.resource.ResourceParent;
import uk.co.alumeni.prism.domain.resource.System;
import uk.co.alumeni.prism.rest.dto.application.ApplicationDTO;
import uk.co.alumeni.prism.rest.dto.resource.InstitutionDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceOpportunityDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceParentDTO;
import uk.co.alumeni.prism.workflow.executors.action.ActionExecutor;
import uk.co.alumeni.prism.workflow.executors.action.ApplicationExecutor;
import uk.co.alumeni.prism.workflow.executors.action.DepartmentExecutor;
import uk.co.alumeni.prism.workflow.executors.action.InstitutionExecutor;
import uk.co.alumeni.prism.workflow.executors.action.ResourceOpportunityExecutor;
import uk.co.alumeni.prism.workflow.executors.action.SystemExecutor;
import uk.co.alumeni.prism.workflow.transition.creators.ApplicationCreator;
import uk.co.alumeni.prism.workflow.transition.creators.DepartmentCreator;
import uk.co.alumeni.prism.workflow.transition.creators.InstitutionCreator;
import uk.co.alumeni.prism.workflow.transition.creators.ProgramCreator;
import uk.co.alumeni.prism.workflow.transition.creators.ProjectCreator;
import uk.co.alumeni.prism.workflow.transition.creators.ResourceCreator;
import uk.co.alumeni.prism.workflow.transition.populators.ApplicationPopulator;
import uk.co.alumeni.prism.workflow.transition.populators.ResourcePopulator;
import uk.co.alumeni.prism.workflow.transition.processors.ApplicationProcessor;
import uk.co.alumeni.prism.workflow.transition.processors.ResourceProcessor;
import uk.co.alumeni.prism.workflow.transition.processors.postprocessors.ApplicationPostprocessor;
import uk.co.alumeni.prism.workflow.transition.processors.postprocessors.DepartmentPostprocessor;
import uk.co.alumeni.prism.workflow.transition.processors.postprocessors.InstitutionPostprocessor;
import uk.co.alumeni.prism.workflow.transition.processors.postprocessors.ProgramPostprocessor;
import uk.co.alumeni.prism.workflow.transition.processors.postprocessors.ProjectPostprocessor;
import uk.co.alumeni.prism.workflow.transition.processors.preprocessors.ApplicationPreprocessor;

import com.google.common.collect.Sets;

public enum PrismScope implements EnumDefinition<uk.co.alumeni.prism.enums.PrismScope>, PrismLocalizableDefinition {

    SYSTEM(PrismScopeCategory.SYSTEM, "SM", true, //
            new PrismScopeDefinition() //
                    .withResourceClass(System.class) //
                    .withActionExecutor(SystemExecutor.class)), //
    INSTITUTION(PrismScopeCategory.ORGANIZATION, "IN", true, //
            new PrismScopeDefinition() //
                    .withResourceClass(Institution.class) //
                    .withResourceDTOClass(InstitutionDTO.class) //
                    .withActionExecutor(InstitutionExecutor.class) //
                    .withResourceCreator(InstitutionCreator.class) //
                    .withResourcePostprocessor(InstitutionPostprocessor.class)), //
    DEPARTMENT(PrismScopeCategory.ORGANIZATION, "DT", true, //
            new PrismScopeDefinition() //
                    .withResourceClass(Department.class) //
                    .withResourceDTOClass(ResourceParentDTO.class) //
                    .withActionExecutor(DepartmentExecutor.class) //
                    .withResourceCreator(DepartmentCreator.class) //
                    .withResourcePostprocessor(DepartmentPostprocessor.class)), //
    PROGRAM(PrismScopeCategory.OPPORTUNITY, "PM", true, //
            new PrismScopeDefinition() //
                    .withResourceClass(Program.class) //
                    .withResourceDTOClass(ResourceOpportunityDTO.class) //
                    .withActionExecutor(ResourceOpportunityExecutor.class) //
                    .withResourceCreator(ProgramCreator.class) //
                    .withResourcePostprocessor(ProgramPostprocessor.class)), //
    PROJECT(PrismScopeCategory.OPPORTUNITY, "PT", true, //
            new PrismScopeDefinition() //
                    .withResourceClass(Project.class) //
                    .withResourceDTOClass(ResourceOpportunityDTO.class) //
                    .withActionExecutor(ResourceOpportunityExecutor.class) //
                    .withResourceCreator(ProjectCreator.class) //
                    .withResourcePostprocessor(ProjectPostprocessor.class)), //
    APPLICATION(PrismScopeCategory.APPLICATION, "AN", false, //
            new PrismScopeDefinition() //
                    .withResourceClass(Application.class) //
                    .withResourceDTOClass(ApplicationDTO.class) //
                    .withActionExecutor(ApplicationExecutor.class) //
                    .withResourceCreator(ApplicationCreator.class) //
                    .withResourcePopulator(ApplicationPopulator.class) //
                    .withResourcePreprocessor(ApplicationPreprocessor.class) //
                    .withResourceProcessor(ApplicationProcessor.class) //
                    .withResourcePostprocessor(ApplicationPostprocessor.class));

    private PrismScopeCategory scopeCategory;

    private String shortCode;

    private boolean defaultShared;

    private PrismScopeDefinition definition;

    private static Map<PrismScope, PrismScope> parentScopes = Maps.newHashMap();

    private static Map<Entry<PrismScope, PrismResourceContext>, PrismScopeCreationDefault> defaults = Maps.newHashMap();

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

        defaults.put(new SimpleEntry<>(INSTITUTION, UNIVERSITY), new PrismScopeCreationDefault(OCTOBER, STUDY, PERSONAL_DEVELOPMENT));
        defaults.put(new SimpleEntry<>(INSTITUTION, EMPLOYER), new PrismScopeCreationDefault(APRIL, WORK, EXPERIENCE));
        defaults.put(new SimpleEntry<>(DEPARTMENT, UNIVERSITY), new PrismScopeCreationDefault(STUDY, PERSONAL_DEVELOPMENT));
        defaults.put(new SimpleEntry<>(DEPARTMENT, EMPLOYER), new PrismScopeCreationDefault(WORK, EXPERIENCE));
    }

    private PrismScope(PrismScopeCategory scopeCategory, String shortCode, boolean defaultShared, PrismScopeDefinition definition) {
        this.scopeCategory = scopeCategory;
        this.shortCode = shortCode;
        this.defaultShared = defaultShared;
        this.definition = definition;
    }

    public PrismScopeCategory getScopeCategory() {
        return scopeCategory;
    }

    public String getShortCode() {
        return shortCode;
    }

    public boolean isDefaultShared() {
        return defaultShared;
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

    public PrismScopeCreationDefault getDefault(PrismResourceContext scopeCreation) {
        return defaults.get(new SimpleEntry<>(this, scopeCreation));
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

    public static Set<PrismResourceContext> getResourceContexts(String opportunityCategories) {
        Set<PrismResourceContext> contexts = Sets.newLinkedHashSet();
        if (isNotEmpty(opportunityCategories)) {
            for(String categoryString : opportunityCategories.split("\\|")) {
                PrismOpportunityCategory opportunityCategory = PrismOpportunityCategory.valueOf(categoryString);
                defaults.keySet().forEach(key -> {
                    if (contains(defaults.get(key).getDefaultOpportunityCategories(), opportunityCategory)) {
                        contexts.add(key.getValue());
                    }
                });
            }
        }
        return contexts;
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

    public static class PrismScopeCreationDefault {

        private Month defaultBusinessYearStartMonth;

        private PrismOpportunityCategory[] defaultOpportunityCategories;

        public PrismScopeCreationDefault(PrismOpportunityCategory... defaultOpportunityCategories) {
            this.defaultOpportunityCategories = defaultOpportunityCategories;
        }

        public PrismScopeCreationDefault(Month defaultBusinessYearStartMonth, PrismOpportunityCategory... defaultOpportunityCategories) {
            this.defaultBusinessYearStartMonth = defaultBusinessYearStartMonth;
            this.defaultOpportunityCategories = defaultOpportunityCategories;
        }

        public Month getDefaultBusinessYearStartMonth() {
            return defaultBusinessYearStartMonth;
        }

        public PrismOpportunityCategory[] getDefaultOpportunityCategories() {
            return defaultOpportunityCategories;
        }

    }

    @Override
    public PrismDisplayPropertyDefinition getDisplayProperty() {
        return PrismDisplayPropertyDefinition.valueOf("SYSTEM_SCOPE_" + this.name());
    }

}
