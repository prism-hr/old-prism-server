package com.zuehlke.pgadmissions.services;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newLinkedList;
import static com.zuehlke.pgadmissions.dao.WorkflowDAO.targetScopes;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_EDIT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.CREATE_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.VIEW_EDIT_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.BooleanUtils.toBoolean;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Objects;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.TreeMultimap;
import com.zuehlke.pgadmissions.dao.ActionDAO;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionRedactionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.Scope;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.dto.ActionCreationScopeDTO;
import com.zuehlke.pgadmissions.dto.ActionDTO;
import com.zuehlke.pgadmissions.dto.ActionEnhancementDTO;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.dto.ActionRedactionDTO;
import com.zuehlke.pgadmissions.exceptions.WorkflowPermissionException;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentDTO;
import com.zuehlke.pgadmissions.rest.dto.user.UserRegistrationDTO;

@Service
@Transactional
public class ActionService {

    @Inject
    private ActionDAO actionDAO;

    @Inject
    private AdvertService advertService;

    @Inject
    private EntityService entityService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private RoleService roleService;

    @Inject
    private ScopeService scopeService;

    @Inject
    private StateService stateService;

    @Inject
    private UserService userService;

    public Action getById(PrismAction id) {
        return entityService.getById(Action.class, id);
    }

    public void validateInvokeAction(Resource resource, Action action, User user, boolean declinedReponse) {
        resource = resourceService.getOperativeResource(resource, action);
        if (checkActionExecutable(resource, action, user, declinedReponse)) {
            return;
        }
        throw new WorkflowPermissionException(resource, action);
    }

    public List<Action> getActions(Resource resource) {
        return actionDAO.getActions(resource);
    }

    public ActionDTO getPermittedAction(User user, Resource resource, Action action) {
        PrismAction prismAction = action.getId();
        return getPermittedActions(user, resource, prismAction).stream().filter(pa -> pa.getActionId().equals(prismAction)).findFirst().get();
    }

    public List<ActionDTO> getPermittedActions(User user, Resource resource) {
        return getPermittedActions(user, resource, null);
    }

    public TreeMultimap<Integer, ActionDTO> getPermittedActions(User user, PrismScope scope, Collection<Integer> targeterEntities, Collection<Integer> resources) {
        return getPermittedActions(user, scope, targeterEntities, resources, null);
    }

    public List<ActionDTO> getPermittedUnsecuredActions(PrismScope scope, Collection<Integer> resourceIds) {
        if (isNotEmpty(resourceIds)) {
            return actionDAO.getPermittedUnsecuredActions(scope, resourceIds, userService.isUserLoggedIn());
        }
        return Lists.newArrayList();
    }

    public TreeMultimap<Integer, ActionDTO> getCreateResourceActions(PrismScope scope, Collection<Integer> resourceIds) {
        TreeMultimap<Integer, ActionDTO> creationActions = TreeMultimap.create();
        for (ActionDTO resourceListActionDTO : getPermittedUnsecuredActions(scope, resourceIds)) {
            creationActions.put(resourceListActionDTO.getResourceId(), resourceListActionDTO);
        }
        return creationActions;
    }

    public List<PrismActionEnhancement> getPermittedActionEnhancements(User user, Resource resource) {
        return getPermittedActionEnhancements(user, resource, null);
    }

    public List<PrismActionEnhancement> getPermittedActionEnhancements(User user, Resource resource, PrismAction action) {
        return getPermittedActionEnhancements(user, resource, action, advertService.getAdvertTargeterEntities(user, resource.getResourceScope()));
    }

    public ActionOutcomeDTO executeUserAction(Resource resource, Action action, Comment comment) {
        validateInvokeAction(resource, action, comment.getUser(), comment.getDeclinedResponse());
        return executeAction(resource, action, comment);
    }

    public ActionOutcomeDTO executeAction(Resource resource, Action action, Comment comment) {
        return executeAction(resource, action, comment, true);
    }

    public ActionOutcomeDTO executeActionSilent(Resource resource, Action action, Comment comment) {
        return executeAction(resource, action, comment, false);
    }

    public ActionOutcomeDTO executeRegistrationAction(User user, UserRegistrationDTO registrationDTO) {
        CommentDTO commentDTO = registrationDTO.getComment();
        if (commentDTO != null) {
            commentDTO.setUser(user.getId());
            return resourceService.executeAction(user, commentDTO);
        }
        return null;
    }

    public Action getViewEditAction(Resource resource) {
        return actionDAO.getViewEditAction(resource);
    }

    public List<Action> getActions() {
        return entityService.getAll(Action.class);
    }

    public List<PrismAction> getEscalationActions() {
        return actionDAO.getEscalationActions();
    }

    public HashMultimap<PrismAction, PrismActionRedactionType> getRedactions(Resource resource, User user, List<PrismRole> overridingRoles) {
        HashMultimap<PrismAction, PrismActionRedactionType> actionRedactions = HashMultimap.create();
        if (overridingRoles.isEmpty()) {
            List<PrismRole> roleIds = roleService.getRolesForResource(resource, user);
            if (!roleIds.isEmpty()) {
                List<ActionRedactionDTO> redactions = actionDAO.getRedactions(resource, roleIds);
                for (ActionRedactionDTO redaction : redactions) {
                    actionRedactions.put(redaction.getActionId(), redaction.getRedactionType());
                }
            }
        }
        return actionRedactions;
    }

    public boolean hasRedactions(User user, PrismScope resourceScope) {
        if (roleService.getVisibleScopes(user).get(0).ordinal() == resourceScope.ordinal()) {
            List<PrismRole> userRoles = roleService.getRolesByScope(user, resourceScope);
            List<PrismRole> rolesWithRedactions = roleService.getRolesWithRedactions(resourceScope);

            userRoles.removeAll(rolesWithRedactions);
            return userRoles.isEmpty();
        }
        return false;
    }

    public List<Action> getCustomizableActions() {
        return actionDAO.getCustomizableActions();
    }

    public List<Action> getConfigurableActions() {
        return actionDAO.getConfigurableActions();
    }

    public void setCreationActions() {
        List<ActionCreationScopeDTO> actionCreationScopes = actionDAO.getCreationActions();
        for (ActionCreationScopeDTO actionCreationScope : actionCreationScopes) {
            actionCreationScope.getAction().setCreationScope(actionCreationScope.getCreationScope());
        }
    }

    public void setFallbackActions() {
        List<Action> actions = getActions();
        Map<PrismAction, Action> fallbackActions = Maps.newHashMap();
        for (Action action : actions) {
            Scope creationScope = action.getCreationScope();
            PrismScope actionScopeId = creationScope == null ? action.getScope().getId() : creationScope.getId();
            PrismAction fallbackActionId = actionScopeId == SYSTEM ? SYSTEM_VIEW_EDIT : PrismAction.valueOf("SYSTEM_VIEW_" + actionScopeId.name() + "_LIST");
            Action fallbackAction = fallbackActions.get(fallbackActionId);
            if (fallbackAction == null) {
                fallbackAction = getById(fallbackActionId);
                fallbackActions.put(fallbackActionId, fallbackAction);
            }
            action.setFallbackAction(fallbackAction);
        }
    }

    public void setStateGroupTransitionActions() {
        List<PrismAction> actions = actionDAO.getStateGroupTransitionActions();
        if (!actions.isEmpty()) {
            actionDAO.setStateGroupTransitionActions(actions);
        }
    }

    public List<PrismActionCondition> getActionConditions(PrismScope prismScope) {
        return actionDAO.getActionConditions(prismScope);
    }

    public Map<PrismScope, PrismAction> getCreateResourceActions(PrismScope creationScope) {
        Map<PrismScope, PrismAction> createResourceActions = Maps.newHashMap();
        List<PrismAction> creationActions = actionDAO.getCreateResourceActions(creationScope);
        for (PrismAction creationAction : creationActions) {
            createResourceActions.put(creationAction.getScope(), creationAction);
        }
        return createResourceActions;
    }

    public List<PrismActionCondition> getExternalConditions(ResourceParent resource) {
        return actionDAO.getExternalConditions(resource);
    }

    public boolean checkActionVisible(Resource resource, Action action, User user, List<Integer> targeterEntities) {
        boolean available = true;
        Set<PrismActionEnhancement> expectedActionEnhancements = getExpectedActionEnhancements(resource, action);
        if (expectedActionEnhancements.size() > 0) {
            available = getPermittedActionEnhancements(user, resource, action.getId()).stream().anyMatch(ae -> ae.name().contains("_VIEW"));
        }
        return available ? checkActionAvailable(resource, action, user, false) : false;
    }

    public boolean checkActionExecutable(Resource resource, Action action, User user, boolean declinedResponse) {
        boolean executable = true;
        Set<PrismActionEnhancement> expectedActionEnhancements = getExpectedActionEnhancements(resource, action);
        if (expectedActionEnhancements.size() > 0) {
            executable = getPermittedActionEnhancements(user, resource, action.getId()).stream().anyMatch(ae -> ae.name().contains("_VIEW_EDIT"));
        }
        return executable ? checkActionAvailable(resource, action, user, declinedResponse) : false;
    }

    public boolean checkActionAvailable(Resource resource, Action action, User user, boolean declinedResponse) {
        if (action.getDeclinableAction() && toBoolean(declinedResponse)) {
            return true;
        } else if (actionDAO.getPermittedUnsecuredAction(userService.isUserLoggedIn(), resource, action) != null) {
            return true;
        } else if (getPermittedAction(user, resource, action) != null) {
            return true;
        }
        return false;
    }

    public PrismActionEnhancement[] getAdministratorActionEnhancements(PrismScope scope) {
        String scopeName = scope.name();
        List<PrismActionEnhancement> actionEnhancements = Lists.newArrayList();
        for (PrismActionEnhancement actionEnhancement : PrismActionEnhancement.values()) {
            String actionEnhancementName = actionEnhancement.name();
            if (actionEnhancementName.contains(scopeName) && actionEnhancementName.contains("VIEW_EDIT")) {
                actionEnhancements.add(actionEnhancement);
            }
        }
        return actionEnhancements.toArray(new PrismActionEnhancement[actionEnhancements.size()]);
    }

    private ActionOutcomeDTO executeAction(Resource resource, Action action, Comment comment, boolean notify) {
        User user = comment.getUser();

        if (action.getActionCategory() == CREATE_RESOURCE || action.getActionCategory() == VIEW_EDIT_RESOURCE) {
            Resource duplicate = entityService.getDuplicateEntity(resource);

            if (duplicate != null) {
                if (action.getActionCategory() == CREATE_RESOURCE) {
                    return new ActionOutcomeDTO().withUser(user).withResource(duplicate).withTransitionResource(duplicate).withTransitionAction(getViewEditAction(resource));
                } else if (!Objects.equal(resource.getId(), duplicate.getId())) {
                    throw new WorkflowPermissionException(resource, action);
                }
            }
        }

        StateTransition stateTransition = stateService.executeStateTransition(resource, action, comment, notify);
        Action transitionAction = stateTransition == null ? action.getFallbackAction() : stateTransition.getTransitionAction();
        Resource transitionResource = stateTransition == null ? resource : resource.getEnclosingResource(transitionAction.getScope().getId());

        return new ActionOutcomeDTO().withUser(user).withResource(resource).withTransitionResource(transitionResource)
                .withTransitionAction(transitionAction);
    }

    private List<ActionDTO> getPermittedActions(User user, Resource resource, PrismAction action) {
        Integer resourceId = resource.getId();
        PrismScope scope = resource.getResourceScope();
        List<Integer> targeterEntities = advertService.getAdvertTargeterEntities(user, scope);
        return newLinkedList(getPermittedActions(user, scope, targeterEntities, newArrayList(resourceId), action).get(resourceId));
    }

    private Set<PrismActionEnhancement> getExpectedActionEnhancements(Resource resource, Action action) {
        Set<PrismActionEnhancement> expected = Sets.newHashSet(actionDAO.getExpectedDefaultActionEnhancements(resource, action));
        expected.addAll(actionDAO.getExpectedCustomActionEnhancements(resource, action));
        return expected;
    }

    private List<PrismActionEnhancement> getPermittedActionEnhancements(User user, Resource resource, PrismAction action, Collection<Integer> targeterEntities) {
        PrismScope scope = resource.getResourceScope();
        Set<PrismActionEnhancement> enhancements = Sets.newHashSet();
        for (String enhancementProperty : new String[] { "stateAction.actionEnhancement", "stateActionAssignment.actionEnhancement" }) {
            enhancements.addAll(getPermittedActionEnhancements(user, scope, targeterEntities, Lists.newArrayList(resource.getId()), action, enhancementProperty).stream()
                    .map(ae -> ae.getActionEnhancement()).collect(toList()));
        }
        return Lists.newArrayList(enhancements);
    }

    private TreeMultimap<Integer, ActionDTO> getPermittedActions(User user, PrismScope scope, Collection<Integer> targeterEntities, Collection<Integer> resources,
            PrismAction action) {
        TreeMultimap<Integer, ActionDTO> permittedActions = TreeMultimap.create();
        getActionEntities(user, scope, targeterEntities, resources, action,
                Projections.projectionList() //
                        .add(Projections.groupProperty("resource.id").as("resourceId")) //
                        .add(Projections.groupProperty("action.id").as("actionId")) //
                        .add(Projections.max("stateAction.raisesUrgentFlag").as("raisesUrgentFlag")) //
                        .add(Projections.max("primaryState").as("primaryState")) //
                        .add(Projections.min("stateActionAssignment.externalMode").as("onlyAsPartner")) //
                        .add(Projections.property("action.declinableAction").as("declinable")),
                ActionDTO.class).forEach(permittedAction -> {
                    permittedActions.put(permittedAction.getResourceId(), permittedAction);
                });

        return permittedActions;
    }

    private List<ActionEnhancementDTO> getPermittedActionEnhancements(User user, PrismScope scope, Collection<Integer> targeterEntities, Collection<Integer> resources,
            PrismAction action, String targetColumn) {
        return newArrayList(getActionEntities(user, scope, targeterEntities, resources, action,
                Projections.projectionList() //
                        .add(Projections.groupProperty("action.id").as("action")) //
                        .add(Projections.groupProperty(targetColumn).as("actionEnhancement")),
                ActionEnhancementDTO.class));
    }

    private <T> Set<T> getActionEntities(User user, PrismScope scope, Collection<Integer> targeterEntities, Collection<Integer> resources, PrismAction action,
            ProjectionList columns, Class<T> responseClass) {
        Set<T> actionEntities = Sets.newHashSet();
        List<PrismScope> parentScopes = scopeService.getParentScopesDescending(scope, SYSTEM);
        actionEntities.addAll(actionDAO.getActionEntities(user, scope, resources, action, columns, responseClass));

        if (!scope.equals(SYSTEM)) {
            for (PrismScope parentScope : parentScopes) {
                actionEntities.addAll(actionDAO.getActionEntities(user, scope, parentScope, resources, action, columns, responseClass));
            }

            if (isNotEmpty(targeterEntities)) {
                for (PrismScope targeterScope : targetScopes) {
                    for (PrismScope targetScope : targetScopes) {
                        actionEntities.addAll(actionDAO.getActionEntities(user, scope, targeterScope, targetScope, targeterEntities, resources, action, columns, responseClass));
                    }
                }
            }
        }

        return actionEntities;
    }

}
