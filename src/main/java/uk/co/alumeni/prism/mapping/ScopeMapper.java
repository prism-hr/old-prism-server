package uk.co.alumeni.prism.mapping;

import org.hibernate.criterion.Projections;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static jersey.repackaged.com.google.common.collect.Lists.newArrayList;
import static jersey.repackaged.com.google.common.collect.Maps.newHashMap;
import static jersey.repackaged.com.google.common.collect.Maps.newLinkedHashMap;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.BooleanUtils.isTrue;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.PrismRoleCategory.ADMINISTRATOR;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.PrismRoleCategory.RECRUITER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.*;

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
        List<ResourceRelationRepresentation> representations = newLinkedList();
        for (PrismResourceRelationContext relation : PrismResourceRelationContext.values()) {
            ResourceRelationRepresentation representation = new ResourceRelationRepresentation(relation);

            Map<PrismScope, Integer> occurrences = newHashMap();
            Map<PrismScope, ResourceRelationComponentRepresentation> scopeRepresentations = newLinkedHashMap();
            PrismResourceRelationGroup scopeCreationFamilies = relation.getRelations();
            scopeCreationFamilies.forEach(scf -> {
                scf.forEach(s -> {
                    PrismScope scope = s.getScope();
                    Integer frequency = occurrences.get(scope);
                    frequency = frequency == null ? 1 : (frequency + 1);
                    occurrences.put(scope, frequency);
                    scopeRepresentations.put(scope,
                            new ResourceRelationComponentRepresentation(scope, s.getAutoSuggest(), s.getDescription(), s.getUser(), s
                                    .getOpportunityCategories()));
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

        List<PrismScope> scopesCreatorFor = newArrayList();
        List<PrismRoleCategory> creatorRoleCategories = asList(ADMINISTRATOR, RECRUITER);
        for (PrismScope scope : new PrismScope[]{SYSTEM, INSTITUTION, DEPARTMENT, PROGRAM, PROJECT}) {
            if (creatorRoleCategories.contains(defaultRoleCategories.get(scope))) {
                scopesCreatorFor.add(scope);
            }
        }

        List<PrismScope> scopes = asList(PrismScope.values());
        List<ResourceActivityRepresentation> representations = newLinkedList();
        for (PrismScope scope : scopes) {
            Set<ResourceActionOpportunityCategoryDTO> resourceActions = resourceService.getResources(user, scope, scopes.stream()
                            .filter(filterScope -> filterScope.ordinal() < scope.ordinal()).collect(toList()), //
                    advertService.getAdvertTargeterEntities(user, scope), Projections.projectionList() //
                            .add(Projections.groupProperty("resource.id").as("id")) //
                            .add(Projections.groupProperty("stateAction.action.id").as("actionId")) //
                            .add(Projections.property("stateAction.raisesUrgentFlag").as("raisesUrgentFlag")) //
                            .add(Projections.property("resource.recentUpdate").as("recentUpdate")) //
                            .add(Projections.property("resource.sequenceIdentifier").as("sequenceIdentifier")),
                    ResourceActionOpportunityCategoryDTO.class);

            if (isNotEmpty(resourceActions)) {
                resourceService.setResourceMessageAttributes(scope, resourceActions, user);

                Set<Integer> resources = newHashSet();
                Set<Integer> updateResources = newHashSet();
                Set<Integer> messageResources = newHashSet();
                Map<PrismAction, Integer> urgentCounts = newLinkedHashMap();
                for (ResourceActionOpportunityCategoryDTO resourceAction : resourceActions) {
                    Integer resourceId = resourceAction.getId();
                    resources.add(resourceId);

                    if (isTrue(resourceAction.getRaisesUrgentFlag())) {
                        PrismAction actionId = resourceAction.getActionId();
                        Integer existingCount = urgentCounts.get(actionId);
                        urgentCounts.put(actionId, existingCount == null ? 1 : existingCount + 1);
                    }

                    if (isTrue(resourceAction.getRecentUpdate())) {
                        updateResources.add(resourceId);
                    }

                    Integer unreadMessageCount = resourceAction.getUnreadMessageCount();
                    if (unreadMessageCount != null && unreadMessageCount > 0) {
                        messageResources.add(resourceId);
                    }
                }

                List<ActionActivityRepresentation> actions = urgentCounts.entrySet().stream() //
                        .map(urgentCountEntry -> new ActionActivityRepresentation().withAction(actionMapper.getActionRepresentation(urgentCountEntry.getKey()))
                                .withUrgentCount(urgentCountEntry.getValue()))
                        .collect(toList());

                Integer resourceForWhichCanCreateCount = 0;
                if (scope.equals(INSTITUTION) && isNotEmpty(scopesCreatorFor)) {
                    resourceForWhichCanCreateCount = 1;
                } else if (isResourceCreator(scope, scopesCreatorFor)) {
                    resourceForWhichCanCreateCount = resourceService.getResourcesParentsForWhichUserCanCreateResource(system, INSTITUTION, scope).size();
                }

                representations.add(new ResourceActivityRepresentation().withScope(scope).withDefaultRoleCategory(defaultRoleCategories.get(scope))
                        .withResourceCreator(resourceForWhichCanCreateCount > 0).withCount(resources.size()).withUpdateCount(updateResources.size())
                        .withMessageCount(messageResources.size()).withActions(actions));
            }
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
