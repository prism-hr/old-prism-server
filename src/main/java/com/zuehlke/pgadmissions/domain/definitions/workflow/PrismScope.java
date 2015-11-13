package com.zuehlke.pgadmissions.domain.definitions.workflow;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocalizableDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismResourceContext;
import com.zuehlke.pgadmissions.domain.resource.*;
import com.zuehlke.pgadmissions.domain.resource.System;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.InstitutionDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceOpportunityDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceParentDTO;
import com.zuehlke.pgadmissions.workflow.executors.action.*;
import com.zuehlke.pgadmissions.workflow.transition.creators.*;
import com.zuehlke.pgadmissions.workflow.transition.populators.ApplicationPopulator;
import com.zuehlke.pgadmissions.workflow.transition.populators.ResourcePopulator;
import com.zuehlke.pgadmissions.workflow.transition.processors.ApplicationProcessor;
import com.zuehlke.pgadmissions.workflow.transition.processors.ResourceProcessor;
import com.zuehlke.pgadmissions.workflow.transition.processors.postprocessors.*;
import com.zuehlke.pgadmissions.workflow.transition.processors.preprocessors.ApplicationPreprocessor;
import jersey.repackaged.com.google.common.collect.Maps;
import uk.co.alumeni.prism.api.model.advert.EnumDefinition;

import java.time.Month;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static com.google.common.base.CaseFormat.*;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory.*;
import static com.zuehlke.pgadmissions.domain.definitions.PrismResourceContext.EMPLOYER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismResourceContext.UNIVERSITY;
import static java.time.Month.APRIL;
import static java.time.Month.OCTOBER;
import static org.apache.commons.lang.ArrayUtils.contains;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public enum PrismScope implements EnumDefinition<uk.co.alumeni.prism.enums.PrismScope>,PrismLocalizableDefinition {

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
                    .withActionExecutor(ProgramExecutor.class) //
                    .withResourceCreator(ProgramCreator.class) //
                    .withResourcePostprocessor(ProgramPostprocessor.class)), //
    PROJECT(PrismScopeCategory.OPPORTUNITY, "PT", true, //
            new PrismScopeDefinition() //
                    .withResourceClass(Project.class) //
                    .withResourceDTOClass(ResourceOpportunityDTO.class) //
                    .withActionExecutor(ProjectExecutor.class) //
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
        Set<PrismResourceContext> contexts = Sets.newHashSet();
        if (isNotEmpty(opportunityCategories)) {
            for(String categoryString : opportunityCategories.split("\\|")){
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
