package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.DELETE;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.text.WordUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.comment.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.Role;
import com.zuehlke.pgadmissions.domain.workflow.RoleTransition;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowPropertyConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowPropertyDefinition;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.exceptions.IntegrationException;
import com.zuehlke.pgadmissions.exceptions.WorkflowEngineException;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;
import com.zuehlke.pgadmissions.utils.ReflectionUtils;

@Service
@Transactional
public class RoleService {

	@Autowired
	private RoleDAO roleDAO;

	@Autowired
	private ActionService actionService;

	@Autowired
	private EntityService entityService;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private ResourceService resourceService;

	@Autowired
	private UserService userService;

	@Autowired
	private CustomizationService customizationService;

	@Autowired
	private ApplicationContext applicationContext;

	public Role getById(PrismRole roleId) {
		return entityService.getByProperty(Role.class, "id", roleId);
	}

	public UserRole getUserRole(Resource resource, User user, Role role) {
		return roleDAO.getUserRole(resource, user, role);
	}

	public List<Role> getRoles() {
		return entityService.list(Role.class);
	}

	public UserRole getOrCreateUserRole(UserRole userRole) throws DeduplicationException {
		return getOrCreateUserRole(userRole.getResource(), userRole.getUser(), userRole.getRole().getId());
	}

	public UserRole getOrCreateUserRole(Resource resource, User user, PrismRole newRoleId) throws DeduplicationException {
		Role newRole = getById(newRoleId);
		UserRole userRole = entityService.getOrCreate(new UserRole().withResource(resource).withUser(user).withRole(newRole)
		        .withAssignedTimestamp(new DateTime()));
		entityService.flush();
		return userRole;
	}

	public void updateUserRole(Resource resource, User user, PrismRoleTransitionType transitionType, PrismRole... roles) throws DeduplicationException,
	        InstantiationException, IllegalAccessException, BeansException, WorkflowEngineException, IOException, IntegrationException {
		if (roles.length > 0) {
			User invoker = userService.getCurrentUser();
			Action action = actionService.getViewEditAction(resource);

			PropertyLoader loader = applicationContext.getBean(PropertyLoader.class).localize(resource);

			Comment comment = new Comment().withAction(action).withUser(invoker)
			        .withContent(loader.load(PrismDisplayPropertyDefinition.valueOf(resource.getResourceScope().name() + "_COMMENT_UPDATED_USER_ROLE")))
			        .withDeclinedResponse(false).withCreatedTimestamp(new DateTime());
			for (PrismRole role : roles) {
				comment.addAssignedUser(user, getById(role), transitionType);
			}

			actionService.executeUserAction(resource, action, comment);
			notificationService.sendInvitationNotifications(comment);
		}
	}

	public List<PrismRole> getActionOwnerRoles(User user, Resource resource, Action action) {
		return roleDAO.getActionOwnerRoles(user, resource, action);
	}

	public boolean hasUserRole(Resource resource, User user, PrismRole roleId) {
		Role role = getById(roleId);
		return roleDAO.getUserRole(resource, user, role) != null;
	}

	public boolean hasAnyUserRole(Resource resource, User user, PrismRole... roleIds) {
		for (PrismRole roleId : roleIds) {
			if (hasUserRole(resource, user, roleId)) {
				return true;
			}
		}
		return false;
	}

	public List<PrismRole> getRoles(User user) {
		return roleDAO.getRoles(user);
	}

	public List<PrismRole> getRolesOverridingRedactions(PrismScope resourceScope, User user) {
		return roleDAO.getRolesOverridingRedactions(resourceScope, user);
	}

	public List<PrismRole> getRolesOverridingRedactions(Resource resource, User user) {
		return roleDAO.getRolesOverridingRedactions(resource, user);
	}

	public List<PrismRole> getRolesForResource(Resource resource, User user) {
		return roleDAO.getRolesForResource(resource, user);
	}

	public List<PrismRole> getRolesWithinResource(Resource resource, User user) {
		return roleDAO.getRolesWithinResource(resource, user);
	}

	public List<User> getRoleUsers(Resource resource, Role role) {
		return roleDAO.getRoleUsers(resource, role);
	}

	public List<User> getRoleUsers(Resource resource, PrismRole roleId) {
		Role role = getById(roleId);
		return roleDAO.getRoleUsers(resource, role);
	}

	public List<PrismRole> getCreatableRoles(PrismScope scopeId) {
		return roleDAO.getCreatableRoles(scopeId);
	}

	public void deleteObseleteUserRoles() {
		roleDAO.deleteObseleteUserRoles();
	}

	public Role getCreatorRole(Resource resource) {
		return roleDAO.getCreatorRole(resource);
	}

	public void executeDelegatedRoleTransitions(Resource resource, Comment comment) throws DeduplicationException {
		ReflectionUtils.invokeMethod(this, ReflectionUtils.getMethodName(comment.getAction().getId(), "execute") + "RoleTransition", resource, comment);
		entityService.flush();
	}

	public void executeRoleTransitions(Resource resource, Comment comment, StateTransition stateTransition) throws DeduplicationException {
		for (PrismRoleTransitionType roleTransitionType : PrismRoleTransitionType.values()) {
			List<RoleTransition> roleTransitions = roleDAO.getRoleTransitions(stateTransition, roleTransitionType);
			executeRoleTransitions(resource, comment, roleTransitions);
		}
		entityService.flush();
	}

	public void deleteUserRoles(Resource resource, User user) throws DeduplicationException, InstantiationException, IllegalAccessException, BeansException,
	        WorkflowEngineException, IOException, IntegrationException {
		List<PrismRole> roles = roleDAO.getRolesForResource(resource, user);
		updateUserRole(resource, user, DELETE, roles.toArray(new PrismRole[roles.size()]));
	}

	public Integer getPermissionPrecedence(User user) {
		return roleDAO.getPermissionOrdinal(user);
	}

	public void executeApplicationProvideReferenceRoleTransition(Resource resource, Comment comment) {
		User user = comment.getDelegateUser();
		Role referee = getById(PrismRole.APPLICATION_REFEREE);
		UserRole oldUserRole = new UserRole().withResource(resource).withUser(user).withRole(referee);
		Role viewer = getById(PrismRole.APPLICATION_VIEWER_REFEREE);
		UserRole newUserRole = new UserRole().withResource(resource).withUser(user).withRole(viewer);
		executeUpdateUserRole(oldUserRole, newUserRole, comment);
	}

	private void executeRoleTransitions(Resource resource, Comment comment, List<RoleTransition> roleTransitions) {
		for (RoleTransition roleTransition : roleTransitions) {
			List<User> users = getRoleTransitionUsers(resource, comment, roleTransition);
			for (User user : users) {
				executeRoleTransition(comment, user, roleTransition);
			}
		}
	}

	private List<User> getRoleTransitionUsers(Resource resource, Comment comment, RoleTransition roleTransition) throws WorkflowEngineException {
		User actionOwner = comment.getActionOwner();
		User restrictedToUser = roleTransition.getRestrictToActionOwner() ? actionOwner : null;

		List<User> users;
		if (roleTransition.getRoleTransitionType().isSpecified()) {
			users = getSpecifiedRoleTransitionUsers(comment, roleTransition, restrictedToUser);
		} else {
			users = roleDAO.getUnspecifiedRoleTransitionUsers(resource, roleTransition, restrictedToUser);
		}

		Integer minimumPermitted;
		Integer maximumPermitted;

		WorkflowPropertyDefinition definition = roleTransition.getWorkflowPropertyDefinition();
		if (definition == null) {
			minimumPermitted = roleTransition.getMinimumPermitted();
			maximumPermitted = roleTransition.getMaximumPermitted();
		} else {
			WorkflowPropertyConfiguration workflowPropertyConfiguration = (WorkflowPropertyConfiguration) customizationService.getConfigurationWithVersion(
			        PrismConfiguration.WORKFLOW_PROPERTY, definition, resource.getWorkflowPropertyConfigurationVersion());

			minimumPermitted = workflowPropertyConfiguration.getMinimum();
			maximumPermitted = workflowPropertyConfiguration.getMaximum();
		}

		if (!(minimumPermitted == null || users.size() >= minimumPermitted) && !(maximumPermitted == null || users.size() <= maximumPermitted)) {
			throw new WorkflowEngineException("Incorrect number of role assignments");
		}

		return users;
	}

	private List<User> getSpecifiedRoleTransitionUsers(Comment comment, RoleTransition roleTransition, User restrictedToUser) {
		List<User> transitionUsers = Lists.newArrayList();

		Role transitionRole = roleTransition.getTransitionRole();
		PrismRoleTransitionType roleTransitionType = roleTransition.getRoleTransitionType();

		for (CommentAssignedUser assignee : comment.getAssignedUsers()) {
			if (assignee.getRole() == transitionRole && assignee.getRoleTransitionType() == roleTransitionType) {
				User transitionUser = assignee.getUser();
				if (restrictedToUser == null || transitionUser.equals(restrictedToUser)) {
					transitionUsers.add(transitionUser);
				} else {
					throw new WorkflowEngineException("Invalid role assignment");
				}
			}
		}

		return transitionUsers;
	}

	private void executeRoleTransition(Comment comment, User user, RoleTransition roleTransition) throws DeduplicationException {
		DateTime baseline = new DateTime();

		Role role = roleTransition.getRole();
		Role transitionRole = roleTransition.getTransitionRole();

		Resource resource = resourceService.getOperativeResource(comment.getResource(), comment.getAction());
		Resource transitionResource = comment.getResource();

		UserRole transientRole = new UserRole().withResource(resource).withUser(user).withRole(role).withAssignedTimestamp(baseline);
		UserRole transientTransitionRole = new UserRole().withResource(transitionResource).withUser(user).withRole(transitionRole)
		        .withAssignedTimestamp(baseline);

		ReflectionUtils.invokeMethod(this, "execute" + WordUtils.capitalizeFully(roleTransition.getRoleTransitionType().name()) + "UserRole", transientRole,
		        transientTransitionRole, comment);
	}

	public void executeBranchUserRole(UserRole userRole, UserRole transitionUserRole, Comment comment) throws DeduplicationException {
		UserRole persistentRole = entityService.getDuplicateEntity(userRole);
		if (persistentRole != null) {
			comment.addAssignedUser(transitionUserRole.getUser(), transitionUserRole.getRole(), PrismRoleTransitionType.CREATE);
			getOrCreateUserRole(transitionUserRole);
		}
	}

	public void executeCreateUserRole(UserRole userRole, UserRole transitionUserRole, Comment Comment) throws DeduplicationException {
		getOrCreateUserRole(transitionUserRole);
	}

	public void executeDeleteUserRole(UserRole userRole, UserRole transitionUserRole, Comment comment) throws DeduplicationException {
		executeRetireUserRole(userRole, transitionUserRole, comment);
	}

	public void executeRetireUserRole(UserRole userRole, UserRole transitionUserRole, Comment comment) throws DeduplicationException {
		UserRole persistentRole = entityService.getDuplicateEntity(transitionUserRole);
		if (persistentRole != null) {
			deleteUserRole(persistentRole.getResource(), persistentRole.getUser(), persistentRole.getRole());
			comment.addAssignedUser(transitionUserRole.getUser(), transitionUserRole.getRole(), PrismRoleTransitionType.DELETE);
		}
	}

	public void executeUpdateUserRole(UserRole userRole, UserRole transitionUserRole, Comment comment) throws DeduplicationException {
		UserRole persistentRole = entityService.getDuplicateEntity(userRole);
		if (persistentRole != null) {
			comment.addAssignedUser(userRole.getUser(), userRole.getRole(), PrismRoleTransitionType.DELETE);
			comment.addAssignedUser(transitionUserRole.getUser(), transitionUserRole.getRole(), PrismRoleTransitionType.CREATE);
			deleteUserRole(persistentRole.getResource(), persistentRole.getUser(), persistentRole.getRole());
			getOrCreateUserRole(transitionUserRole);
		}
	}

	public void executeReviveUserRole(UserRole userRole, UserRole transitionUserRole, Comment comment) throws DeduplicationException {
		UserRole persistentRole = entityService.getDuplicateEntity(userRole);
		if (persistentRole != null) {
			persistentRole.setLastNotifiedDate(null);
		}
	}

	public void executeExhumeUserRole(UserRole userRole, UserRole transitionUserRole, Comment comment) throws DeduplicationException {
		executeUpdateUserRole(userRole, transitionUserRole, comment);
	}

	private void deleteUserRole(Resource resource, User user, Role role) {
		UserRole userRole = roleDAO.getUserRole(resource, user, role);
		validateUserRoleRemoval(resource, userRole.getRole());
		entityService.delete(userRole);
		reassignResourceOwner(resource);
	}

	private void validateUserRoleRemoval(Resource resource, Role roleToRemove) {
		Role creatorRole = getCreatorRole(resource);
		if (creatorRole == roleToRemove) {
			List<User> creatorRoleAssignments = getRoleUsers(resource, creatorRole);
			if (creatorRoleAssignments.size() == 1) {
				throw new Error();
			}
		}
	}

	private void reassignResourceOwner(Resource resource) {
		User owner = resource.getUser();
		Role ownerRole = getCreatorRole(resource);
		if (!hasUserRole(resource, owner, ownerRole.getId())) {
			User newOwner = getRoleUsers(resource, ownerRole).get(0);
			resource.setUser(newOwner);
		}
	}

}
