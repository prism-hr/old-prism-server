package com.zuehlke.pgadmissions.mapping;

import static com.zuehlke.pgadmissions.domain.definitions.PrismFilterMatchMode.ANY;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;

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
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.ResourceActionDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceListFilterDTO;
import com.zuehlke.pgadmissions.rest.representation.user.UserActivityRepresentation.ResourceActivityRepresentation;
import com.zuehlke.pgadmissions.rest.representation.user.UserActivityRepresentation.ResourceActivityRepresentation.ActionActivityRepresentation;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.ScopeService;

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
    private ScopeService scopeService;

    public List<ResourceActivityRepresentation> getResourceActivityRepresentation(User user, PrismScope permissionScope) {
        DateTime baseline = new DateTime().minusDays(1);
        permissionScope = permissionScope.equals(SYSTEM) ? PrismScope.INSTITUTION : permissionScope;

        List<ResourceActivityRepresentation> representations = Lists.newLinkedList();
        List<PrismScope> visibleScopes = scopeService.getEnclosingScopesDescending(APPLICATION, permissionScope);
        visibleScopes.forEach(scope -> {
            Set<Integer> updatedResources = Sets.newHashSet();
            Map<PrismAction, Integer> actionCounts = Maps.newLinkedHashMap();

            Set<ResourceActionDTO> resourceActionDTOs = resourceService.getResources(user, scope, visibleScopes.stream()
                    .filter(as -> as.ordinal() < scope.ordinal())
                    .collect(Collectors.toList()), //
                    new ResourceListFilterDTO().withMatchMode(ANY).withUrgentOnly(true).withUpdateOnly(true), //
                    Projections.projectionList() //
                            .add(Projections.groupProperty("id").as("resourceId")) //
                            .add(Projections.groupProperty("stateAction.action.id").as("actionId")) //
                            .add(Projections.property("stateAction.raisesUrgentFlag").as("raisesUrgentFlag")) //
                            .add(Projections.property("updatedTimestamp").as("updatedTimestamp")),
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

            representations.add(new ResourceActivityRepresentation().withScope(scope).withUpdateCount(updatedResources.size()).withActions(actions));
        });

        return representations;
    }

}
