package com.zuehlke.pgadmissions.services;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Objects;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.dao.ActionDAO;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionRedactionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.ActionPropertyConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.dto.ActionDTO;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.dto.ActionRedactionDTO;
import com.zuehlke.pgadmissions.exceptions.CustomizationException;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.exceptions.WorkflowEngineException;
import com.zuehlke.pgadmissions.exceptions.WorkflowPermissionException;
import com.zuehlke.pgadmissions.rest.dto.UserRegistrationDTO;
import com.zuehlke.pgadmissions.rest.representation.resource.ActionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ActionRepresentation.StateTransitionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ActionRepresentation.StateTransitionRepresentation.RoleTransitionRepresentation;

@Service
@Transactional
public class ActionService {

    @Autowired
    private ActionDAO actionDAO;

    @Autowired
    private CustomizationService customizationService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private StateService stateService;

    @Autowired
    private UserService userService;

    public Action getById(PrismAction id) {
        return entityService.getById(Action.class, id);
    }

    public ActionPropertyConfiguration getActionPropertyConfiguration(Resource resource, User user, Action action) {
        return customizationService.getConfiguration(ActionPropertyConfiguration.class, resource, user, "action", action);
    }

    public ActionPropertyConfiguration getActionPropertyConfiguration(Resource resource, PrismLocale locale, PrismProgramType programType, Action action) {
        return customizationService.getConfiguration(ActionPropertyConfiguration.class, resource, locale, programType, "action", action);
    }

    public void updateConfiguration(Resource resource, PrismLocale locale, PrismProgramType programType, Action action, String json)
            throws DeduplicationException, CustomizationException {
        createOrUpdateActionPropertyConfiguration(resource, locale, programType, action, json);
        resourceService.executeUpdate(resource, PrismDisplayProperty.valueOf(resource.getResourceScope().name() + "_COMMENT_UPDATED_NOTIFICATION"));
    }

    public void createOrUpdateActionPropertyConfiguration(Resource resource, PrismLocale locale, PrismProgramType programType, Action action, String json)
            throws CustomizationException, DeduplicationException {
        customizationService.validateConfiguration(resource, action, locale, programType);
        ActionPropertyConfiguration transientConfiguration = new ActionPropertyConfiguration().withResource(resource).withLocale(locale)
                .withProgramType(programType).withAction(action).withJson(json)
                .withSystemDefault(customizationService.isSystemDefault(action, locale, programType));
        entityService.createOrUpdate(transientConfiguration);
    }

    public void validateInvokeAction(Resource resource, Action action, Comment comment) {
        User owner = comment.getAuthor();
        User delegateOwner = comment.getUser();

        User currentUser = userService.getCurrentUser();
        authenticateActionInvocation(currentUser, action, owner, delegateOwner);

        Resource operative = resourceService.getOperativeResource(resource, action);

        if (delegateOwner == null && checkActionAvailable(operative, action, owner)) {
            return;
        } else if (delegateOwner != null && checkActionAvailable(operative, action, delegateOwner)) {
            return;
        } else if (delegateOwner != null && checkDelegateActionAvailable(operative, action, delegateOwner)) {
            return;
        }

        throwWorkflowPermissionException(operative, action);
    }

    public void validateUpdateAction(Comment comment) {
        Action action = comment.getAction();

        User owner = comment.getUser();
        User delegateOwner = comment.getDelegateUser();

        User currentUser = userService.getCurrentUser();
        authenticateActionInvocation(currentUser, action, owner, delegateOwner);

        Resource resource = comment.getResource();

        if (owner == currentUser || checkDelegateActionAvailable(resource, action, delegateOwner)) {
            return;
        }

        throwWorkflowPermissionException(resource, action);
    }

    public Set<ActionRepresentation> getPermittedActions(Resource resource, User user) {
        PrismAction lastActionId = null;
        ActionRepresentation thisActionRepresentation = null;

        PrismState lastTransitionStateId = null;
        StateTransitionRepresentation thisStateTransitionRepresentation = null;

        Set<ActionRepresentation> representations = Sets.newLinkedHashSet();
        List<ActionDTO> actions = actionDAO.getPermittedActions(resource, user);
        for (ActionDTO action : actions) {
            PrismAction thisActionId = action.getActionId();
            boolean newAction = thisActionId != lastActionId;

            if (newAction) {
                thisActionRepresentation = new ActionRepresentation().withName(thisActionId).withRaisesUrgentFlag(action.getRaisesUrgentFlag());
                representations.add(thisActionRepresentation);
            }

            PrismState thisTransitionStateId = action.getTransitionStateId();

            if (newAction || (thisTransitionStateId != null && thisTransitionStateId != lastTransitionStateId)) {
                thisStateTransitionRepresentation = new StateTransitionRepresentation().withTransitionStateId(thisTransitionStateId);
                thisActionRepresentation.addStateTransition(thisStateTransitionRepresentation);
            }

            PrismRole thisTransitionRoleId = action.getTransitionRoleId();

            if (thisTransitionRoleId != null) {
                thisStateTransitionRepresentation.addRoleTransition(new RoleTransitionRepresentation().withRoleId(thisTransitionRoleId)
                        .withRoleTransitionType(action.getRoleTransitionType()).withMinimumPermitted(action.getMinimumPermitted())
                        .withMaximumPermitted(action.getMaximumPermitted()));
            }

            lastActionId = thisActionId;
            lastTransitionStateId = thisTransitionStateId;
        }

        return representations;
    }

    public Set<ActionRepresentation> getPermittedActions(PrismScope resourcScope, Integer systemId, Integer institutionId, Integer programId,
            Integer projectId, Integer applicationId, PrismState stateId, User user) {
        return Sets.newLinkedHashSet(actionDAO.getPermittedActions(resourcScope, systemId, institutionId, programId, projectId, applicationId, stateId, user));
    }

    public List<PrismActionEnhancement> getPermittedActionEnhancements(Resource resource, User user) {
        Set<PrismActionEnhancement> enhancements = Sets.newHashSet();
        enhancements.addAll(actionDAO.getGlobalActionEnhancements(resource, user));
        enhancements.addAll(actionDAO.getCustomActionEnhancements(resource, user));
        return Lists.newArrayList(enhancements);
    }

    public ActionOutcomeDTO executeUserAction(Resource resource, Action action, Comment comment) throws DeduplicationException {
        validateInvokeAction(resource, action, comment);
        return executeAction(resource, action, comment);
    }

    public ActionOutcomeDTO executeAction(Resource resource, Action action, Comment comment) throws DeduplicationException {
        User actionOwner = comment.getUser();

        if (action.getActionCategory() == PrismActionCategory.CREATE_RESOURCE || action.getActionCategory() == PrismActionCategory.VIEW_EDIT_RESOURCE) {
            Resource duplicateResource = entityService.getDuplicateEntity(resource);

            if (duplicateResource != null) {
                if (action.getActionCategory() == PrismActionCategory.CREATE_RESOURCE) {
                    Action redirectAction = getRedirectAction(action, actionOwner, duplicateResource);
                    return new ActionOutcomeDTO().withUser(actionOwner).withResource(duplicateResource).withTransitionResource(duplicateResource)
                            .withTransitionAction(redirectAction);
                } else if (!Objects.equal(resource.getId(), duplicateResource.getId())) {
                    throwWorkflowPermissionException(resource, action);
                }
            }
        }

        StateTransition stateTransition = stateService.executeStateTransition(resource, action, comment);
        Action transitionAction = stateTransition == null ? action.getFallbackAction() : stateTransition.getTransitionAction();
        Resource transitionResource = stateTransition == null ? resource : resource.getEnclosingResource(transitionAction.getScope().getId());

        return new ActionOutcomeDTO().withUser(actionOwner).withResource(resource).withTransitionResource(transitionResource)
                .withTransitionAction(transitionAction);
    }

    public ActionOutcomeDTO getRegistrationOutcome(User user, UserRegistrationDTO registrationDTO, String referrer) throws Exception {
        Action action = getById(registrationDTO.getAction().getActionId());
        if (action.getActionCategory() == PrismActionCategory.CREATE_RESOURCE) {
            Object operativeResourceDTO = registrationDTO.getAction().getOperativeResourceDTO();
            return resourceService.createResource(user, action, operativeResourceDTO, referrer);
        } else {
            Resource resource = entityService.getById(action.getScope().getId().getResourceClass(), registrationDTO.getResourceId());
            return new ActionOutcomeDTO().withUser(user).withResource(resource).withTransitionResource(resource).withTransitionAction(action);
        }
    }

    public Action getViewEditAction(Resource resource) {
        return actionDAO.getViewEditAction(resource);
    }

    public Action getRedirectAction(Action action, User actionOwner, Resource duplicateResource) {
        if (action.getActionType() == PrismActionType.USER_INVOCATION) {
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

    public List<PrismAction> getPropagatedActions(Integer stateTransitionPendingId) {
        return actionDAO.getPropagatedActions(stateTransitionPendingId);
    }

    public HashMultimap<PrismAction, PrismActionRedactionType> getRedactions(Resource resource, User user) {
        List<PrismRole> roleIds = roleService.getRoles(resource, user);
        List<ActionRedactionDTO> redactions = actionDAO.getRedactions(resource, roleIds);

        HashMultimap<PrismAction, PrismActionRedactionType> actionRedactions = HashMultimap.create();
        for (ActionRedactionDTO redaction : redactions) {
            actionRedactions.put(redaction.getActionId(), redaction.getRedactionType());
        }
        return actionRedactions;
    }

    public void throwWorkflowPermissionException(Resource resource, Action action) {
        throwWorkflowPermissionException(resource, action, null);
    }

    public void throwWorkflowPermissionException(Resource resource, Action action, String message) {
        Action fallbackAction = action.getFallbackAction();
        Resource fallbackResource = resource.getEnclosingResource(fallbackAction.getScope().getId());
        throw new WorkflowPermissionException(action, fallbackAction, resource, fallbackResource);
    }

    public void throwWorkflowEngineException(Resource resource, Action action, String message) {
        throw new WorkflowEngineException("Error executing " + action.getId().name() + " on " + resource.getCode() + ". Explanation was \"" + message + "\".");
    }

    public List<Action> getCustomizableActions() {
        return actionDAO.getCustomizableActions();
    }

    public List<Action> getConfigurableActions() {
        return actionDAO.getConfigurableActions();
    }

    public void validateUserAction(Resource resource, Action action, User invoker) {
        if (checkActionAvailable(resource, action, invoker)) {
            return;
        } else if (checkDelegateActionAvailable(resource, action, invoker)) {
            return;
        }
        throwWorkflowPermissionException(resource, action);
    }

    private boolean checkActionAvailable(Resource resource, Action action, User invoker) {
        return actionDAO.getPermittedAction(resource, action, invoker) != null;
    }

    private boolean checkDelegateActionAvailable(Resource resource, Action action, User invoker) {
        Action delegateAction = actionDAO.getDelegateAction(resource, action);
        return checkActionAvailable(resource, delegateAction, invoker);
    }

    private void authenticateActionInvocation(User currentUser, Action action, User owner, User delegateOwner) {
        if (action.getActionCategory() == PrismActionCategory.CREATE_RESOURCE) {
            return;
        } else if (owner != null && Objects.equal(owner.getId(), currentUser.getId())) {
            return;
        } else if (delegateOwner != null && Objects.equal(delegateOwner.getId(), currentUser.getId())) {
            return;
        }
        throw new Error();
    }

}
