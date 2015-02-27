package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceState;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.Role;
import com.zuehlke.pgadmissions.domain.workflow.RoleTransition;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;

@Repository
@SuppressWarnings("unchecked")
public class RoleDAO {

	@Autowired
	private SessionFactory sessionFactory;

	public List<PrismRole> getRoles(User user) {
		return (List<PrismRole>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
		        .setProjection(Projections.groupProperty("role.id")) //
		        .add(Restrictions.eq("user", user)) //
		        .list();
	}

	public List<PrismRole> getRolesOverridingRedactions(PrismScope resourceScope, User user) {
		return (List<PrismRole>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
		        .setProjection(Projections.groupProperty("role.id")) //
		        .createAlias("role", "role", JoinType.INNER_JOIN) //
		        .createAlias("role.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
		        .createAlias("stateActionAssignment.stateAction", "stateAction", JoinType.INNER_JOIN) //
		        .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
		        .createAlias("role.actionRedactions", "actionRedaction", JoinType.LEFT_OUTER_JOIN) //
		        .add(Restrictions.eq("user", user)) //
		        .add(Restrictions.disjunction() //
		                .add(Restrictions.eq("action.scope.id", resourceScope)) //
		                .add(Restrictions.eq("action.creationScope.id", resourceScope))) //
		        .add(Restrictions.isNull("actionRedaction.id")) //
		        .list();
	}

	public List<PrismRole> getRolesOverridingRedactions(Resource resource, User user) {
		return (List<PrismRole>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
		        .setProjection(Projections.groupProperty("role.id")) //
		        .createAlias("role", "role", JoinType.INNER_JOIN) //
		        .add(Restrictions.eq("user", user)) //
		        .createAlias("role.actionRedactions", "actionRedaction", JoinType.LEFT_OUTER_JOIN) //
		        .add(Restrictions.disjunction() //
		                .add(Restrictions.eq("system", resource.getSystem())) //
		                .add(Restrictions.eq("institution", resource.getInstitution())) //
		                .add(Restrictions.eq("program", resource.getProgram())) //
		                .add(Restrictions.eq("project", resource.getProject())) //
		                .add(Restrictions.eq("application", resource.getApplication()))) //
		        .add(Restrictions.isNull("actionRedaction.id")) //
		        .list();
	}

	public List<PrismRole> getRolesForResource(Resource resource, User user) {
		return (List<PrismRole>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
		        .setProjection(Projections.groupProperty("role.id")) //
		        .add(Restrictions.eq("user", user)) //
		        .add(Restrictions.eq(PrismScope.getByResourceClass(resource.getClass()).getLowerCaseName(), resource)) //
		        .list();
	}
	
	public List<PrismRole> getRolesWithinResource(Resource resource, User user) {
		return (List<PrismRole>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
		        .setProjection(Projections.property("role.id")) //
		        .add(Restrictions.eq("user", user)) //
		        .add(Restrictions.disjunction() //
		                .add(Restrictions.eq("system", resource.getSystem())) //
		                .add(Restrictions.eq("institution", resource.getInstitution())) //
		                .add(Restrictions.eq("program", resource.getProgram())) //
		                .add(Restrictions.eq("project", resource.getProject())) //
		                .add(Restrictions.eq("application", resource.getApplication()))) //
		        .list();
	}

	public UserRole getUserRole(Resource resource, User user, Role role) {
		return (UserRole) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
		        .add(Restrictions.eq(resource.getResourceScope().getLowerCaseName(), resource)) //
		        .add(Restrictions.eq("user", user)) //
		        .add(Restrictions.eq("role", role)) //
		        .uniqueResult();
	}

	public List<UserRole> getUserRoles(Resource resource, User user, PrismRole... authorities) {
		return (List<UserRole>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
		        .add(Restrictions.eq(resource.getResourceScope().getLowerCaseName(), resource)) //
		        .add(Restrictions.eq("user", user)) //
		        .add(Restrictions.in("role.id", authorities)) //
		        .list();
	}

	public List<User> getRoleUsers(Resource resource, Role role) {
		return (List<User>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
		        .setProjection(Projections.property("user")) //
		        .add(Restrictions.eq(resource.getResourceScope().getLowerCaseName(), resource)) //
		        .add(Restrictions.eq("role", role)) //
		        .list();
	}

	public List<Integer> getExcludingUserRoles(Resource resource, User user, Role role) {
		String resourceReference = resource.getResourceScope().getLowerCaseName();
		return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
		        .setProjection(Projections.property("excludedUserRole.id")) //
		        .createAlias("role", "role", JoinType.INNER_JOIN) //
		        .createAlias("role.excludedRoles", "excludedRole", JoinType.INNER_JOIN) //
		        .createAlias("excludedRole.userRoles", "excludedUserRole") //
		        .add(Restrictions.eq(resourceReference, resource)) //
		        .add(Restrictions.eq("user", user)) //
		        .add(Restrictions.ne("role", role)) //
		        .add(Restrictions.eq("excludedUserRole." + resourceReference, resource)) //
		        .add(Restrictions.eq("excludedUserRole.user", user)) //
		        .list();
	}

	public Role getCreatorRole(Resource resource) {
		return (Role) sessionFactory.getCurrentSession().createCriteria(Role.class) //
		        .add(Restrictions.eq("scope.id", PrismScope.getByResourceClass(resource.getClass()))) //
		        .add(Restrictions.eq("scopeCreator", true)) //
		        .uniqueResult();
	}

	public List<PrismRole> getActionOwnerRoles(User user, Resource resource, Action action) {
		return (List<PrismRole>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
		        .setProjection(Projections.groupProperty("role.id")) //
		        .createAlias("state", "state", JoinType.INNER_JOIN) //
		        .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
		        .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
		        .createAlias("stateAction.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
		        .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
		        .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
		        .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
		        .add(Restrictions.eq(resource.getResourceScope().getLowerCaseName(), resource)) //
		        .add(Restrictions.eq("stateAction.action", action)) //
		        .add(Restrictions.eq("action.actionType", PrismActionType.USER_INVOCATION)) //
		        .add(Restrictions.disjunction() //
		                .add(Restrictions.eq("userRole.application", resource.getApplication())) //
		                .add(Restrictions.eq("userRole.project", resource.getProject())) //
		                .add(Restrictions.eq("userRole.program", resource.getProgram())) //
		                .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
		                .add(Restrictions.eq("userRole.system", resource.getSystem()))) //
		        .add(Restrictions.eq("userRole.user", user)) //
		        .list();
	}

	public List<RoleTransition> getRoleTransitions(StateTransition stateTransition, PrismRoleTransitionType roleTransitionType) {
		return (List<RoleTransition>) sessionFactory.getCurrentSession().createCriteria(RoleTransition.class) //
		        .add(Restrictions.eq("stateTransition", stateTransition)) //
		        .add(Restrictions.eq("roleTransitionType", roleTransitionType)) //
		        .list();
	}

	public List<User> getUnspecifiedRoleTransitionUsers(Resource resource, RoleTransition roleTransition, User actionOwner) {
		return (List<User>) sessionFactory.getCurrentSession().createCriteria(RoleTransition.class) //
		        .setProjection(Projections.property("userRole.user")) //
		        .createAlias("role", "role", JoinType.INNER_JOIN) //
		        .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
		        .add(Restrictions.eq("id", roleTransition.getId())) //
		        .add(Restrictions.eq("userRole." + resource.getResourceScope().getLowerCaseName(), resource)) //
		        .add(Restrictions.disjunction() //
		                .add(Restrictions.conjunction() //
		                        .add(Restrictions.eq("restrictToActionOwner", true)) //
		                        .add(Restrictions.eq("userRole.user", actionOwner))) //
		                .add(Restrictions.eq("restrictToActionOwner", false))) //
		        .list();
	}

	public List<Role> getActiveRoles() {
		return sessionFactory.getCurrentSession().createCriteria(RoleTransition.class) //
		        .setProjection(Projections.groupProperty("role")) //
		        .list();
	}

	public void deleteObseleteUserRoles() {
		sessionFactory.getCurrentSession().createQuery( //
		        "delete UserRole " //
		                + "where role not in ( " //
		                + "select role " //
		                + "from RoleTransition " //
		                + "group by role) " //
		                + "and role not in ( " //
		                + "select transitionRole " //
		                + "from RoleTransition " //
		                + "group by transitionRole)") //
		        .executeUpdate();
	}

	public void deleteUserRoles(Resource resource, User user) {
		sessionFactory.getCurrentSession().createQuery( //
		        "delete UserRole " //
		                + "where :resourceReference = :resource " //
		                + "and user = :user") //
		        .setParameter("resourceReference", resource.getResourceScope().getLowerCaseName()) //
		        .setParameter("resource", resource).setParameter("user", user) //
		        .executeUpdate();
	}

	public List<PrismRole> getCreatableRoles(PrismScope scopeId) {
		return (List<PrismRole>) sessionFactory.getCurrentSession().createCriteria(RoleTransition.class) //
		        .setProjection(Projections.groupProperty("transitionRole.id")) //
		        .createAlias("transitionRole", "transitionRole", JoinType.INNER_JOIN) //
		        .add(Restrictions.eq("roleTransitionType", PrismRoleTransitionType.CREATE)) //
		        .add(Restrictions.eq("transitionRole.scope.id", scopeId)) //
		        .list();
	}

	public Integer getPermissionOrdinal(User user) {
		return (Integer) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
		        .setProjection(Projections.groupProperty("scope.ordinal")) //
		        .createAlias("role", "role", JoinType.INNER_JOIN) //
		        .createAlias("role.scope", "scope", JoinType.INNER_JOIN) //
		        .add(Restrictions.eq("user", user)) //
		        .addOrder(Order.asc("scope.ordinal")) //
		        .setMaxResults(1) //
		        .uniqueResult();
	}

}
