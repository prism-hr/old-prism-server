package com.zuehlke.pgadmissions.mapping;

import static com.google.common.collect.Lists.newLinkedList;
import static com.zuehlke.pgadmissions.domain.definitions.PrismFilterMatchMode.ANY;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.hibernate.criterion.Projections;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.definitions.PrismScopeRelationContext;
import com.zuehlke.pgadmissions.domain.definitions.PrismScopeRelationContext.PrismScopeRelationGroup;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.ResourceActionDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceListFilterDTO;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRelationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRelationRepresentation.ResourceRelationComponentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.user.UserActivityRepresentation.ResourceActivityRepresentation;
import com.zuehlke.pgadmissions.rest.representation.user.UserActivityRepresentation.ResourceActivityRepresentation.ActionActivityRepresentation;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.RoleService;

import jersey.repackaged.com.google.common.collect.Lists;
import jersey.repackaged.com.google.common.collect.Maps;

@Service
@Transactional
public class ScopeMapper {

    @Inject
    private ActionMapper actionMapper;

    @Inject
    private ResourceService resourceService;

    @Inject
    private RoleService roleService;

    public List<ResourceRelationRepresentation> getResourceFamilyCreationRepresentations() {
        List<ResourceRelationRepresentation> representations = Lists.newLinkedList();
        for (PrismScopeRelationContext relation : PrismScopeRelationContext.values()) {
            ResourceRelationRepresentation representation = new ResourceRelationRepresentation(relation);

            Map<PrismScope, Integer> occurrences = Maps.newHashMap();
            Map<PrismScope, ResourceRelationComponentRepresentation> scopeRepresentations = Maps.newLinkedHashMap();
            PrismScopeRelationGroup scopeCreationFamilies = relation.getRelations();
            scopeCreationFamilies.forEach(scf -> {
                scf.forEach(s -> {
                    PrismScope scope = s.getScope();
                    Integer frequency = occurrences.get(scope);
                    frequency = frequency == null ? 1 : (frequency + 1);
                    occurrences.put(scope, frequency);
                    scopeRepresentations.put(scope,
                            new ResourceRelationComponentRepresentation(scope, s.getAutosuggest(), s.getDescription(), s.getUser(), s.getOpportunityCategories()));
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

    public List<ResourceActivityRepresentation> getResourceActivityRepresentation(User user) {
        DateTime baseline = new DateTime().minusDays(1);

        List<ResourceActivityRepresentation> representations = Lists.newLinkedList();
        List<PrismScope> visibleScopes = roleService.getVisibleScopes(user);
        for (PrismScope scope : visibleScopes) {
            Set<Integer> updatedResources = Sets.newHashSet();
            Map<PrismAction, Integer> actionCounts = Maps.newLinkedHashMap();

            Set<ResourceActionDTO> resourceActionDTOs = resourceService.getResources(user, scope, visibleScopes.stream()
                    .filter(as -> as.ordinal() < scope.ordinal())
                    .collect(Collectors.toList()), //
                    new ResourceListFilterDTO().withMatchMode(ANY).withUrgentOnly(true).withUpdateOnly(true), //
                    Projections.projectionList() //
                            .add(Projections.groupProperty("resource.id").as("resourceId")) //
                            .add(Projections.groupProperty("stateAction.action.id").as("actionId")) //
                            .add(Projections.property("stateAction.raisesUrgentFlag").as("raisesUrgentFlag")) //
                            .add(Projections.property("resource.updatedTimestamp").as("updatedTimestamp")),
                    ResourceActionDTO.class);

            for (ResourceActionDTO resourceActionDTO : resourceActionDTOs) {
                PrismAction actionId = resourceActionDTO.getActionId();
                Integer existingCount = actionCounts.get(actionId);
                actionCounts.put(actionId, existingCount == null ? 1 : existingCount + 1);

                if (resourceActionDTO.getUpdatedTimestamp().isAfter(baseline)) {
                    updatedResources.add(resourceActionDTO.getResourceId());
                }
            }

            List<ActionActivityRepresentation> actions = Lists.newLinkedList();
            actionCounts.keySet().forEach(action -> {
                actions.add(new ActionActivityRepresentation().withAction(actionMapper.getActionRepresentation(action)).withUrgentCount(actionCounts.get(action)));
            });

            if (!updatedResources.isEmpty() || !actions.isEmpty()) {
                representations.add(new ResourceActivityRepresentation().withScope(scope).withUpdateCount(updatedResources.size()).withActions(actions));
            }
        }

        return representations;
    }

}
