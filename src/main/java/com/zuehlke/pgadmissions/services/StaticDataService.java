package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType.getOpportunityTypes;
import static com.zuehlke.pgadmissions.utils.PrismWordUtils.pluralize;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.toList;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang.WordUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableMap;
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
import com.zuehlke.pgadmissions.domain.definitions.PrismFilterEntity;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismPerformanceIndicator;
import com.zuehlke.pgadmissions.domain.definitions.PrismResourceListConstraint;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.PrismYesNoUnsureResponse;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScopeSectionDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowConstraint;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntity;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.Role;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowDefinition;
import com.zuehlke.pgadmissions.mapping.ActionMapper;
import com.zuehlke.pgadmissions.mapping.CustomizationMapper;
import com.zuehlke.pgadmissions.mapping.ImportedEntityMapper;
import com.zuehlke.pgadmissions.mapping.StateMapper;
import com.zuehlke.pgadmissions.rest.representation.OpportunityCategoryRepresentation;
import com.zuehlke.pgadmissions.rest.representation.OpportunityCategoryRepresentation.OpportunityTypeRepresentation;
import com.zuehlke.pgadmissions.rest.representation.WorkflowConstraintRepresentation;
import com.zuehlke.pgadmissions.rest.representation.action.ActionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceListFilterRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceListFilterRepresentation.FilterExpressionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.workflow.RoleRepresentation;
import com.zuehlke.pgadmissions.rest.representation.workflow.WorkflowDefinitionRepresentation;
import com.zuehlke.pgadmissions.utils.TimeZoneUtils;

import uk.co.alumeni.prism.api.model.imported.ImportedEntityResponseDefinition;

@Service
@Transactional
public class StaticDataService {

    @Value("${integration.google.api.key}")
    private String googleApiKey;

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
    private ImportedEntityMapper importedEntityMapper;

    @Inject
    private StateMapper stateMapper;

    @Inject
    private CustomizationMapper customizationMapper;

    public Map<String, Object> getActions() {
        Map<String, Object> staticData = Maps.newHashMap();
        List<Action> actions = entityService.list(Action.class);
        List<ActionRepresentation> actionRepresentations = actions.stream().map(action -> actionMapper.getActionRepresentation(action.getId()))
                .collect(Collectors.toList());
        staticData.put("actions", actionRepresentations);
        return staticData;
    }

    public Map<String, Object> getStates() {
        Map<String, Object> staticData = Maps.newHashMap();
        List<State> states = entityService.list(State.class);
        staticData.put("states", states.stream().map(stateMapper::getStateRepresentationSimple).collect(Collectors.toList()));
        return staticData;
    }

    public Map<String, Object> getRoles() {
        Map<String, Object> staticData = Maps.newHashMap();
        List<Role> roles = entityService.list(Role.class);
        staticData.put("roles", roles.stream().map(r -> new RoleRepresentation(r.getId(), r.getDirectlyAssignable())).collect(toList()));
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
            groupRepresentation.put("indicators", indicators.toArray(new PrismPerformanceIndicator[indicators.size()]));
            groupRepresentation.put("cumulative", group.isCumulative());
            groups.put(group, groupRepresentation);
        }
        staticData.put("performanceIndicatorGroups", groups);
        return staticData;
    }

    public Map<String, Object> getSimpleProperties() {
        Map<String, Object> staticData = Maps.newHashMap();

        for (Class<?> enumClass : new Class[] { PrismStudyOption.class, PrismYesNoUnsureResponse.class, PrismDurationUnit.class, PrismAdvertFunction.class,
                PrismAdvertIndustry.class, PrismApplicationReserveStatus.class, PrismDisplayPropertyCategory.class, PrismImportedEntity.class, PrismFilterEntity.class,
                PrismStateGroup.class }) {
            String simpleName = enumClass.getSimpleName().replaceFirst("Prism", "");
            simpleName = WordUtils.uncapitalize(simpleName);
            staticData.put(pluralize(simpleName), enumClass.getEnumConstants());
        }

        staticData.put("timeZones", TimeZoneUtils.getInstance().getTimeZoneDefinitions());
        staticData.put("currencies", institutionService.listAvailableCurrencies());
        staticData.put("googleApiKey", googleApiKey);
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

    public Map<String, Object> getOpportunityCategories() {
        Map<String, Object> staticData = Maps.newHashMap();
        staticData.put("opportunityCategories",
                asList(PrismOpportunityCategory.values()).stream()
                        .map(oc -> new OpportunityCategoryRepresentation(oc, oc.isPublished(),
                                getOpportunityTypes(oc).stream().map(ot -> new OpportunityTypeRepresentation(ot, ot.isPublished(), ot.getTermsAndConditions()))
                                        .collect(Collectors.toList())))
                        .collect(Collectors.toList()));
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

    public Map<String, Object> getRequiredSections() {
        List<Object> sectionDefinitions = new LinkedList<>();
        for (PrismScopeSectionDefinition section : PrismScopeSectionDefinition.values()) {
            sectionDefinitions.add(ImmutableMap.of("id", section, "explanationDisplayProperty", section.getIncompleteExplanation()));
        }
        return singletonMap("requiredSections", sectionDefinitions);
    }

    public Map<String, Object> getWorkflowConstraints() {
        List<Object> constraintDefinitions = Lists.newArrayList();
        for (PrismWorkflowConstraint constraint : PrismWorkflowConstraint.values()) {
            constraintDefinitions.add(new WorkflowConstraintRepresentation().withConstraint(constraint).withMinimumPermitted(constraint.getMinimumPermitted())
                    .withMaximumPermitted(constraint.getMaximumPermitted()));
        }
        return singletonMap("workflowConstraints", constraintDefinitions);
    }

    @SuppressWarnings("unchecked")
    public <U extends ImportedEntityResponseDefinition<?>, T extends ImportedEntity<?>> Map<String, Object> getImportedEntities() {
        Map<String, Object> staticData = Maps.newHashMap();

        for (PrismImportedEntity prismImportedEntity : PrismImportedEntity.values()) {
            List<T> entities = importedEntityService.getEnabledImportedEntities(prismImportedEntity);
            List<U> entityRepresentations = entities.stream().map(entity -> (U) importedEntityMapper.getImportedEntityRepresentation(entity))
                    .collect(Collectors.toList());
            staticData.put(pluralize(prismImportedEntity.getLowerCamelName()), entityRepresentations);
        }
        return staticData;
    }

}
