package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.PrismResource;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.PrismAction;

@Repository
public class ActionDAO {

    private SessionFactory sessionFactory;

    public ActionDAO() {
    }

    @Autowired
    public ActionDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Action getById(PrismAction actionId) {
        return (Action) sessionFactory.getCurrentSession().createCriteria(Action.class).add(Restrictions.eq("id", actionId)).uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<StateAction> getValidAction(PrismResource resource, PrismAction action) {
        return (List<StateAction>) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .add(Restrictions.eq("state", resource.getState())) //
                .add(Restrictions.eq("action.id", action)) //
                .list();
    }

    public PrismAction getRedirectAction(PrismResource resource, PrismAction action, User user) {
        return (PrismAction) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.property("action.id")) //
                .createAlias("stateActionAssignments", "stateActionAssignments", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("state", resource.getState())) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.isNotNull("precedence")) //
                                .add(Property.forName("precedence").eq(getActionPrecedence(resource, user))) //
                                .add(Restrictions.eq("stateActionAssignments.defaultAction", true))) //
                        .add(Restrictions.eq("stateActionAssignments.defaultAction", true))) //
                .add(Restrictions.eq("precedence", 1))//
                .add(Restrictions.eq("stateActionAssignments.defaultAction", true)).uniqueResult();
    }

    public PrismAction getPermittedAction(User user, PrismResource resource, PrismAction action) {
        return (PrismAction) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.property("action.id")) //
                .createAlias("stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("state", resource.getState())) //
                .add(Restrictions.eq("action.id", action)).add(Restrictions.disjunction() //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.isNotNull("precedence")) //
                                .add(Property.forName("precedence").eq(getActionPrecedence(resource, user)))) //
                        .add(Restrictions.isNull("precedence"))) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.application", resource.getApplication())) //
                        .add(Restrictions.eq("userRole.project", resource.getProject())) //
                        .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                        .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                        .add(Restrictions.eq("userRole.system", resource.getSystem())) //
                        .add(Restrictions.eq("user.parentUser", user))) //
                .add(Restrictions.eq("userAccount.enabled", true)).addOrder(Order.desc("stateActionAssignment.raisesUrgentFlag")) //
                .uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<StateAction> getPermittedActions(User user, PrismResource resource) {
        return (List<StateAction>) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .createAlias("stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("state", resource.getState())) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.isNotNull("precedence")) //
                                .add(Property.forName("precedence").eq(getActionPrecedence(resource, user)))) //
                        .add(Restrictions.isNull("precedence"))) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.application", resource.getApplication())) //
                        .add(Restrictions.eq("userRole.project", resource.getProject())) //
                        .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                        .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                        .add(Restrictions.eq("userRole.system", resource.getSystem())) //
                        .add(Restrictions.eq("user.parentUser", user))) //
                .add(Restrictions.eq("userAccount.enabled", true)).addOrder(Order.desc("stateActionAssignment.raisesUrgentFlag")) //
                .addOrder(Order.asc("action.id")) //
                .list();
    }

    private DetachedCriteria getActionPrecedence(PrismResource resource, User user) {
        return DetachedCriteria.forClass(StateAction.class)
                //
                .setProjection(Projections.max("precedence"))
                //
                .createAlias("stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN)
                //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN)
                //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN)
                //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN).createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN)
                .add(Restrictions.eq("state", resource.getState())) //
                .add(Restrictions.isNotNull("precedence")) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.application", resource.getApplication())) //
                        .add(Restrictions.eq("userRole.project", resource.getProject())) //
                        .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                        .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                        .add(Restrictions.eq("userRole.system", resource.getSystem())) //
                        .add(Restrictions.eq("user.parentUser", user))) //
                .add(Restrictions.eq("userAccount.enabled", true)) //
                .addOrder(Order.asc("action.id"));
    }

}
