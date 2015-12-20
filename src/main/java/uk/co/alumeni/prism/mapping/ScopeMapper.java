package uk.co.alumeni.prism.mapping;

import static com.google.common.collect.Lists.newLinkedList;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.PrismRoleCategory.ADMINISTRATOR;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.PrismRoleCategory.RECRUITER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.INSTITUTION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.PROGRAM;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.PROJECT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.SYSTEM;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.hibernate.criterion.Projections;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;

import jersey.repackaged.com.google.common.collect.Lists;
import jersey.repackaged.com.google.common.collect.Maps;
import uk.co.alumeni.prism.domain.definitions.PrismResourceRelationContext;
import uk.co.alumeni.prism.domain.definitions.PrismResourceRelationContext.PrismResourceRelationGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.PrismRoleCategory;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.resource.System;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.dto.ResourceActionOpportunityCategoryDTO;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRelationRepresentation;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRelationRepresentation.ResourceRelationComponentRepresentation;
import uk.co.alumeni.prism.rest.representation.user.UserActivityRepresentation.ResourceActivityRepresentation;
import uk.co.alumeni.prism.rest.representation.user.UserActivityRepresentation.ResourceActivityRepresentation.ActionActivityRepresentation;
import uk.co.alumeni.prism.services.AdvertService;
import uk.co.alumeni.prism.services.ResourceService;
import uk.co.alumeni.prism.services.SystemService;

@Service
@Transactional
public class ScopeMapper {

    @Inject
    private ActionMapper actionMapper;

    @Inject
    private AdvertService advertService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private SystemService systemService;

    public List<ResourceRelationRepresentation> getResourceFamilyCreationRepresentations() {
        List<ResourceRelationRepresentation> representations = Lists.newLinkedList();
        for (PrismResourceRelationContext relation : PrismResourceRelationContext.values()) {
            ResourceRelationRepresentation representation = new ResourceRelationRepresentation(relation);

            Map<PrismScope, Integer> occurrences = Maps.newHashMap();
            Map<PrismScope, ResourceRelationComponentRepresentation> scopeRepresentations = Maps.newLinkedHashMap();
            PrismResourceRelationGroup scopeCreationFamilies = relation.getRelations();
            scopeCreationFamilies.forEach(scf -> {
                scf.forEach(s -> {
                    PrismScope scope = s.getScope();
                    Integer frequency = occurrences.get(scope);
                    frequency = frequency == null ? 1 : (frequency + 1);
                    occurrences.put(scope, frequency);
                    scopeRepresentations.put(scope,
                            new ResourceRelationComponentRepresentation(scope, s.getAutoSuggest(), s.getDescription(), s.getUser(), s.getOpportunityCategories()));
                });
            });

            Integer scopeCreationFamilySize = scopeCreationFamilies.size();
            occurrences.keySet().forEach(o -> {
                if (occurrences.get(o).equals(scopeCreationFamilySize)) {
                    scopeRepresentations.get(o).setRequired(true);
                }
            });

            representation.setResourceRelations(newLinkedList(scopeRepresentations.values()));
            representations.add(representation);
        }
        return representations;
    }

    public List<ResourceActivityRepresentation> getResourceActivityRepresentation(User user, Map<PrismScope, PrismRoleCategory> defaultRoleCategories) {
        System system = systemService.getSystem();
        DateTime baseline = new DateTime().minusDays(1);

        List<PrismScope> scopesCreatorFor = Lists.newArrayList();
        List<PrismRoleCategory> creatorRoleCategories = Arrays.asList(ADMINISTRATOR, RECRUITER);
        for (PrismScope scope : new PrismScope[] { SYSTEM, INSTITUTION, DEPARTMENT, PROGRAM, PROJECT }) {
            if (creatorRoleCategories.contains(defaultRoleCategories.get(scope))) {
                scopesCreatorFor.add(scope);
            }
        }

        List<PrismScope> scopes = Arrays.asList(PrismScope.values());
        List<ResourceActivityRepresentation> representations = Lists.newLinkedList();
        for (PrismScope scope : scopes) {
            Set<ResourceActionOpportunityCategoryDTO> resourceActionDTOs = resourceService.getResources(user, scope, scopes.stream()
                    .filter(filterScope -> filterScope.ordinal() < scope.ordinal())
                    .collect(Collectors.toList()), //
                    advertService.getAdvertTargeterEntities(user, scope), //
                    Projections.projectionList() //
                            .add(Projections.groupProperty("resource.id").as("id")) //
                            .add(Projections.groupProperty("stateAction.action.id").as("actionId")) //
                            .add(Projections.property("stateAction.raisesUrgentFlag").as("prioritize")) //
                            .add(Projections.property("resource.updatedTimestamp").as("updatedTimestamp")) //
                            .add(Projections.property("resource.sequenceIdentifier").as("sequenceIdentifier")),
                    ResourceActionOpportunityCategoryDTO.class);

            Set<Integer> resources = Sets.newHashSet();
            Set<Integer> updatedResources = Sets.newHashSet();
            Map<PrismAction, Integer> actionCounts = Maps.newLinkedHashMap();
            for (ResourceActionOpportunityCategoryDTO resourceActionDTO : resourceActionDTOs) {
                Integer resourceId = resourceActionDTO.getId();
                resources.add(resourceId);

                if (BooleanUtils.isTrue(resourceActionDTO.getPrioritize())) {
                    PrismAction actionId = resourceActionDTO.getActionId();
                    Integer existingCount = actionCounts.get(actionId);
                    actionCounts.put(actionId, existingCount == null ? 1 : existingCount + 1);
                }

                if (resourceActionDTO.getUpdatedTimestamp().isAfter(baseline)) {
                    updatedResources.add(resourceId);
                }
            }

            List<ActionActivityRepresentation> actions = actionCounts.entrySet().stream()
                    .map(entry -> new ActionActivityRepresentation().withAction(actionMapper.getActionRepresentation(entry.getKey())).withUrgentCount(entry.getValue()))
                    .collect(Collectors.toList());

            Integer resourceForWhichCanCreateCount = 0;
            if (scope.equals(INSTITUTION) && CollectionUtils.isNotEmpty(scopesCreatorFor)) {
                resourceForWhichCanCreateCount = 1;
            } else if (isResourceCreator(scope, scopesCreatorFor)) {
                resourceForWhichCanCreateCount = resourceService.getResourcesForWhichUserCanCreateResource(system, INSTITUTION, scope).size();
            }

            representations.add(new ResourceActivityRepresentation().withScope(scope).withDefaultRoleCategory(defaultRoleCategories.get(scope))
                    .withResourceCreator(resourceForWhichCanCreateCount > 0).withCount(resources.size()).withUpdateCount(updatedResources.size()).withActions(actions));
        }

        return representations;
    }

    private boolean isResourceCreator(PrismScope actionScope, List<PrismScope> scopesCreatorFor) {
        for (PrismScope scope : PrismScope.values()) {
            if (scope.ordinal() < actionScope.ordinal() && scopesCreatorFor.contains(scope)) {
                return true;
            }
        }
        return false;
    }

}
