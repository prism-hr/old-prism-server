package com.zuehlke.pgadmissions.services;

import com.google.common.base.CaseFormat;
import com.google.common.base.Function;
import com.google.common.collect.*;
import com.zuehlke.pgadmissions.domain.Competence;
import com.zuehlke.pgadmissions.domain.definitions.*;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.imported.ImportedProgram;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.workflow.*;
import com.zuehlke.pgadmissions.mapping.*;
import com.zuehlke.pgadmissions.rest.representation.action.ActionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.advert.AdvertCompetenceRepresentation;
import com.zuehlke.pgadmissions.rest.representation.configuration.ProgramCategoryRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceListFilterRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceListFilterRepresentation.FilterExpressionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.workflow.WorkflowDefinitionRepresentation;
import com.zuehlke.pgadmissions.utils.TimeZoneUtils;
import org.apache.commons.lang.WordUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.alumeni.prism.api.model.imported.ImportedEntityResponseDefinition;
import uk.co.alumeni.prism.api.model.imported.response.ImportedEntityResponse;
import uk.co.alumeni.prism.api.model.imported.response.ImportedProgramResponse;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity.getPrefetchEntities;
import static com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity.getResourceReportFilterProperties;
import static com.zuehlke.pgadmissions.utils.PrismWordUtils.pluralize;

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
    private EntityService entityService;

    @Inject
    private ImportedEntityService importedEntityService;

    @Inject
    private InstitutionService institutionService;

    @Inject
    private ActionMapper actionMapper;

    @Inject
    private AdvertMapper advertMapper;

    @Inject
    private ImportedEntityMapper importedEntityMapper;

    @Inject
    private StateMapper stateMapper;

    @Inject
    private ResourceMapper resourceMapper;

    @Inject
    private CustomizationMapper customizationMapper;

    public Map<String, Object> getActions() {
        Map<String, Object> staticData = Maps.newHashMap();

        List<Action> actions = entityService.list(Action.class);
        List<ActionRepresentation> actionRepresentations = actions.stream().map(action -> actionMapper.getActionRepresentation(action.getId())).collect(Collectors.toList());

        staticData.put("actions", actionRepresentations);
        return staticData;
    }

    public Map<String, Object> getStates() {
        Map<String, Object> staticData = Maps.newHashMap();

        List<State> states = entityService.list(State.class);

        staticData.put("states", states.stream().map(stateMapper::getStateRepresentationSimple).collect(Collectors.toList()));
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

        for (Class<?> enumClass : new Class[]{PrismOpportunityType.class, PrismStudyOption.class, PrismYesNoUnsureResponse.class, PrismDurationUnit.class,
                PrismAdvertFunction.class, PrismAdvertIndustry.class, PrismRefereeType.class, PrismApplicationReserveStatus.class,
                PrismDisplayPropertyCategory.class, PrismImportedEntity.class}) {
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

        List<ResourceListFilterRepresentation> filters = Lists.newArrayListWithCapacity(PrismResourceListConstraint.values().length);
        for (PrismResourceListConstraint filterProperty : PrismResourceListConstraint.values()) {
            List<FilterExpressionRepresentation> filterExpressions = filterProperty.getPermittedExpressions().stream()
                    .map(filterExpression -> new FilterExpressionRepresentation(filterExpression, filterExpression.isNegatable()))
                    .collect(Collectors.toList());
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
                    parameters.add(customizationMapper.getWorkflowDefinitionRepresentation(definition));
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

    public Map<String, Object> getAdvertCompetences() {
        List<Competence> competences = entityService.list(Competence.class);
        List<AdvertCompetenceRepresentation> competenceRepresentations = competences.stream()
                .map(competence -> new AdvertCompetenceRepresentation().withId(competence.getId()).withTitle(competence.getTitle()).withDescription(competence.getDescription()))
                .collect(Collectors.toList());
        return Collections.singletonMap("competences", competenceRepresentations);
    }

    @SuppressWarnings("unchecked")
    @Cacheable("importedInstitutionData")
    public <T extends ImportedEntity<?, ?>, U extends ImportedEntityResponseDefinition<?>> Map<String, Object> getInstitutionData(Integer institutionId) {
        Map<String, Object> staticData = Maps.newHashMap();

        Institution institution = entityService.getById(Institution.class, institutionId);

        for (PrismImportedEntity prismImportedEntity : getPrefetchEntities()) {
            List<T> entities = importedEntityService.getEnabledImportedEntities(institution, prismImportedEntity);
            List<U> entityRepresentations = entities.stream().map(entity -> (U) importedEntityMapper.getImportedEntityRepresentation(entity)).collect(Collectors.toList());
            staticData.put(pluralize(prismImportedEntity.getLowerCamelName()), entityRepresentations);
        }

        staticData.put("institution", resourceMapper.getResourceRepresentationSimple(institution));
        staticData.put("resourceReportFilterProperties", getResourceReportFilterProperties());
        return staticData;
    }

    public List<ImportedEntityResponse> getImportedInstitutions(Integer institutionId, Integer domicileId) {
        Institution institution = institutionService.getById(institutionId);
        ImportedEntitySimple domicile = entityService.getById(ImportedEntitySimple.class, domicileId);
        List<ImportedInstitution> importedInstitutions = importedEntityService.getEnabledImportedInstitutions(institution, domicile);

        return importedInstitutions.stream().map(importedEntityMapper::getImportedInstitutionSimpleRepresentation).collect(Collectors.toList());
    }

    public List<ImportedEntityResponse> getImportedPrograms(Integer institutionId, Integer importedInstitutionId) {
        Institution institution = institutionService.getById(institutionId);
        ImportedInstitution importedInstitution = entityService.getById(ImportedInstitution.class, importedInstitutionId);
        List<ImportedProgram> importedPrograms = importedEntityService.getEnabledImportedPrograms(institution, importedInstitution);

        return importedPrograms.stream().map(importedEntityMapper::getImportedProgramSimpleRepresentation).collect(Collectors.toList());
    }

    public List<ImportedProgramResponse> searchImportedPrograms(Integer institutionId, String searchQuery) {
        Institution institution = institutionService.getById(institutionId);
        List<ImportedProgram> importedPrograms = importedEntityService.getImportedPrograms(searchQuery);
        return importedPrograms.stream().map(program -> importedEntityMapper.getImportedProgramRepresentation(program, institution)).collect(Collectors.toList());
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
