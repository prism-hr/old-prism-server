package com.zuehlke.pgadmissions.services;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Objects;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.dao.ActionDAO;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCustomQuestionDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionRedactionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.ActionCustomQuestionDefinition;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.dto.ActionRedactionDTO;
import com.zuehlke.pgadmissions.dto.StateActionDTO;
import com.zuehlke.pgadmissions.exceptions.WorkflowPermissionException;
import com.zuehlke.pgadmissions.rest.dto.user.UserRegistrationDTO;
import com.zuehlke.pgadmissions.rest.representation.resource.ActionRepresentation;

@Service
@Transactional
public class ActionService {

	@Autowired
	private ActionDAO actionDAO;

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

	public ActionCustomQuestionDefinition getCustomQuestionDefinitionById(PrismActionCustomQuestionDefinition id) {
		return entityService.getById(ActionCustomQuestionDefinition.class, id);
	}

	public void validateInvokeAction(Resource resource, Action action, Comment comment) {
		User user = comment.getUser();
		Boolean declineComment = BooleanUtils.toBoolean(comment.getDeclinedResponse());
		authenticateActionInvocation(action, user, declineComment);

		if (declineComment) {
			return;
		}

		resource = resourceService.getOperativeResource(resource, action);

		if (comment.isDelegateComment() && checkDelegateActionAvailable(resource, action, user)) {
			return;
		} else if (checkActionAvailable(resource, action, user)) {
			return;
		}

		throw new WorkflowPermissionException(resource, action);
	}

	public void validateUpdateAction(Comment comment) {
		Action action = comment.getAction();

		User user = comment.getUser();
		authenticateActionInvocation(action, user, null);

		Resource resource = comment.getResource();

		if (userService.isCurrentUser(user) || checkDelegateActionAvailable(resource, action, user)) {
			return;
		}

		throw new WorkflowPermissionException(resource, action);
	}

	public Set<ActionRepresentation> getPermittedActions(Resource resource, User user) {
		PrismScope scope = resource.getResourceScope();
		Institution institution = resource.getInstitution();
		Program program = resource.getProgram();
		Project project = resource.getProject();
		Application application = resource.getApplication();

		Set<ActionRepresentation> actions = Sets.newLinkedHashSet(actionDAO.getPermittedActions(scope, resource.getId(), resource.getSystem().getId(),
		        institution == null ? null : institution.getId(), program == null ? null : program.getId(), project == null ? null : project.getId(),
		        application == null ? null : application.getId(), user));
		actions.addAll(actionDAO.getCreateResourceActions(resource.getResourceScope()));

		for (ActionRepresentation action : actions) {
			PrismAction actionId = action.getId();
			action.addActionEnhancements(actionDAO.getGlobalActionEnhancements(resource, actionId, user));
			action.addActionEnhancements(actionDAO.getCustomActionEnhancements(resource, actionId, user));

			if (BooleanUtils.isTrue(action.getPrimaryState())) {
				action.addNextStates(stateService.getSelectableTransitionStates(resource.getState(), actionId,
				        scope == PrismScope.PROGRAM && program.getImported()));
			}
		}

		return actions;
	}

	public Set<ActionRepresentation> getPermittedActions(PrismScope resourceScope, Integer systemId, Integer institutionId, Integer programId,
	        Integer projectId, Integer applicationId, User user) {
		return Sets.newLinkedHashSet(actionDAO.getPermittedActions(resourceScope,
		        ObjectUtils.firstNonNull(applicationId, projectId, programId, institutionId, systemId), systemId, institutionId, programId, projectId,
		        applicationId, user));
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
		User actionOwner = comment.getUser();

		if (action.getActionCategory() == PrismActionCategory.CREATE_RESOURCE || action.getActionCategory() == PrismActionCategory.VIEW_EDIT_RESOURCE) {
			Resource duplicateResource = entityService.getDuplicateEntity(resource);

			if (duplicateResource != null) {
				if (action.getActionCategory() == PrismActionCategory.CREATE_RESOURCE) {
					Action redirectAction = getRedirectAction(action, actionOwner, duplicateResource);
					return new ActionOutcomeDTO().withUser(actionOwner).withResource(duplicateResource).withTransitionResource(duplicateResource)
					        .withTransitionAction(redirectAction);
				} else if (!Objects.equal(resource.getId(), duplicateResource.getId())) {
					throw new WorkflowPermissionException(resource, action);
				}
			}
		}

		StateTransition stateTransition = stateService.executeStateTransition(resource, action, comment);
		Action transitionAction = stateTransition == null ? action.getFallbackAction() : stateTransition.getTransitionAction();
		Resource transitionResource = stateTransition == null ? resource : resource.getEnclosingResource(transitionAction.getScope().getId());

		return new ActionOutcomeDTO().withUser(actionOwner).withResource(resource).withTransitionResource(transitionResource)
		        .withTransitionAction(transitionAction);
	}

	public ActionOutcomeDTO getRegistrationOutcome(User user, UserRegistrationDTO registrationDTO) throws Exception {
		Action action = getById(registrationDTO.getAction().getActionId());
		if (action.getActionCategory() == PrismActionCategory.CREATE_RESOURCE) {
			Object operativeResourceDTO = registrationDTO.getAction().getOperativeResourceDTO();
			return resourceService.create(user, action, operativeResourceDTO, registrationDTO.getAction().getReferer(), registrationDTO.getAction()
			        .getWorkflowPropertyConfigurationVersion());
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

	public void validateUserAction(Resource resource, Action action, User invoker) {
		if (checkActionAvailable(resource, action, invoker)) {
			return;
		} else if (checkDelegateActionAvailable(resource, action, invoker)) {
			return;
		}
		throw new WorkflowPermissionException(resource, action);
	}

	public HashMultimap<PrismState, PrismAction> getCreateResourceActionsByState(PrismScope resourceScope) {
		HashMultimap<PrismState, PrismAction> creationActions = HashMultimap.create();
		for (StateActionDTO stateActionDTO : actionDAO.getCreateResourceActionsByState(resourceScope)) {
			creationActions.put(stateActionDTO.getStateId(), stateActionDTO.getActionId());
		}
		return creationActions;
	}
	
	public void setStateGroupTransitionActions() {
		List<PrismAction> actions = actionDAO.getStateGroupTransitionActions();
		if (!actions.isEmpty()) {
			actionDAO.setStateGroupTransitionActions(actions);
		}
	}

	private boolean checkActionAvailable(Resource resource, Action action, User user) {
		return actionDAO.getPermittedAction(resource, action, user) != null;
	}

	private boolean checkDelegateActionAvailable(Resource resource, Action action, User invoker) {
		Action delegateAction = actionDAO.getDelegateAction(resource, action);
		return checkActionAvailable(resource, delegateAction, invoker);
	}

	private void authenticateActionInvocation(Action action, User user, Boolean declineComment) {
		if (action.getDeclinableAction() && BooleanUtils.toBoolean(declineComment)) {
			return;
		} else if (action.getActionCategory() == PrismActionCategory.CREATE_RESOURCE) {
			return;
		} else if (userService.isCurrentUser(user)) {
			return;
		}
		throw new Error();
	}

}
