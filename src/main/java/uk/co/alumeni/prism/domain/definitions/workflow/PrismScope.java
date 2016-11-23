package uk.co.alumeni.prism.domain.definitions.workflow;

import uk.co.alumeni.prism.api.model.advert.EnumDefinition;
import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition;
import uk.co.alumeni.prism.domain.definitions.PrismLocalizableDefinition;
import uk.co.alumeni.prism.domain.definitions.PrismOpportunityCategory;
import uk.co.alumeni.prism.domain.definitions.PrismResourceContext;
import uk.co.alumeni.prism.domain.resource.*;
import uk.co.alumeni.prism.domain.resource.System;
import uk.co.alumeni.prism.workflow.executors.action.*;
import uk.co.alumeni.prism.workflow.transition.creators.*;
import uk.co.alumeni.prism.workflow.transition.populators.ApplicationPopulator;
import uk.co.alumeni.prism.workflow.transition.populators.ResourcePopulator;
import uk.co.alumeni.prism.workflow.transition.processors.ApplicationProcessor;
import uk.co.alumeni.prism.workflow.transition.processors.ResourceProcessor;
import uk.co.alumeni.prism.workflow.transition.processors.postprocessors.*;
import uk.co.alumeni.prism.workflow.transition.processors.preprocessors.ApplicationPreprocessor;

import java.time.Month;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static com.google.common.base.CaseFormat.*;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static java.time.Month.APRIL;
import static java.time.Month.OCTOBER;
import static org.apache.commons.lang.ArrayUtils.contains;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static uk.co.alumeni.prism.domain.definitions.PrismOpportunityCategory.*;
import static uk.co.alumeni.prism.domain.definitions.PrismResourceContext.EMPLOYER;
import static uk.co.alumeni.prism.domain.definitions.PrismResourceContext.UNIVERSITY;

public enum PrismScope implements EnumDefinition<uk.co.alumeni.prism.enums.PrismScope>, PrismLocalizableDefinition {

    SYSTEM(PrismScopeCategory.SYSTEM, "SM", true, //
            new PrismScopeDefinition() //
                    .withResourceClass(System.class) //
                    .withActionExecutor(SystemExecutor.class)), //
    INSTITUTION(PrismScopeCategory.ORGANIZATION, "IN", true, //
            new PrismScopeDefinition() //
                    .withResourceClass(Institution.class) //
                    .withActionExecutor(InstitutionExecutor.class) //
                    .withResourceCreator(InstitutionCreator.class) //
                    .withResourcePostprocessor(InstitutionPostprocessor.class)), //
    DEPARTMENT(PrismScopeCategory.ORGANIZATION, "DT", true, //
            new PrismScopeDefinition() //
                    .withResourceClass(Department.class) //
                    .withActionExecutor(DepartmentExecutor.class) //
                    .withResourceCreator(DepartmentCreator.class) //
                    .withResourcePostprocessor(DepartmentPostprocessor.class)), //
    PROGRAM(PrismScopeCategory.OPPORTUNITY, "PM", true, //
            new PrismScopeDefinition() //
                    .withResourceClass(Program.class) //
                    .withActionExecutor(ResourceOpportunityExecutor.class) //
                    .withResourceCreator(ProgramCreator.class) //
                    .withResourcePostprocessor(ProgramPostprocessor.class)), //
    PROJECT(PrismScopeCategory.OPPORTUNITY, "PT", true, //
            new PrismScopeDefinition() //
                    .withResourceClass(Project.class) //
                    .withActionExecutor(ResourceOpportunityExecutor.class) //
                    .withResourceCreator(ProjectCreator.class) //
                    .withResourcePostprocessor(ProjectPostprocessor.class)), //
    APPLICATION(PrismScopeCategory.APPLICATION, "AN", false, //
            new PrismScopeDefinition() //
                    .withResourceClass(Application.class) //
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

    private static Map<PrismScope, PrismScope> parentScopes = new HashMap<>();

    private static Map<Entry<PrismScope, PrismResourceContext>, PrismScopeCreationDefault> defaults = new HashMap<>();

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
        Set<PrismResourceContext> contexts = newLinkedHashSet();
        if (isNotEmpty(opportunityCategories)) {
            for (String categoryString : opportunityCategories.split("\\|")) {
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

    public List<PrismScope> getEnclosingScopes() {
        int thisOrdinal = this.ordinal();
        List<PrismScope> enclosingScopes = newArrayList();
        for (PrismScope scope : PrismScope.values()) {
            if (scope.ordinal() <= thisOrdinal) {
                enclosingScopes.add(scope);
            }
        }
        return enclosingScopes;
    }

    private static class PrismScopeDefinition {

        private Class<? extends Resource> resourceClass;

        private Class<? extends ActionExecutor> actionExecutor;

        private Class<? extends ResourceCreator<?>> resourceCreator;

        private Class<? extends ResourcePopulator<?>> resourcePopulator;

        private Class<? extends ResourceProcessor<?>> resourcePreprocessor;

        private Class<? extends ResourceProcessor<?>> resourceProcessor;

        private Class<? extends ResourceProcessor<?>> resourcePostprocessor;

        public Class<? extends Resource> getResourceClass() {
            return resourceClass;
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
