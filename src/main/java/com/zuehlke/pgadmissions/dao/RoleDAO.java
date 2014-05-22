package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.PrismResource;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.RoleTransition;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.System;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.RoleTransitionType;
import com.zuehlke.pgadmissions.domain.enums.SystemAction;

@Repository
public class RoleDAO {

    private SessionFactory sessionFactory;

    public RoleDAO() {
    }

    @Autowired
    public RoleDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public System getPrismSystem() {
        return (System) sessionFactory.getCurrentSession().createCriteria(System.class).uniqueResult();
    }

    public Role getById(final Authority id) {
        return (Role) sessionFactory.getCurrentSession().createCriteria(Role.class).add(Restrictions.eq("id", id)).uniqueResult();
    }

    public int save(UserRole userRole) {
        return (Integer) sessionFactory.getCurrentSession().save(userRole);
    }

    public List<User> getUsersInRole(PrismResource scope, Authority[] authorities) {
        // TODO Auto-generated method stub, sort by first and last names
        return null;
    }

    public User getUserInRole(PrismResource scope, Authority[] authorities) {
        // TODO Auto-generated method stub
        return null;
    }

    public UserRole get(User user, PrismResource resource, Authority authority) {
        return (UserRole) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .add(Restrictions.eq("user", user)) //
                .add(Restrictions.eq("role.id", authority)) //
                .add(Restrictions.eq(resource.getResourceType().toString().toLowerCase(), resource)) //
                .uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<RoleTransition> getRoleTransitions(StateTransition stateTransition, List<Role> invokerRoles) {
        return (List<RoleTransition>) sessionFactory.getCurrentSession().createCriteria(RoleTransition.class) //
                .add(Restrictions.eq("stateTransition", stateTransition)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.in("role", invokerRoles)) //
                                .add(Restrictions.eq("restrictToInvoker", true))) //
                        .add(Restrictions.ne("restrictToInvoker", true))) //
                .addOrder(Order.asc("role")) //
                .addOrder(Order.asc("processingOrder")) //
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
    }
    
    public Role getCreatorRole(SystemAction action, PrismResource resource) {
        return (Role) sessionFactory.getCurrentSession().createCriteria(RoleTransition.class) //
                .setProjection(Projections.groupProperty("role"))
                .createAlias("stateTransition", "stateTransition", JoinType.INNER_JOIN)
                .createAlias("stateTransition.stateAction", "stateAction", JoinType.INNER_JOIN)
                .add(Restrictions.eq("stateAction.state", resource.getState())) //
                .add(Restrictions.eq("stateAction.action.id", action)) //
                .add(Restrictions.eq("type", RoleTransitionType.CREATE)) //
                .add(Restrictions.eq("restrictToInvoker", true)).uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<Role> getExecutorRoles(User user, PrismResource resource, SystemAction action) {
        return (List<Role>) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.groupProperty("userRole.role")) //
                .createAlias("stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN)
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN)
                .add(Restrictions.eq("state", resource.getState())) //
                .add(Restrictions.eq("action", action)) //
                .add(Restrictions.disjunction() //
                       .add(Restrictions.eq("application", resource.getApplication())) //
                       .add(Restrictions.eq("project", resource.getProject())) //
                       .add(Restrictions.eq("program", resource.getProgram())) //
                       .add(Restrictions.eq("institution", resource.getInstitution())) //
                       .add(Restrictions.eq("system", resource.getSystem())) //
                 .add(Restrictions.eq("user.parentUser", user))) //
                 .add(Restrictions.eq("userAccount.enabled", true))
                 .list();
    }
    
    @SuppressWarnings("unchecked")
    public List<StateAction> getPermittedActions(User user, PrismResource scope) {
        return (List<StateAction>) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .createAlias("stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN)
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN)
                .add(Restrictions.eq("state", scope.getState())) //
                .add(Restrictions.disjunction() //
                       .add(Restrictions.eq("application", scope.getApplication())) //
                       .add(Restrictions.eq("project", scope.getProject())) //
                       .add(Restrictions.eq("program", scope.getProgram())) //
                       .add(Restrictions.eq("institution", scope.getInstitution())) //
                       .add(Restrictions.eq("system", scope.getSystem())) //
                 .add(Restrictions.eq("user.parentUser", user))) //
                 .add(Restrictions.eq("userAccount.enabled", true))
                 .addOrder(Order.desc("stateActionAssignment.raisesUrgentFlag")) //
                 .addOrder(Order.asc("action.id")) //
                 .list();
        // TODO
        // Filter the returned list to get the precedent actions.
    }

    @SuppressWarnings("unchecked")
    public List<User> getBy(Role role, PrismResource resource) {
        return (List<User>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.distinct(Projections.property("user"))) //
                .add(Restrictions.eq("role", role)) //
                .add(Restrictions.eq(resource.getResourceType().toString().toLowerCase(), resource)) //
                .list();
    }

    public UserRole getUserRole(User user, Authority authority) {
        return (UserRole) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .add(Restrictions.eq("role.id", authority)) //
                .add(Restrictions.eq("user", user)) //
                .uniqueResult();
    }

}
