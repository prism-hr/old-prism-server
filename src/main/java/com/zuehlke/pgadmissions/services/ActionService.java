package com.zuehlke.pgadmissions.services;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.dao.ActionDAO;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.exceptions.WorkflowEngineException;
import com.zuehlke.pgadmissions.exceptions.WorkflowPermissionException;
import com.zuehlke.pgadmissions.rest.dto.UserRegistrationDTO;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ActionRepresentation;

@Service
@Transactional
public class ActionService {

    @Autowired
    private ActionDAO actionDAO;

    @Autowired
    private StateService stateService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private UserService userService;

    public Action getById(PrismAction id) {
        return entityService.getById(Action.class, id);
    }

    public void validateInvokeAction(Resource resource, Action action, Comment comment) {
        User owner = comment.getUser();
        User delegateOwner = comment.getDelegateUser();

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

    public List<ActionRepresentation> getPermittedActions(Integer systemId, Integer institutionId, Integer programId, Integer projectId, Integer applicationId,
                                                          PrismState stateId, User user) {
        return actionDAO.getPermittedActions(systemId, institutionId, programId, projectId, applicationId, stateId, user);
    }

    public List<ActionRepresentation> getPermittedActions(Resource resource, User user) {
        return actionDAO.getPermittedActions(getResourceId(resource.getSystem()), getResourceId(resource.getInstitution()), getResourceId(resource.getProgram()),
                getResourceId(resource.getProject()), getResourceId(resource.getApplication()), resource.getState().getId(), user);
    }

    public List<PrismActionEnhancement> getPermittedActionEnhancements(Resource resource, User user) {
        Set<PrismActionEnhancement> enhancements = Sets.newHashSet();
        enhancements.addAll(actionDAO.getGlobalActionEnhancements(resource, user));
        enhancements.addAll(actionDAO.getCustomActionEnhancements(resource, user));
        return Lists.newArrayList(enhancements);
    }

    public ActionOutcomeDTO executeUserAction(Resource resource, Action action, Comment comment) throws DeduplicationException {
        validateInvokeAction(resource, action, comment);
        return executeSystemAction(resource, action, comment);
    }

    public ActionOutcomeDTO executeSystemAction(Resource resource, Action action, Comment comment) throws DeduplicationException {
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
        Action transitionAction = stateTransition == null ? action : stateTransition.getTransitionAction();
        Resource transitionResource = stateTransition == null ? resource : resource.getEnclosingResource(transitionAction.getScope().getId());

        return new ActionOutcomeDTO().withUser(actionOwner).withResource(resource).withTransitionResource(transitionResource)
                .withTransitionAction(transitionAction);
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

    public ActionOutcomeDTO getRegistrationOutcome(User user, UserRegistrationDTO registrationDTO, String referrer) throws DeduplicationException,
            InterruptedException, IOException, JAXBException {
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

    private boolean checkActionAvailable(Resource resource, Action action, User invoker) {
        return actionDAO.getPermittedAction(resource, action, invoker) != null;
    }

    private boolean checkDelegateActionAvailable(Resource resource, Action action, User invoker) {
        Action delegateAction = actionDAO.getDelegateAction(resource, action);
        return checkActionAvailable(resource, delegateAction, invoker);
    }

    private void authenticateActionInvocation(User currentUser, Action action, User owner, User delegateOwner) {
        if (!(currentUser == null && action.getActionCategory() == PrismActionCategory.CREATE_RESOURCE || //
                Objects.equal(owner.getId(), currentUser.getId()) || Objects.equal(delegateOwner.getId(), currentUser.getId()))) {
            throw new Error();
        }
    }

    private Integer getResourceId(Resource resource) {
        return resource == null ? null : resource.getId();
    }

}
