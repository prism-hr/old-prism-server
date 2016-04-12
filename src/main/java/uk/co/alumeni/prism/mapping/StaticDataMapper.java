package uk.co.alumeni.prism.mapping;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;
import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang.WordUtils.uncapitalize;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.getTransientActions;
import static uk.co.alumeni.prism.utils.PrismWordUtils.pluralize;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyCategory;
import uk.co.alumeni.prism.domain.definitions.PrismDurationUnit;
import uk.co.alumeni.prism.domain.definitions.PrismFilterEntity;
import uk.co.alumeni.prism.domain.definitions.PrismGender;
import uk.co.alumeni.prism.domain.definitions.PrismPerformanceIndicator;
import uk.co.alumeni.prism.domain.definitions.PrismRejectionReason;
import uk.co.alumeni.prism.domain.definitions.PrismStudyOption;
import uk.co.alumeni.prism.domain.definitions.PrismYesNoUnsureResponse;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCondition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismConfiguration;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScopeSectionDefinition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowConstraint;
import uk.co.alumeni.prism.domain.workflow.Action;
import uk.co.alumeni.prism.domain.workflow.State;
import uk.co.alumeni.prism.domain.workflow.WorkflowDefinition;
import uk.co.alumeni.prism.rest.representation.FilterEntityRepresentation;
import uk.co.alumeni.prism.rest.representation.WorkflowConstraintRepresentation;
import uk.co.alumeni.prism.rest.representation.action.ActionRepresentation;
import uk.co.alumeni.prism.rest.representation.workflow.RoleRepresentation;
import uk.co.alumeni.prism.rest.representation.workflow.WorkflowDefinitionRepresentation;
import uk.co.alumeni.prism.services.CustomizationService;
import uk.co.alumeni.prism.services.EntityService;
import uk.co.alumeni.prism.services.InstitutionService;
import uk.co.alumeni.prism.services.RoleService;
import uk.co.alumeni.prism.utils.TimeZoneUtils;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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
    private RoleService roleService;

    @Inject
    private ActionMapper actionMapper;

    @Inject
    private PrismMapper prismMapper;

    @Inject
    private ResourceMapper resourceMapper;

    @Inject
    private ScopeMapper scopeMapper;

    @Inject
    private StateMapper stateMapper;

    @Inject
    private CustomizationMapper customizationMapper;

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
        staticData.putAll(getDomiciles());
        staticData.putAll(getEthnicities());
        staticData.putAll(getDisabilities());
        staticData.putAll(getAdvertFunctions());
        staticData.putAll(getAdvertIndustries());
        staticData.putAll(getAdvertBenefits());
        staticData.putAll(getAgeRanges());
        return staticData;
    }

    private Map<String, Object> getActions() {
        Map<String, Object> staticData = Maps.newHashMap();
        List<ActionRepresentation> actionRepresentations = getTransientActions().stream().map(action -> actionMapper.getActionRepresentation(action))
                .collect(toList());

        List<Action> actions = entityService.getAll(Action.class);
        actions.stream().map(action -> actionMapper.getActionRepresentation(action.getId())).forEach(action -> actionRepresentations.add(action));
        staticData.put("actions", actionRepresentations);
        return staticData;
    }

    private Map<String, Object> getStates() {
        Map<String, Object> staticData = Maps.newHashMap();
        List<State> states = entityService.getAll(State.class);
        staticData.put("states", states.stream().map(stateMapper::getStateRepresentationSimple).collect(Collectors.toList()));
        return staticData;
    }

    private Map<String, Object> getRoles() {
        Map<String, Object> staticData = Maps.newHashMap();
        staticData.put("roles", roleService.getRoles().stream().map(role -> new RoleRepresentation(role.getId(),
                role.getVerified(), role.getDirectlyAssignable())).collect(toList()));
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
        return Collections.singletonMap(
                "reportFilterEntities",
                Stream.of(PrismFilterEntity.values()).map(e -> new FilterEntityRepresentation().withId(e).withScope(e.getFilterScope()))
                        .collect(Collectors.toList()));
    }

    private Map<String, Object> getSimpleProperties() {
        Map<String, Object> staticData = Maps.newHashMap();

        for (Class<?> enumClass : new Class[] { PrismStudyOption.class, PrismYesNoUnsureResponse.class, PrismDurationUnit.class,
                PrismDisplayPropertyCategory.class, PrismFilterEntity.class, PrismStateGroup.class, PrismRejectionReason.class,
                PrismGender.class }) {
            String simpleName = enumClass.getSimpleName().replaceFirst("Prism", "");
            simpleName = uncapitalize(simpleName);
            staticData.put(pluralize(simpleName), enumClass.getEnumConstants());
        }

        staticData.put("timeZones", TimeZoneUtils.getInstance().getTimeZoneDefinitions());
        staticData.put("currencies", institutionService.getAvailableCurrencies());
        staticData.put("googleApiKey", googleApiKey);
        return staticData;
    }

    private Map<String, Object> getFilterProperties() {
        return singletonMap("filters", resourceMapper.getResourceListFilterRepresentations());
    }

    private Map<String, Object> getConfigurations() {
        Map<String, Object> staticData = Maps.newHashMap();

        Map<String, Object> configurations = Maps.newHashMap();
        for (PrismConfiguration prismConfiguration : PrismConfiguration.values()) {
            String name = pluralize(UPPER_UNDERSCORE.to(LOWER_CAMEL, prismConfiguration.name()));
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
        return singletonMap("opportunityCategories", prismMapper.getOpportunityTypeRepresentations());
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
            sectionDefinitions.add(ImmutableMap.of(
                    "id", section,
                    "name", section.getName(),
                    "explanation", section.getIncompleteExplanation(),
                    "scopes", PrismScopeSectionDefinition.getScopes(section)));
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
        return singletonMap("resourceFamilyCreations", scopeMapper.getResourceFamilyCreationRepresentations());
    }

    private Map<String, Object> getAgeRanges() {
        return singletonMap("ageRanges", prismMapper.getAgeRangeRepresentations());
    }

    private Map<String, Object> getDomiciles() {
        return singletonMap("domiciles", prismMapper.getDomicileRepresentations());
    }

    private Map<String, Object> getEthnicities() {
        return singletonMap("ethnicities", prismMapper.getEthnicityRepresentations());
    }

    private Map<String, Object> getDisabilities() {
        return singletonMap("disabilities", prismMapper.getDisabilityRepresentations());
    }

    private Map<String, Object> getAdvertFunctions() {
        return singletonMap("advertFunctions", prismMapper.getAdvertFunctionRepresentations());
    }

    private Map<String, Object> getAdvertIndustries() {
        return singletonMap("advertIndustries", prismMapper.getAdvertIndustryRepresentations());
    }

    private Map<String, Object> getAdvertBenefits() {
        return singletonMap("advertBenefits", prismMapper.getAdvertBenefitRepresentations());
    }

}
