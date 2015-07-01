package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_APPLICATION_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_EDIT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_INSTITUTION_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_PROGRAM_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_PROJECT_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.CREATE_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.VIEW_EDIT_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionType.USER_INVOCATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Objects;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.dao.ActionDAO;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCustomQuestionDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionRedactionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceCondition;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.ActionCustomQuestionDefinition;
import com.zuehlke.pgadmissions.domain.workflow.Scope;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.dto.ActionCreationScopeDTO;
import com.zuehlke.pgadmissions.dto.ActionDTO;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.dto.ActionRedactionDTO;
import com.zuehlke.pgadmissions.dto.ResourceListRowDTO;
import com.zuehlke.pgadmissions.exceptions.WorkflowEngineException;
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
    private StateService stateService;

    @Inject
    private UserService userService;

    public Action getById(PrismAction id) {
        return entityService.getById(Action.class, id);
    }

    public ActionCustomQuestionDefinition getCustomQuestionDefinitionById(PrismActionCustomQuestionDefinition id) {
        return entityService.getById(ActionCustomQuestionDefinition.class, id);
    }

    public void validateInvokeAction(Resource resource, Action action, Comment comment) {
        resource = resourceService.getOperativeResource(resource, action);
        if (checkActionAvailable(resource, action, comment.getUser(), comment.getDeclinedResponse())) {
            return;
        }
        throw new WorkflowPermissionException(resource, action);
    }

    public List<ActionDTO> getPermittedActions(PrismScope resourceScope, ResourceListRowDTO row, User user) {
        return actionDAO.getPermittedActions(resourceScope, row.getResourceId(), row.getSystemId(), row.getInstitutionId(), row.getProgramId(),
                row.getProjectId(), row.getApplicationId(), user);
    }

    public List<ActionDTO> getPermittedActions(PrismScope resourceScope, Integer resourceId, Integer systemId, Integer institutionId,
            Integer programId, Integer projectId, Integer applicationId, User user) {
        return actionDAO.getPermittedActions(resourceScope, resourceId, systemId, institutionId, programId, projectId, applicationId, user);
    }

    public List<ActionDTO> getPermittedUnsecuredActions(PrismScope resourceScope, Set<Integer> resourceIds, PrismScope... exclusions) {
        return actionDAO.getPermittedUnsecuredActions(resourceScope, resourceIds, exclusions);
    }

    public List<PrismActionEnhancement> getGlobalActionEnhancements(Resource resource, PrismAction actionId, User user) {
        return actionDAO.getGlobalActionEnhancements(resource, actionId, user);
    }

    public List<PrismActionEnhancement> getCustomActionEnhancements(Resource resource, PrismAction actionId, User user) {
        return actionDAO.getCustomActionEnhancements(resource, actionId, user);
    }

    public HashMultimap<Integer, ActionDTO> getCreateResourceActions(PrismScope resourceScope, Set<Integer> resourceIds) {
        HashMultimap<Integer, ActionDTO> creationActions = HashMultimap.create();
        for (ActionDTO resourceListActionDTO : actionDAO.getPermittedUnsecuredActions(resourceScope, resourceIds, APPLICATION)) {
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

    public ActionOutcomeDTO executeUserAction(Resource resource, Action action, Comment comment) throws Exception {
        validateInvokeAction(resource, action, comment);
        return executeAction(resource, action, comment);
    }

    public ActionOutcomeDTO executeAction(Resource resource, Action action, Comment comment) throws Exception {
        return executeAction(resource, action, comment, true);
    }

    public ActionOutcomeDTO executeActionSilent(Resource resource, Action action, Comment comment) throws Exception {
        return executeAction(resource, action, comment, false);
    }

    public ActionOutcomeDTO getRegistrationOutcome(User user, UserRegistrationDTO registrationDTO) throws Exception {
        CommentDTO comment = registrationDTO.getComment();
        if (comment != null) {
            comment.setUser(user.getId());
            return resourceService.executeAction(user, registrationDTO.getResourceId(), comment);
        }
        return null;
    }

    public Action getViewEditAction(Resource resource) {
        return actionDAO.getViewEditAction(resource);
    }

    public Action getRedirectAction(Action action, User actionOwner, Resource duplicateResource) {
        if (action.getActionType() == USER_INVOCATION) {
            return actionDAO.getUserRedirectAction(duplicateResource, actionOwner);
        } else {
            return actionDAO.getSystemRedirectAction(duplicateResource);
        }
    }

    public List<Action> getActions() {
        return entityService.list(Action.class);
    }

    public List<PrismAction> getEscalationActions() {
        return actionDAO.getEscalationActions();
    }

    public boolean hasRedactions(Resource resource, User user) {
        return !getRedactions(resource, user).isEmpty();
    }

    public HashMultimap<PrismAction, PrismActionRedactionType> getRedactions(Resource resource, User user) {
        HashMultimap<PrismAction, PrismActionRedactionType> actionRedactions = HashMultimap.create();
        List<PrismRole> rolesOverridingRedactions = roleService.getRolesOverridingRedactions(resource, user);
        if (rolesOverridingRedactions.isEmpty()) {
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

    public boolean hasRedactions(PrismScope resourceScope, Set<Integer> resourceIds, User user) {
        return !getRedactions(resourceScope, resourceIds, user).isEmpty();
    }

    public List<PrismActionRedactionType> getRedactions(PrismScope resourceScope, Set<Integer> resourceIds, User user) {
        List<PrismRole> rolesOverridingRedactions = roleService.getRolesOverridingRedactions(resourceScope, user);
        if (rolesOverridingRedactions.isEmpty()) {
            List<PrismRole> roleIds = roleService.getRoles(user);
            if (!(resourceIds.isEmpty() || roleIds.isEmpty())) {
                return actionDAO.getRedactions(resourceScope, resourceIds, roleIds);
            }
        }
        return Lists.newArrayList();
    }

    public List<Action> getCustomizableActions() {
        return actionDAO.getCustomizableActions();
    }

    public List<Action> getConfigurableActions() {
        return actionDAO.getConfigurableActions();
    }

    public void validateViewEditAction(Resource resource, Action action, User invoker) {
        if (checkActionAvailable(resource, action, invoker, false)) {
            return;
        }
        throw new WorkflowPermissionException(resource, action);
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
            PrismAction fallbackActionId;
            Scope creationScope = action.getCreationScope();
            switch (creationScope == null ? action.getScope().getId() : creationScope.getId()) {
            case APPLICATION:
                fallbackActionId = SYSTEM_VIEW_APPLICATION_LIST;
                break;
            case INSTITUTION:
                fallbackActionId = SYSTEM_VIEW_INSTITUTION_LIST;
                break;
            case PROGRAM:
                fallbackActionId = SYSTEM_VIEW_PROGRAM_LIST;
                break;
            case PROJECT:
                fallbackActionId = SYSTEM_VIEW_PROJECT_LIST;
                break;
            case SYSTEM:
                fallbackActionId = SYSTEM_VIEW_EDIT;
                break;
            default:
                throw new UnsupportedOperationException();
            }
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
        List<PrismActionCondition> filteredActionConditions = Lists.newLinkedList();
        List<ResourceCondition> actionConditions = resourceService.getResourceAttributes(resource, ResourceCondition.class, "actionCondition");

        PrismScope lastResourceScope = null;
        for (ResourceCondition actionCondition : actionConditions) {
            PrismScope thisResourceScope = actionCondition.getResource().getResourceScope();
            if (lastResourceScope != null && !thisResourceScope.equals(lastResourceScope)) {
                break;
            }
            filteredActionConditions.add(actionCondition.getActionCondition());
            lastResourceScope = thisResourceScope;
        }

        if (lastResourceScope != null) {
            List<PrismAction> partnerActions = actionDAO.getPartnerActions(resource, filteredActionConditions);
            return partnerActions;
        }

        return Lists.newArrayList();

    }

    public boolean checkActionAvailable(Resource resource, Action action, User user, boolean declinedResponse) {
        if (action.getDeclinableAction() && BooleanUtils.toBoolean(declinedResponse)) {
            return true;
        } else if (actionDAO.getPermittedUnsecuredAction(resource, action, userService.isCurrentUser(user)) != null) {
            return true;
        } else if (actionDAO.getPermittedAction(resource, action, user) != null) {
            return true;
        }
        return false;
    }

    private ActionOutcomeDTO executeAction(Resource resource, Action action, Comment comment, boolean notify) throws Exception {
        User user = comment.getUser();

        if (action.getActionCategory() == CREATE_RESOURCE || action.getActionCategory() == VIEW_EDIT_RESOURCE) {
            Resource duplicate = entityService.getDuplicateEntity(resource);

            if (duplicate != null) {
                if (action.getActionCategory() == CREATE_RESOURCE) {
                    Action redirectAction = getRedirectAction(action, user, duplicate);
                    if (redirectAction == null) {
                        throw new WorkflowEngineException("SYSTEM_DUPLICATE_" + action.getCreationScope().getId().name());
                    }
                    return new ActionOutcomeDTO().withUser(user).withResource(duplicate).withTransitionResource(duplicate)
                            .withTransitionAction(redirectAction);
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

}
