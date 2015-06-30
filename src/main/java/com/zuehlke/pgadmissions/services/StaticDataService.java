package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity.getPrefetchimports;
import static com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity.getResourceReportFilterProperties;
import static com.zuehlke.pgadmissions.utils.PrismWordUtils.pluralize;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang.WordUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.CaseFormat;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertFunction;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertIndustry;
import com.zuehlke.pgadmissions.domain.definitions.PrismApplicationReserveStatus;
import com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismDurationUnit;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.definitions.PrismPerformanceIndicator;
import com.zuehlke.pgadmissions.domain.definitions.PrismRefereeType;
import com.zuehlke.pgadmissions.domain.definitions.PrismResourceListContraint;
import com.zuehlke.pgadmissions.domain.definitions.PrismResourceListFilterExpression;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.PrismYesNoUnsureResponse;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCustomQuestionDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.imported.ImportedProgram;
import com.zuehlke.pgadmissions.domain.imported.mapping.ImportedEntityMapping;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.Role;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.domain.workflow.StateGroup;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowDefinition;
import com.zuehlke.pgadmissions.mappers.AdvertMapper;
import com.zuehlke.pgadmissions.mappers.ImportedEntityMapper;
import com.zuehlke.pgadmissions.mappers.ResourceMapper;
import com.zuehlke.pgadmissions.mappers.StateMapper;
import com.zuehlke.pgadmissions.rest.representation.configuration.ProgramCategoryRepresentation;
import com.zuehlke.pgadmissions.rest.representation.imported.ImportedEntitySimpleRepresentation;
import com.zuehlke.pgadmissions.rest.representation.imported.ImportedInstitutionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.imported.ImportedProgramRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceListFilterRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceListFilterRepresentation.FilterExpressionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.state.StateRepresentationSimple;
import com.zuehlke.pgadmissions.rest.representation.workflow.ActionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.workflow.WorkflowDefinitionRepresentation;
import com.zuehlke.pgadmissions.utils.TimeZoneUtils;

@Service
@Transactional
public class StaticDataService {

    @Value("${integration.google.api.key}")
    private String googleApiKey;

    @Value("${system.minimum.wage}")
    private BigDecimal systemMinimumWage;

    private ToIdFunction toIdFunction = new ToIdFunction();
    
    @Inject
    private CustomizationService customizationService;

    @Inject
    private DepartmentService departmentService;

    @Inject
    private EntityService entityService;

    @Inject
    private ImportedEntityService importedEntityService;

    @Inject
    private InstitutionService institutionService;

    @Inject
    private AdvertMapper advertMapper;
    
    @Inject
    private ImportedEntityMapper importedEntityMapper;

    @Inject
    private StateMapper stateMapper;

    @Inject
    private ResourceMapper resourceMapper;

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
        List<StateRepresentationSimple> stateRepresentations = Lists.newArrayListWithExpectedSize(states.size());
        for (State state : states) {
            stateRepresentations.add(stateMapper.getStateRepresentationSimple(state));
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
        staticData.put("institutionDomiciles", advertMapper.getAdvertDomicileRepresentations());
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

        for (Class<?> enumClass : new Class[] { PrismOpportunityType.class, PrismStudyOption.class, PrismYesNoUnsureResponse.class, PrismDurationUnit.class,
                PrismAdvertFunction.class, PrismAdvertIndustry.class, PrismRefereeType.class, PrismApplicationReserveStatus.class,
                PrismDisplayPropertyCategory.class, PrismImportedEntity.class }) {
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

        List<ResourceListFilterRepresentation> filters = Lists.newArrayListWithCapacity(PrismResourceListContraint.values().length);
        for (PrismResourceListContraint filterProperty : PrismResourceListContraint.values()) {
            List<FilterExpressionRepresentation> filterExpressions = Lists.newArrayList();
            for (PrismResourceListFilterExpression filterExpression : filterProperty.getPermittedExpressions()) {
                filterExpressions.add(new FilterExpressionRepresentation(filterExpression, filterExpression.isNegatable()));
            }
            filters.add(new ResourceListFilterRepresentation(filterProperty, filterExpressions, filterProperty.getPropertyType(), filterProperty
                    .getPermittedScopes()));
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

    @Cacheable("importedInstitutionData")
    public <T extends ImportedEntity<V>, V extends ImportedEntityMapping<T>> Map<String, Object> getInstitutionData(Integer institutionId) {
        Map<String, Object> staticData = Maps.newHashMap();

        Institution institution = entityService.getById(Institution.class, institutionId);

        for (PrismImportedEntity prismImportedEntity : getPrefetchimports()) {
            List<T> entities = importedEntityService.getEnabledImportedEntities(institution, prismImportedEntity);
            List<ImportedEntitySimpleRepresentation> entityRepresentations = Lists.newArrayListWithExpectedSize(entities.size());
            for (T entity : entities) {
                entityRepresentations.add(importedEntityMapper.getImportedEntityRepresentation(entity));
            }
            staticData.put(pluralize(prismImportedEntity.getLowerCamelName()), entityRepresentations);
        }

        staticData.put("institution", resourceMapper.getResourceRepresentationSimple(institution));
        staticData.put("departments", departmentService.getDepartments(institutionId));
        staticData.put("resourceReportFilterProperties", getResourceReportFilterProperties());
        return staticData;
    }

    public List<ImportedInstitutionRepresentation> getImportedInstitutions(Integer institutionId, Integer domicileId) {
        Institution institution = institutionService.getById(institutionId);
        ImportedEntitySimple domicile = entityService.getById(ImportedEntitySimple.class, domicileId);
        List<ImportedInstitution> importedInstitutions = importedEntityService.getEnabledImportedInstitutions(institution, domicile);

        List<ImportedInstitutionRepresentation> representations = Lists.newArrayListWithCapacity(importedInstitutions.size());
        for (ImportedInstitution importedInstitution : importedInstitutions) {
            representations.add(importedEntityMapper.getImportedInstitutionRepresentation(importedInstitution));
        }

        return representations;
    }

    public List<ImportedProgramRepresentation> getImportedPrograms(Integer institutionId, Integer importedInstitutionId) {
        Institution institution = institutionService.getById(institutionId);
        ImportedInstitution importedInstitution = entityService.getById(ImportedInstitution.class, importedInstitutionId);
        List<ImportedProgram> importedprograms = importedEntityService.getEnabledImportedPrograms(institution, importedInstitution);

        List<ImportedProgramRepresentation> representations = Lists.newArrayListWithCapacity(importedprograms.size());
        for (ImportedProgram importedProgram : importedprograms) {
            representations.add(importedEntityMapper.getImportedProgramRepresentation(importedProgram));
        }

        return representations;
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
