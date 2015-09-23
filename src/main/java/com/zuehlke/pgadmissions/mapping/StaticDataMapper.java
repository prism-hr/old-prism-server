package com.zuehlke.pgadmissions.mapping;

import static com.google.common.collect.Lists.newLinkedList;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType.getOpportunityTypes;
import static com.zuehlke.pgadmissions.utils.PrismWordUtils.pluralize;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.apache.commons.lang.WordUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertFunction;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertIndustry;
import com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismDurationUnit;
import com.zuehlke.pgadmissions.domain.definitions.PrismFilterEntity;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismPerformanceIndicator;
import com.zuehlke.pgadmissions.domain.definitions.PrismScopeCreation;
import com.zuehlke.pgadmissions.domain.definitions.PrismScopeCreation.PrismScopeCreationFamilies;
import com.zuehlke.pgadmissions.domain.definitions.PrismResourceListConstraint;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.PrismYesNoUnsureResponse;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScopeSectionDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowConstraint;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.Role;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowDefinition;
import com.zuehlke.pgadmissions.rest.representation.FilterEntityRepresentation;
import com.zuehlke.pgadmissions.rest.representation.OpportunityCategoryRepresentation;
import com.zuehlke.pgadmissions.rest.representation.OpportunityCategoryRepresentation.OpportunityTypeRepresentation;
import com.zuehlke.pgadmissions.rest.representation.WorkflowConstraintRepresentation;
import com.zuehlke.pgadmissions.rest.representation.action.ActionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceFamilyCreationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceFamilyCreationRepresentation.ResourceCreationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceListFilterRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceListFilterRepresentation.FilterExpressionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.workflow.RoleRepresentation;
import com.zuehlke.pgadmissions.rest.representation.workflow.WorkflowDefinitionRepresentation;
import com.zuehlke.pgadmissions.services.CustomizationService;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.InstitutionService;
import com.zuehlke.pgadmissions.utils.TimeZoneUtils;

@Service
@Transactional
public class StaticDataMapper {

    @Value("${integration.google.api.key}")
    private String googleApiKey;

    @Inject
    private CustomizationService customizationService;

    @Inject
    private EntityService entityService;

    @Inject
    private InstitutionService institutionService;

    @Inject
    private ActionMapper actionMapper;

    @Inject
    private StateMapper stateMapper;

    @Inject
    private CustomizationMapper customizationMapper;

    @Cacheable("staticData")
    @RequestMapping(method = RequestMethod.GET)
    public Map<String, Object> getData() {
        Map<String, Object> staticData = Maps.newHashMap();
        staticData.putAll(getActions());
        staticData.putAll(getStates());
        staticData.putAll(getRoles());
        staticData.putAll(getPerformanceIndicatorGroups());
        staticData.putAll(getReportFilterEntities());
        staticData.putAll(getSimpleProperties());
        staticData.putAll(getFilterProperties());
        staticData.putAll(getConfigurations());
        staticData.putAll(getOpportunityCategories());
        staticData.putAll(getActionConditions());
        staticData.putAll(getRequiredSections());
        staticData.putAll(getWorkflowConstraints());
        staticData.putAll(getResourceFamilyCreations());
        return staticData;
    }

    private Map<String, Object> getActions() {
        Map<String, Object> staticData = Maps.newHashMap();
        List<Action> actions = entityService.list(Action.class);
        List<ActionRepresentation> actionRepresentations = actions.stream().map(action -> actionMapper.getActionRepresentation(action.getId()))
                .collect(Collectors.toList());
        staticData.put("actions", actionRepresentations);
        return staticData;
    }

    private Map<String, Object> getStates() {
        Map<String, Object> staticData = Maps.newHashMap();
        List<State> states = entityService.list(State.class);
        staticData.put("states", states.stream().map(stateMapper::getStateRepresentationSimple).collect(Collectors.toList()));
        return staticData;
    }

    private Map<String, Object> getRoles() {
        Map<String, Object> staticData = Maps.newHashMap();
        List<Role> roles = entityService.list(Role.class);
        staticData.put("roles", roles.stream().map(r -> new RoleRepresentation(r.getId(), r.getDirectlyAssignable())).collect(toList()));
        return staticData;
    }

    private Map<String, Object> getPerformanceIndicatorGroups() {
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

    private Map<String, Object> getReportFilterEntities() {
        return Collections.singletonMap("reportFilterEntities",
                Stream.of(PrismFilterEntity.values()).map(e -> new FilterEntityRepresentation().withId(e).withScope(e.getFilterScope())).collect(Collectors.toList()));
    }

    private Map<String, Object> getSimpleProperties() {
        Map<String, Object> staticData = Maps.newHashMap();

        for (Class<?> enumClass : new Class[] { PrismStudyOption.class, PrismYesNoUnsureResponse.class, PrismDurationUnit.class, PrismAdvertFunction.class,
                PrismAdvertIndustry.class, PrismDisplayPropertyCategory.class, PrismFilterEntity.class, PrismStateGroup.class }) {
            String simpleName = enumClass.getSimpleName().replaceFirst("Prism", "");
            simpleName = WordUtils.uncapitalize(simpleName);
            staticData.put(pluralize(simpleName), enumClass.getEnumConstants());
        }

        staticData.put("timeZones", TimeZoneUtils.getInstance().getTimeZoneDefinitions());
        staticData.put("currencies", institutionService.getAvailableCurrencies());
        staticData.put("googleApiKey", googleApiKey);
        return staticData;
    }

    private Map<String, Object> getFilterProperties() {
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

    private Map<String, Object> getConfigurations() {
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

    private Map<String, Object> getOpportunityCategories() {
        Map<String, Object> staticData = Maps.newHashMap();
        staticData.put("opportunityCategories",
                asList(PrismOpportunityCategory.values()).stream()
                        .map(oc -> new OpportunityCategoryRepresentation(oc, oc.isPublished(),
                                getOpportunityTypes(oc).stream().map(ot -> new OpportunityTypeRepresentation(ot, ot.isPublished(), ot.getTermsAndConditions()))
                                        .collect(Collectors.toList())))
                        .collect(Collectors.toList()));
        return staticData;
    }

    private Map<String, Object> getActionConditions() {
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

    private Map<String, Object> getRequiredSections() {
        List<Object> sectionDefinitions = Lists.newLinkedList();
        for (PrismScopeSectionDefinition section : PrismScopeSectionDefinition.values()) {
            sectionDefinitions.add(ImmutableMap.of("id", section, "explanationDisplayProperty", section.getIncompleteExplanation()));
        }
        return singletonMap("requiredSections", sectionDefinitions);
    }

    private Map<String, Object> getWorkflowConstraints() {
        List<Object> constraintDefinitions = Lists.newArrayList();
        for (PrismWorkflowConstraint constraint : PrismWorkflowConstraint.values()) {
            constraintDefinitions.add(new WorkflowConstraintRepresentation().withConstraint(constraint).withMinimumPermitted(constraint.getMinimumPermitted())
                    .withMaximumPermitted(constraint.getMaximumPermitted()));
        }
        return singletonMap("workflowConstraints", constraintDefinitions);
    }

    private Map<String, Object> getResourceFamilyCreations() {
        List<ResourceFamilyCreationRepresentation> representations = Lists.newLinkedList();
        for (PrismScopeCreation resourceFamilyCreation : PrismScopeCreation.values()) {
            ResourceFamilyCreationRepresentation representation = new ResourceFamilyCreationRepresentation(resourceFamilyCreation);

            Map<PrismScope, Integer> occurrences = Maps.newHashMap();
            Map<PrismScope, ResourceCreationRepresentation> scopeRepresentations = Maps.newLinkedHashMap();
            PrismScopeCreationFamilies scopeCreationFamilies = resourceFamilyCreation.getScopeCreationFamilies();
            scopeCreationFamilies.forEach(scf -> {
                scf.forEach(s -> {
                    Integer frequency = occurrences.get(s);
                    frequency = frequency == null ? 1 : (frequency + 1);
                    occurrences.put(s, frequency);
                    scopeRepresentations.put(s, new ResourceCreationRepresentation(s));
                });
            });

            Integer scopeCreationFamilySize = scopeCreationFamilies.size();
            occurrences.keySet().forEach(o -> {
                if (occurrences.get(o).equals(scopeCreationFamilySize)) {
                    scopeRepresentations.get(o).setRequired(true);
                }
            });

            representation.setResourceCreations(newLinkedList(scopeRepresentations.values()));
            representations.add(representation);
        }

        return singletonMap("resourceFamilyCreations", representations);
    }

}
