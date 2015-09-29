package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_EDIT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.CREATE_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.VIEW_EDIT_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;
import static java.util.Arrays.asList;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Objects;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
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

    public List<ActionDTO> getPermittedActions(Resource resource, User user) {
        PrismScope resourceScope = resource.getResourceScope();
        List<PrismScope> parentScopes = scopeService.getParentScopesDescending(resource.getResourceScope(), SYSTEM);
        return actionDAO.getPermittedActions(resourceScope, Lists.newArrayList(resource.getId()), parentScopes, user);
    }

    public LinkedHashMultimap<Integer, ActionDTO> getPermittedActions(PrismScope resourceScope, Collection<Integer> resourceIds, User user) {
        LinkedHashMultimap<Integer, ActionDTO> permittedActions = LinkedHashMultimap.create();
        List<PrismScope> parentScopes = scopeService.getParentScopesDescending(resourceScope, SYSTEM);
        actionDAO.getPermittedActions(resourceScope, resourceIds, parentScopes, user).forEach(permittedAction -> {
            permittedActions.put(permittedAction.getResourceId(), permittedAction);
        });
        return permittedActions;
    }

    public List<ActionDTO> getPermittedUnsecuredActions(PrismScope resourceScope, Collection<Integer> resourceIds, PrismScope... exclusions) {
        List<ActionDTO> actions = Lists.newArrayList();
        if (isNotEmpty(resourceIds)) {
            return actionDAO.getPermittedUnsecuredActions(resourceScope, resourceIds, userService.isUserLoggedIn(), exclusions);
        }
        return actions;
    }

    public List<PrismActionEnhancement> getGlobalActionEnhancements(Resource resource, PrismAction actionId, User user) {
        return actionDAO.getGlobalActionEnhancements(resource, actionId, user);
    }

    public List<PrismActionEnhancement> getCustomActionEnhancements(Resource resource, PrismAction actionId, User user) {
        return actionDAO.getCustomActionEnhancements(resource, actionId, user);
    }

    public LinkedHashMultimap<Integer, ActionDTO> getCreateResourceActions(PrismScope resourceScope, Collection<Integer> resourceIds, PrismScope... exclusions) {
        LinkedHashMultimap<Integer, ActionDTO> creationActions = LinkedHashMultimap.create();
        for (ActionDTO resourceListActionDTO : getPermittedUnsecuredActions(resourceScope, resourceIds, exclusions)) {
            creationActions.put(resourceListActionDTO.getResourceId(), resourceListActionDTO);
        }
        return creationActions;
    }

    public List<PrismActionEnhancement> getPermittedActionEnhancements(Resource resource, User user) {
        Set<PrismActionEnhancement> enhancements = Sets.newHashSet();
        enhancements.addAll(actionDAO.getGlobalActionEnhancements(resource, user));
        enhancements.addAll(actionDAO.getCustomActionEnhancements(resource, user));
        return Lists.newArrayList(enhancements);
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

    public Action getRedirectAction(Resource resource, User user) {
        return actionDAO.getRedirectAction(resource, user);
    }

    public List<Action> getActions() {
        return entityService.list(Action.class);
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
        if (roleService.getPermissionScope(user).ordinal() >= resourceScope.ordinal()) {
            List<PrismRole> userRoles = roleService.getRolesByScope(user, resourceScope);
            List<PrismRole> rolesWithRedactions = roleService.getRolesWithRedactions(resourceScope);

            userRoles.removeAll(rolesWithRedactions);
            userRoles.isEmpty();
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

    public List<PrismAction> getPartnerActions(ResourceParent resource) {
        List<PrismActionCondition> actionConditions = resourceService.getActionConditions(resource);
        return !actionConditions.isEmpty() ? actionDAO.getPartnerActions(resource, actionConditions) : Lists.newArrayList();
    }

    public boolean checkActionExecutable(Resource resource, Action action, User user, boolean declinedResponse) {
        boolean canExecute = true;
        Set<PrismActionEnhancement> expectedActionEnhancements = getExpectedActionEnhancements(resource, action);
        if (expectedActionEnhancements.size() > 0) {
            canExecute = !getPermittedActionEnhancements(resource, user).stream().filter(ae -> ae.name().contains("_VIEW_EDIT")).collect(Collectors.toList()).isEmpty();
        }
        return canExecute ? checkActionAvailable(resource, action, user, declinedResponse) : false;
    }

    public boolean checkActionAvailable(Resource resource, Action action, User user, boolean declinedResponse) {
        if (action.getDeclinableAction() && BooleanUtils.toBoolean(declinedResponse)) {
            return true;
        } else if (isNotEmpty(getPermittedUnsecuredActions(resource.getResourceScope(), asList(resource.getId())))) {
            return true;
        } else if (actionDAO.getPermittedAction(resource, action, user) != null) {
            return true;
        }
        return false;
    }

    private ActionOutcomeDTO executeAction(Resource resource, Action action, Comment comment, boolean notify) {
        User user = comment.getUser();

        if (action.getActionCategory() == CREATE_RESOURCE || action.getActionCategory() == VIEW_EDIT_RESOURCE) {
            Resource duplicate = entityService.getDuplicateEntity(resource);

            if (duplicate != null) {
                if (action.getActionCategory() == CREATE_RESOURCE) {
                    return new ActionOutcomeDTO().withUser(user).withResource(duplicate).withTransitionResource(duplicate).withTransitionAction(getRedirectAction(duplicate, user));
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

    private Set<PrismActionEnhancement> getExpectedActionEnhancements(Resource resource, Action action) {
        Set<PrismActionEnhancement> expected = Sets.newHashSet(actionDAO.getExpectedDefaultActionEnhancements(resource, action));
        expected.addAll(actionDAO.getExpectedCustomActionEnhancements(resource, action));
        return expected;
    }

}
