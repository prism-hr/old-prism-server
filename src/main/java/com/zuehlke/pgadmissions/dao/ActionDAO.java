package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.ActionRedaction;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.StateActionAssignment;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRedactionType;

@Repository
@SuppressWarnings("unchecked")
public class ActionDAO {
    
    @Autowired
    private SessionFactory sessionFactory;
    
    public Action getValidAction(Resource resource, Action action) {
        return (Action) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.property("action.id")) //
                .createAlias("action", "action", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("state", resource.getState())) //
                .add(Restrictions.eq("action.id", action)) //
                .add(Restrictions.eq("action.actionType", PrismActionType.USER_INVOCATION)) //
                .uniqueResult();
    }
    
    public Action getDelegateAction(Resource resource, Action action) {
        return (Action) sessionFactory.getCurrentSession().createCriteria(StateActionAssignment.class) //
                .setProjection(Projections.property("stateAction.action")) //
                .createAlias("delegatedAction", "action", JoinType.INNER_JOIN)
                .createAlias("stateAction", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "delegateAction", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("stateAction.state", resource.getState())) //
                .add(Restrictions.eq("delegatedAction", action)) //
                .add(Restrictions.eq("action.actionType", PrismActionType.USER_INVOCATION)) //
                .add(Restrictions.eq("delegateAction.actionType", PrismActionType.USER_INVOCATION)) //
                .uniqueResult();
    }

    public Action getUserRedirectAction(Resource resource, User user) {
        return (Action) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.property("action")) //
                .createAlias("action", "action", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("state", resource.getState())) //
                .add(Restrictions.eq("action.actionType", PrismActionType.USER_INVOCATION)) //
                .add(Restrictions.eq("defaultAction", true)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.application", resource.getApplication())) //
                        .add(Restrictions.eq("userRole.project", resource.getProject())) //
                        .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                        .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                        .add(Restrictions.eq("userRole.system", resource.getSystem()))) //
                .add(Restrictions.eq("user.parentUser", user)) //
                .add(Restrictions.eq("userAccount.enabled", true)) //
                .uniqueResult();
    }
    
    public Action getSystemRedirectAction(Resource resource) {
        return (Action) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.property("action")) //
                .createAlias("action", "action", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("state", resource.getState())) //
                .add(Restrictions.eq("action.actionType", PrismActionType.SYSTEM_INVOCATION)) //
                .add(Restrictions.eq("defaultAction", true)) //
                .uniqueResult();
    }
    
    public Action getPermittedAction(Resource resource, Action action, User user) {
        return (Action) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.property("action")) //
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
                .add(Restrictions.eq("userAccount.enabled", true)) //
                .uniqueResult();
    }
    
    public List<PrismAction> getPermittedActions(Resource resource, User user) {
        return (List<PrismAction>) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.groupProperty("action.id"))
                .createAlias("action", "action", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("state", resource.getState())) //
                .add(Restrictions.eq("action.actionType", PrismActionType.USER_INVOCATION)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.application", resource.getApplication())) //
                        .add(Restrictions.eq("userRole.project", resource.getProject())) //
                        .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                        .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                        .add(Restrictions.eq("userRole.system", resource.getSystem()))) //
                .add(Restrictions.eq("user.parentUser", user)) //
                .add(Restrictions.eq("userAccount.enabled", true))
                .addOrder(Order.desc("raisesUrgentFlag")) //
                .addOrder(Order.asc("action.id")) //
                .list();
    }
    
    public List<PrismRedactionType> getRedactions(User user, Resource resource, Action action) {
        return (List<PrismRedactionType>) sessionFactory.getCurrentSession().createCriteria(ActionRedaction.class)
                .setProjection(Projections.groupProperty("redactionType")) //
                .createAlias("role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("action", action)) //
                .add(Restrictions.eq("user.parentUser", user)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.application", resource.getApplication())) //
                        .add(Restrictions.eq("userRole.project", resource.getProject())) //
                        .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                        .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                        .add(Restrictions.eq("userRole.system", resource.getSystem()))) //
                .list();
    }
    
    public List<PrismActionEnhancement> getGlobalActionEnhancements(Resource resource, User user) {
        return (List<PrismActionEnhancement>) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.groupProperty("stateActionEnhancement")) //
                .createAlias("action", "action", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("state", resource.getState())) //
                .add(Restrictions.isNotNull("stateActionEnhancement")) //
                .add(Restrictions.eq("action.actionCategory", PrismActionCategory.VIEW_EDIT_RESOURCE)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.application", resource.getApplication())) //
                        .add(Restrictions.eq("userRole.project", resource.getProject())) //
                        .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                        .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                        .add(Restrictions.eq("userRole.system", resource.getSystem()))) //
                .add(Restrictions.eq("user.parentUser", user)) //
                .add(Restrictions.eq("userAccount.enabled", true)) //
                .list();
    }
    
    public List<PrismActionEnhancement> getCustomActionEnhancements(Resource resource, User user) {
        return (List<PrismActionEnhancement>) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.groupProperty("stateActionAssignment.stateActionEnhancement")) //
                .createAlias("action", "action", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("state", resource.getState())) //
                .add(Restrictions.isNotNull("stateActionAssignment.stateActionEnhancement")) //
                .add(Restrictions.eq("action.actionCategory", PrismActionCategory.VIEW_EDIT_RESOURCE)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.application", resource.getApplication())) //
                        .add(Restrictions.eq("userRole.project", resource.getProject())) //
                        .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                        .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                        .add(Restrictions.eq("userRole.system", resource.getSystem()))) //
                .add(Restrictions.eq("user.parentUser", user)) //
                .add(Restrictions.eq("userAccount.enabled", true)) //
                .list();
    }

}
