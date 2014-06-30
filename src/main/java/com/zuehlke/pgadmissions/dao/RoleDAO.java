package com.zuehlke.pgadmissions.dao;

import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.HashMultimap;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.RoleTransition;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

@Repository
public class RoleDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public Role getById(final PrismRole id) {
        return (Role) sessionFactory.getCurrentSession().createCriteria(Role.class).add(Restrictions.eq("id", id)).uniqueResult();
    }

    public int save(UserRole userRole) {
        return (Integer) sessionFactory.getCurrentSession().save(userRole);
    }

    @SuppressWarnings("unchecked")
    public List<User> getUsersByRole(Resource resource, PrismRole[] authorities) {
        return sessionFactory.getCurrentSession().createCriteria(User.class) //
                .createAlias("userRoles", "userRole") //
                .add(Restrictions.in("userRole.role.id", authorities)) //
                .list();
    }

    public UserRole getUserRole(User user, Resource resource, PrismRole authority) {
        return (UserRole) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .add(Restrictions.eq("user", user)) //
                .add(Restrictions.eq("role.id", authority)) //
                .add(Restrictions.eq(resource.getResourceType().toString().toLowerCase(), resource)) //
                .uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<UserRole> getExcludingUserRole(User user, Resource resource, Set<Role> excludedRoles) {
        return (List<UserRole>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .add(Restrictions.eq("user", user)) //
                .add(Restrictions.in("role", excludedRoles)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.application", resource.getApplication())) //
                        .add(Restrictions.eq("userRole.project", resource.getProject())) //
                        .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                        .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                        .add(Restrictions.eq("userRole.system", resource.getSystem()))) //
                .list();
    }

    @SuppressWarnings("unchecked")
    public List<UserRole> getUserRoles(Resource resource, User user, PrismRole... authorities) {
        return (List<UserRole>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .add(Restrictions.eq("user", user)) //
                .add(Restrictions.in("role.id", authorities)) //
                .add(Restrictions.eq(resource.getResourceType().toString().toLowerCase(), resource)) //
                .list();
    }

    @SuppressWarnings("unchecked")
    public List<RoleTransition> getRoleTransitions(StateTransition stateTransition, List<Role> invokerRoles) {
        Criterion restrictToInvokerCriterion = invokerRoles.isEmpty() ? //
                Restrictions.eq("roleTransitionType", PrismRoleTransitionType.CREATE)
                : Restrictions.in("role", invokerRoles);

        return (List<RoleTransition>) sessionFactory.getCurrentSession().createCriteria(RoleTransition.class) //
                .add(Restrictions.eq("stateTransition", stateTransition)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.eq("restrictToInvoker", true)) //
                                .add(restrictToInvokerCriterion)) //
                        .add(Restrictions.eq("restrictToInvoker", false))) //
                .addOrder(Order.asc("role")) //
                .addOrder(Order.asc("processingOrder")) //
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    public Role getResourceCreatorRole(Resource resource, Action createAction) {
        return (Role) sessionFactory.getCurrentSession().createCriteria(RoleTransition.class) //
                .setProjection(Projections.groupProperty("role")) //
                .createAlias("stateTransition", "stateTransition", JoinType.INNER_JOIN) //
                .createAlias("stateTransition.stateAction", "stateAction", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("stateAction.state", resource.getState())) //
                .add(Restrictions.eq("stateAction.action", createAction)) //
                .add(Restrictions.eq("type", PrismRoleTransitionType.CREATE)) //
                .add(Restrictions.eq("restrictToInvoker", true)).uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<Role> getActionRoles(Resource resource, Action action) {
        return (List<Role>) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.property("stateActionAssignment.role")) //
                .createAlias("action", "action", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("state", resource.getState())) //
                .add(Restrictions.eq("action", action)) //
                .add(Restrictions.eq("action.actionType", PrismActionType.USER_INVOCATION)) //
                .list();
    }

    @SuppressWarnings("unchecked")
    public List<Role> getActionOwnerRoles(User user, Resource resource, Action action) {
        return (List<Role>) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.groupProperty("stateActionAssignment.role")) //
                .createAlias("action", "action", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("state", resource.getState())) //
                .add(Restrictions.eq("action", action)) //
                .add(Restrictions.eq("action.actionType", PrismActionType.USER_INVOCATION)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.application", resource.getApplication())) //
                        .add(Restrictions.eq("userRole.project", resource.getProject())) //
                        .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                        .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                        .add(Restrictions.eq("userRole.system", resource.getSystem()))) //
                .add(Restrictions.eq("user.parentUser", user)) //
                .list();
    }

    @SuppressWarnings("unchecked")
    public List<Role> getDelegateActionOwnerRoles(User user, Resource resource, Action action) {
        return (List<Role>) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.groupProperty("stateActionAssignment.role")) //
                .createAlias("action", "action", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("state", resource.getState())) //
                .add(Restrictions.eq("action", action)) //
                .add(Restrictions.eq("action.actionType", PrismActionType.USER_INVOCATION)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.application", resource.getApplication())) //
                        .add(Restrictions.eq("userRole.project", resource.getProject())) //
                        .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                        .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                        .add(Restrictions.eq("userRole.system", resource.getSystem()))) //
                .add(Restrictions.eq("user.parentUser", user)) //
                .list();
    }

    @SuppressWarnings("unchecked")
    public HashMultimap<RoleTransition, User> getRoleTransitionUsers(StateTransition stateTransition, Resource resource, User invoker) {
        List<RoleTransition> roleTransitions = (List<RoleTransition>) sessionFactory.getCurrentSession().createCriteria(RoleTransition.class) //
                .add(Restrictions.eq("stateTransition", stateTransition)) //
                .add(Restrictions.ne("roleTransitionType", PrismRoleTransitionType.CREATE)) //
                .list();

        HashMultimap<RoleTransition, User> userRoleTransitions = HashMultimap.create();
        for (RoleTransition roleTransition : roleTransitions) {
            List<User> users = sessionFactory.getCurrentSession().createCriteria(RoleTransition.class) //
                    .setProjection(Projections.property("userRole.user"))
                    .createAlias("role", "role", JoinType.INNER_JOIN) //
                    .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                    .add(Restrictions.eq("id", roleTransition.getId())) //
                    .add(Restrictions.eq("userRole." + resource.getClass().getSimpleName().toLowerCase(), resource)) //
                    .add(Restrictions.disjunction() //
                            .add(Restrictions.conjunction() //
                                    .add(Restrictions.eq("restrictToInvoker", true)) //
                                    .add(Restrictions.eq("userRole.user", invoker))) //
                            .add(Restrictions.eq("restrictToInvoker", false))) //
                    .list();

            for (User user : users) {
                userRoleTransitions.put(roleTransition, user);
            }
        }

        return userRoleTransitions;

    }

    @SuppressWarnings("unchecked")
    public HashMultimap<Role, RoleTransition> getRoleCreationTransitions(StateTransition stateTransition) {
        List<RoleTransition> roleTransitions = (List<RoleTransition>) sessionFactory.getCurrentSession().createCriteria(RoleTransition.class) //
                .add(Restrictions.eq("stateTransition", stateTransition)) //
                .add(Restrictions.eq("roleTransitionType", PrismRoleTransitionType.CREATE)) //
                .list();

        HashMultimap<Role, RoleTransition> instructions = HashMultimap.create();
        for (RoleTransition roleTransition : roleTransitions) {
            instructions.put(roleTransition.getRole(), roleTransition);
        }

        return instructions;
    }

    public UserRole getUserRole(User user, PrismRole authority) {
        return (UserRole) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .add(Restrictions.eq("role.id", authority)) //
                .add(Restrictions.eq("user", user)) //
                .uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<User> getUsers(Resource resource) {
        return (List<User>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty("user"))
                .add(Restrictions.eq(PrismScope.getResourceScope(resource.getClass()).getLowerCaseName(), resource))
                .list();
    }

    @SuppressWarnings("unchecked")
    public List<PrismRole> getRoles(Resource resource, User user) {
        return (List<PrismRole>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty("role.id"))
                .add(Restrictions.eq("user", user))
                .add(Restrictions.eq(PrismScope.getResourceScope(resource.getClass()).getLowerCaseName(), resource))
                .list();
    }

    public List<PrismRole> getRoles(Class<? extends Resource> resourceType) {
        return sessionFactory.getCurrentSession().createCriteria(Role.class)
                .setProjection(Projections.property("id"))
                .add(Restrictions.eq("scope.id", PrismScope.getResourceScope(resourceType)))
                .list();
    }
}
