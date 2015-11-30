package uk.co.alumeni.prism.mapping;

import static com.google.common.collect.Lists.newLinkedList;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static uk.co.alumeni.prism.domain.definitions.PrismFilterMatchMode.ANY;

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

import jersey.repackaged.com.google.common.collect.Lists;
import jersey.repackaged.com.google.common.collect.Maps;
import uk.co.alumeni.prism.domain.definitions.PrismResourceRelationContext;
import uk.co.alumeni.prism.domain.definitions.PrismResourceRelationContext.PrismResourceRelationGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.dto.ResourceActionDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceListFilterDTO;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRelationRepresentation;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRelationRepresentation.ResourceRelationComponentRepresentation;
import uk.co.alumeni.prism.rest.representation.user.UserActivityRepresentation.ResourceActivityRepresentation;
import uk.co.alumeni.prism.rest.representation.user.UserActivityRepresentation.ResourceActivityRepresentation.ActionActivityRepresentation;
import uk.co.alumeni.prism.services.AdvertService;
import uk.co.alumeni.prism.services.ResourceService;
import uk.co.alumeni.prism.services.RoleService;

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
    private RoleService roleService;

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
                    advertService.getAdvertTargeterEntities(user, scope), //
                    new ResourceListFilterDTO().withMatchMode(ANY).withUrgentOnly(true).withUpdateOnly(true), //
                    Projections.projectionList() //
                            .add(Projections.groupProperty("resource.id").as("resourceId")) //
                            .add(Projections.groupProperty("stateAction.action.id").as("actionId")) //
                            .add(Projections.property("stateAction.raisesUrgentFlag").as("raisesUrgentFlag")) //
                            .add(Projections.property("resource.updatedTimestamp").as("updatedTimestamp")),
                    ResourceActionDTO.class);

            for (ResourceActionDTO resourceActionDTO : resourceActionDTOs) {
                if (isTrue(resourceActionDTO.getRaisesUrgentFlag())) {
                    PrismAction actionId = resourceActionDTO.getActionId();
                    Integer existingCount = actionCounts.get(actionId);
                    actionCounts.put(actionId, existingCount == null ? 1 : existingCount + 1);
                }

                if (resourceActionDTO.getUpdatedTimestamp().isAfter(baseline)) {
                    updatedResources.add(resourceActionDTO.getResourceId());
                }
            }

            List<ActionActivityRepresentation> actions = actionCounts.entrySet().stream()
                    .map(entry -> new ActionActivityRepresentation().withAction(actionMapper.getActionRepresentation(entry.getKey())).withUrgentCount(entry.getValue()))
                    .collect(Collectors.toList());

            if (!updatedResources.isEmpty() || !actions.isEmpty()) {
                representations.add(new ResourceActivityRepresentation().withScope(scope).withUpdateCount(updatedResources.size()).withActions(actions));
            }
        }

        return representations;
    }

}
