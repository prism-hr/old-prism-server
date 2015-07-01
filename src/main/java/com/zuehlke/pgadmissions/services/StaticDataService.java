package com.zuehlke.pgadmissions.services;

import com.google.common.base.CaseFormat;
import com.google.common.base.Function;
import com.google.common.collect.*;
import com.zuehlke.pgadmissions.domain.definitions.*;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCustomQuestionDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.imported.Domicile;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.imported.ImportedLanguageQualificationType;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.workflow.*;
import com.zuehlke.pgadmissions.rest.representation.StateRepresentation;
import com.zuehlke.pgadmissions.rest.representation.configuration.ProgramCategoryRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.FilterRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.FilterRepresentation.FilterExpressionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.InstitutionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ImportedEntityRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ImportedInstitutionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.LanguageQualificationTypeRepresentation;
import com.zuehlke.pgadmissions.rest.representation.workflow.ActionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.workflow.WorkflowDefinitionRepresentation;
import com.zuehlke.pgadmissions.utils.TimeZoneUtils;
import org.apache.commons.lang.WordUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity.*;
import static com.zuehlke.pgadmissions.utils.WordUtils.pluralize;

@Service
@Transactional
public class StaticDataService {

    @Value("${integration.google.api.key}")
    private String googleApiKey;

    @Value("${system.minimum.wage}")
    private BigDecimal systemMinimumWage;

    private ToIdFunction toIdFunction = new ToIdFunction();

    @Inject
    private EntityService entityService;

    @Inject
    private ImportedEntityService importedEntityService;

    @Inject
    private InstitutionService institutionService;

    @Inject
    private CustomizationService customizationService;

    @Inject
    private Mapper mapper;

    public Map<String, Object> getActions() {
        Map<String, Object> staticData = Maps.newHashMap();

        List<Action> actions = entityService.list(Action.class);
        List<ActionRepresentation> actionRepresentations = Lists.newArrayListWithExpectedSize(actions.size());
        for (Action action : actions) {
            PrismActionCustomQuestionDefinition customQuestionDefinitionId = action.getActionCustomQuestionDefinition() != null ? action
                    .getActionCustomQuestionDefinition().getId() : null;
            actionRepresentations.add(new ActionRepresentation(action.getId(), action.getActionCategory(), customQuestionDefinitionId));
        }

        staticData.put("actions", actionRepresentations);
        return staticData;
    }

    public Map<String, Object> getStates() {
        Map<String, Object> staticData = Maps.newHashMap();

        List<State> states = entityService.list(State.class);
        List<StateRepresentation> stateRepresentations = Lists.newArrayListWithExpectedSize(states.size());
        for (State state : states) {
            stateRepresentations.add(new StateRepresentation(state.getId(), state.getStateGroup().getId()));
        }

        staticData.put("states", stateRepresentations);
        return staticData;
    }

    public Map<String, Object> getStateGroups() {
        Map<String, Object> staticData = Maps.newHashMap();
        List<StateGroup> stateGroups = entityService.list(StateGroup.class);
        staticData.put("stateGroups", Lists.newArrayList(Iterables.transform(stateGroups, toIdFunction)));
        return staticData;
    }

    public Map<String, Object> getRoles() {
        Map<String, Object> staticData = Maps.newHashMap();
        List<Role> roles = entityService.list(Role.class);
        staticData.put("roles", Lists.newArrayList(Iterables.transform(roles, toIdFunction)));
        return staticData;
    }

    public Map<String, Object> getInstitutionDomiciles() {
        Map<String, Object> staticData = Maps.newHashMap();
        staticData.put("institutionDomiciles", institutionService.getInstitutionDomiciles());
        return staticData;
    }

    public Map<String, Object> getPerformanceIndicatorGroups() {
        Map<String, Object> staticData = Maps.newHashMap();
        Map<PrismPerformanceIndicator.PrismPerformanceIndicatorGroup, Object> groups = Maps.newLinkedHashMap();
        for (PrismPerformanceIndicator.PrismPerformanceIndicatorGroup group : PrismPerformanceIndicator.PrismPerformanceIndicatorGroup.values()) {
            Map<String, Object> groupRepresentation = Maps.newHashMap();
            List<PrismPerformanceIndicator> indicators = Lists.newLinkedList();
            for (PrismPerformanceIndicator indicator : PrismPerformanceIndicator.values()) {
                if (indicator.getGroup() == group) {
                    indicators.add(indicator);
                }
            }
            groupRepresentation.put("indicators", indicators.toArray(new PrismPerformanceIndicator[0]));
            groupRepresentation.put("cumulative", group.isCumulative());
            groups.put(group, groupRepresentation);
        }
        staticData.put("performanceIndicatorGroups", groups);
        return staticData;
    }

    public Map<String, Object> getSimpleProperties() {
        Map<String, Object> staticData = Maps.newHashMap();

        for (Class<?> enumClass : new Class[]{PrismOpportunityType.class, PrismStudyOption.class,
                PrismYesNoUnsureResponse.class, PrismDurationUnit.class, PrismAdvertDomain.class,
                PrismAdvertFunction.class, PrismAdvertIndustry.class, PrismRefereeType.class,
                PrismApplicationReserveStatus.class, PrismDisplayPropertyCategory.class,
                PrismImportedEntity.class}) {
            String simpleName = enumClass.getSimpleName().replaceFirst("Prism", "");
            simpleName = WordUtils.uncapitalize(simpleName);
            staticData.put(pluralize(simpleName), enumClass.getEnumConstants());
        }

        staticData.put("timeZones", TimeZoneUtils.getInstance().getTimeZoneDefinitions());
        staticData.put("currencies", institutionService.listAvailableCurrencies());
        staticData.put("googleApiKey", googleApiKey);
        staticData.put("minimumWage", systemMinimumWage);
        return staticData;
    }

    public Map<String, Object> getFilterProperties() {
        Map<String, Object> staticData = Maps.newHashMap();

        List<FilterRepresentation> filters = Lists.newArrayListWithCapacity(PrismResourceListFilter.values().length);
        for (PrismResourceListFilter filterProperty : PrismResourceListFilter.values()) {
            List<FilterExpressionRepresentation> filterExpressions = Lists.newArrayList();
            for (PrismResourceListFilterExpression filterExpression : filterProperty.getPermittedExpressions()) {
                filterExpressions.add(new FilterExpressionRepresentation(filterExpression, filterExpression.isNegatable()));
            }
            filters.add(new FilterRepresentation(filterProperty, filterExpressions, filterProperty.getPropertyType(), filterProperty.getPermittedScopes()));
        }

        staticData.put("filters", filters);
        return staticData;
    }

    public Map<String, Object> getConfigurations() {
        Map<String, Object> staticData = Maps.newHashMap();

        Map<String, Object> configurations = Maps.newHashMap();
        for (PrismConfiguration prismConfiguration : PrismConfiguration.values()) {
            String name = pluralize(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, prismConfiguration.name()));
            Map<PrismScope, List<WorkflowDefinitionRepresentation>> scopeConfigurations = Maps.newHashMap();
            for (PrismScope prismScope : PrismScope.values()) {
                List<? extends WorkflowDefinition> definitions = customizationService.getDefinitions(prismConfiguration, prismScope);
                List<WorkflowDefinitionRepresentation> parameters = Lists.newArrayList();
                for (WorkflowDefinition definition : definitions) {
                    parameters.add(mapper.map(definition, prismConfiguration.getDefinitionRepresentationClass()));
                }
                if (!parameters.isEmpty()) {
                    scopeConfigurations.put(prismScope, parameters);
                }
            }
            configurations.put(name, scopeConfigurations);
        }

        staticData.put("workflowConfigurations", configurations);
        return staticData;
    }

    public Map<String, Object> getProgramCategories() {
        Map<String, Object> staticData = Maps.newHashMap();

        List<ProgramCategoryRepresentation> programCategories = Lists.newArrayListWithCapacity(PrismOpportunityCategory.values().length);
        for (PrismOpportunityCategory programCategory : PrismOpportunityCategory.values()) {
            ProgramCategoryRepresentation category = new ProgramCategoryRepresentation();
            category.setId(programCategory);
            category.setHasFee(programCategory.isHasFee());
            category.setHasPay(programCategory.isHasPay());
            category.setOpportunityTypes(PrismOpportunityType.getOpportunityTypes(programCategory));
            programCategories.add(category);
        }

        staticData.put("programCategories", programCategories);
        return staticData;
    }

    public Map<String, Object> getActionConditions() {
        Map<String, Object> staticData = Maps.newHashMap();

        ListMultimap<PrismScope, PrismActionCondition> actionConditionsMultimap = LinkedListMultimap.create();
        for (PrismActionCondition actionCondition : PrismActionCondition.values()) {
            for (PrismScope prismScope : actionCondition.getValidScopes()) {
                actionConditionsMultimap.put(prismScope, actionCondition);
            }
        }
        staticData.put("actionConditions", actionConditionsMultimap.asMap());
        return staticData;
    }

    @SuppressWarnings("unchecked")
    @Cacheable("importedInstitutionData")
    public Map<String, Object> getImportedData(Integer institutionId) {
        Map<String, Object> staticData = Maps.newHashMap();

        Institution institution = entityService.getById(Institution.class, institutionId);

        for (PrismImportedEntity prismImportedEntity : new PrismImportedEntity[]{AGE_RANGE, COUNTRY, DISABILITY, DOMICILE, ETHNICITY, NATIONALITY,
                QUALIFICATION_TYPE, REFERRAL_SOURCE, FUNDING_SOURCE, LANGUAGE_QUALIFICATION_TYPE, TITLE, GENDER, REJECTION_REASON,
                STUDY_OPTION, OPPORTUNITY_TYPE}) {
            Class<? extends ImportedEntity> entityClass = (Class<? extends ImportedEntity>) prismImportedEntity.getEntityClass();
            String simpleName = entityClass.getSimpleName();
            simpleName = WordUtils.uncapitalize(simpleName);
            List<? extends ImportedEntity> entities = importedEntityService.getEnabledImportedEntities(institution, entityClass);
            List<ImportedEntityRepresentation> entityRepresentations = Lists.newArrayListWithCapacity(entities.size());
            for (Object entity : entities) {
                entityRepresentations.add(mapper.map(entity, ImportedEntityRepresentation.class));
            }
            staticData.put(pluralize(simpleName), entityRepresentations);
        }

        List<ImportedLanguageQualificationType> languageQualificationTypes = importedEntityService.getEnabledImportedEntities(institution,
                ImportedLanguageQualificationType.class);
        List<LanguageQualificationTypeRepresentation> languageQualificationTypeRepresentations = Lists.newArrayListWithCapacity(languageQualificationTypes
                .size());

        for (ImportedLanguageQualificationType languageQualificationType : languageQualificationTypes) {
            languageQualificationTypeRepresentations.add(mapper.map(languageQualificationType, LanguageQualificationTypeRepresentation.class));
        }

        staticData.put("languageQualificationTypes", languageQualificationTypeRepresentations);
        staticData.put("institution", mapper.map(institution, InstitutionRepresentation.class));

        staticData.put("resourceReportFilterProperties", getResourceReportFilterProperties());
        return staticData;
    }

    public List<ImportedInstitutionRepresentation> getImportedInstitutions(Integer domicileId) {
        Domicile domicile = entityService.getById(Domicile.class, domicileId);
        List<ImportedInstitution> institutions = importedEntityService.getEnabledImportedInstitutions(domicile);

        List<ImportedInstitutionRepresentation> institutionRepresentations = Lists.newArrayListWithCapacity(institutions.size());
        for (ImportedInstitution institution : institutions) {
            institutionRepresentations.add(mapper.map(institution, ImportedInstitutionRepresentation.class));
        }

        return institutionRepresentations;
    }

    private static class ToIdFunction implements Function<WorkflowDefinition, Object> {
        @Override
        public Object apply(WorkflowDefinition input) {
            return input.getId();
        }
    }

    @SuppressWarnings("unused")
    private class EnumDefinition {

        private String id;

        private String name;

        private EnumDefinition(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

}
