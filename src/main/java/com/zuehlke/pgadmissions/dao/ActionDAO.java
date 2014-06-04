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

import com.zuehlke.pgadmissions.domain.PrismResource;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.PrismAction;
import com.zuehlke.pgadmissions.domain.enums.PrismActionType;

@Repository
public class ActionDAO {
    
    private SessionFactory sessionFactory;

    public ActionDAO() {
    }

    @Autowired
    public ActionDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    public PrismAction getValidAction(PrismResource resource, PrismAction action) {
        return (PrismAction) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.property("action.id")) //
                .createAlias("action", "action", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("state", resource.getState())) //
                .add(Restrictions.eq("action.id", action)) //
                .add(Restrictions.eq("action.actionType", PrismActionType.USER_INVOCATION)) //
                .uniqueResult();
    }
    
    public PrismAction getDelegateAction(PrismResource resource, PrismAction actionId) {
        return (PrismAction) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.property("action.delegateAction.id")) //
                .createAlias("action", "action", JoinType.INNER_JOIN) //
                .createAlias("action.delegateAction", "delegateAction", JoinType.INNER_JOIN)
                .add(Restrictions.eq("state", resource.getState())) //
                .add(Restrictions.eq("action.id", actionId)) //
                .add(Restrictions.eq("action.actionType", PrismActionType.USER_INVOCATION)) //
                .add(Restrictions.eq("delegateAction.actionType", PrismActionType.USER_INVOCATION)) //
                .uniqueResult();
    }

    public PrismAction getRedirectAction(PrismResource resource, User user) {
        return (PrismAction) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.property("action.id")) //
                .createAlias("action", "action", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignments", "stateActionAssignments", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("state", resource.getState())) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.isNotNull("precedence")) //
                                .add(Property.forName("precedence").eq(getActionPrecedence(resource, user))) //
                                .add(Restrictions.eq("stateActionAssignments.defaultAction", true))) //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.isNull("precedence")) //
                                .add(Restrictions.eq("defaultAction", true)))) //
                .add(Restrictions.eq("stateActionAssignments.defaultAction", true))
                 .add(Restrictions.eq("action.actionType", PrismActionType.USER_INVOCATION)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.application", resource.getApplication())) //
                        .add(Restrictions.eq("userRole.project", resource.getProject())) //
                        .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                        .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                        .add(Restrictions.eq("userRole.system", resource.getSystem())) //
                        .add(Restrictions.eq("user.parentUser", user))) //
                .add(Restrictions.eq("userAccount.enabled", true)) //
                .uniqueResult();
    }
    
    public PrismAction getPermittedAction(PrismResource resource, PrismAction actionId, User user) {
        return (PrismAction) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.property("action.id")) //
                .createAlias("action", "action", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("state", resource.getState())) //
                .add(Restrictions.eq("action.id", actionId)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.isNotNull("precedence")) //
                                .add(Property.forName("precedence").eq(getActionPrecedence(resource, user)))) //
                        .add(Restrictions.isNull("precedence"))) //
                .add(Restrictions.eq("action.actionType", PrismActionType.USER_INVOCATION)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.application", resource.getApplication())) //
                        .add(Restrictions.eq("userRole.project", resource.getProject())) //
                        .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                        .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                        .add(Restrictions.eq("userRole.system", resource.getSystem())) //
                        .add(Restrictions.eq("user.parentUser", user))) //
                .add(Restrictions.eq("userAccount.enabled", true)) //
                .uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<StateAction> getPermittedActions(PrismResource resource, User user) {
        return (List<StateAction>) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .createAlias("action", "action", JoinType.INNER_JOIN) //
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
                .add(Restrictions.eq("action.actionType", PrismActionType.USER_INVOCATION)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.application", resource.getApplication())) //
                        .add(Restrictions.eq("userRole.project", resource.getProject())) //
                        .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                        .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                        .add(Restrictions.eq("userRole.system", resource.getSystem())) //
                        .add(Restrictions.eq("user.parentUser", user))) //
                .add(Restrictions.eq("userAccount.enabled", true))
                .addOrder(Order.desc("raisesUrgentFlag")) //
                .addOrder(Order.asc("action.id")) //
                .list();
    }

    private DetachedCriteria getActionPrecedence(PrismResource resource, User user) {
        return DetachedCriteria.forClass(StateAction.class) //
                .setProjection(Projections.max("precedence")) //
                .createAlias("action", "action", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN).createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN)
                .add(Restrictions.eq("state", resource.getState())) //
                .add(Restrictions.isNotNull("precedence")) //
                .add(Restrictions.eq("action.actionType", PrismActionType.USER_INVOCATION)) //
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
