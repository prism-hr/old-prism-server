package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.dao.WorkflowDAO.getEndorsementActionFilterConstraintNew;
import static com.zuehlke.pgadmissions.dao.WorkflowDAO.getResourceStateActionConstraint;
import static com.zuehlke.pgadmissions.dao.WorkflowDAO.getTargetUserRoleConstraintNew;
import static com.zuehlke.pgadmissions.dao.WorkflowDAO.getUserEnabledConstraint;
import static com.zuehlke.pgadmissions.dao.WorkflowDAO.getUserRoleConstraint;
import static com.zuehlke.pgadmissions.dao.WorkflowDAO.getUserRoleWithPartnerConstraint;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_STARTUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.CREATE_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.ESCALATE_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.VIEW_EDIT_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceState;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.StateAction;
import com.zuehlke.pgadmissions.dto.ActionCreationScopeDTO;
import com.zuehlke.pgadmissions.dto.ActionDTO;
import com.zuehlke.pgadmissions.dto.ActionRedactionDTO;

@Repository
@SuppressWarnings("unchecked")
public class ActionDAO {

    @Inject
    private WorkflowDAO workflowDAO;

    @Inject
    private SessionFactory sessionFactory;

    public Action getRedirectAction(Resource resource, User user) {
        return (Action) workflowDAO.getWorklflowCriteria(resource.getResourceScope(), Projections.property("stateAction.action"))
                .add(Restrictions.eq("resource.id", resource.getId())) //
                .add(Restrictions.eq("action.actionCategory", VIEW_EDIT_RESOURCE)) //
                .add(getUserRoleWithPartnerConstraint(resource, user)) //
                .add(getEndorsementActionFilterConstraintNew())
                .setMaxResults(1) //
                .uniqueResult();
    }

    public List<Action> getActions(Resource resource) {
        return (List<Action>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class)
                .setProjection(Projections.groupProperty("stateAction.action"))
                .createAlias("state", "state", JoinType.INNER_JOIN)
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN)
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource))
                .list();
    }

    public Action getPermittedAction(Resource resource, Action action, User user) {
        return (Action) workflowDAO.getWorklflowCriteria(resource.getResourceScope(), Projections.property("stateAction.action"))
                .add(getUserRoleWithPartnerConstraint(resource, user)) //
                .add(getEndorsementActionFilterConstraintNew())
                .setMaxResults(1) //
                .uniqueResult();
    }

    public List<ActionDTO> getPermittedActions(PrismScope resourceScope, Collection<Integer> resourceIds, List<PrismScope> parentScopes, User user) {
        return (List<ActionDTO>) workflowDAO.getWorklflowCriteria(resourceScope, Projections.projectionList() //
                .add(Projections.groupProperty("resource.id").as("resourceId")) //
                .add(Projections.groupProperty("action.id").as("actionId")) //
                .add(Projections.max("stateAction.raisesUrgentFlag").as("raisesUrgentFlag")) //
                .add(Projections.max("primaryState").as("primaryState")) //
                .add(Projections.min("stateActionAssignment.externalMode").as("onlyAsPartner")) //
                .add(Projections.property("action.declinableAction").as("declinable"))) //
                .add(Restrictions.in("resource.id", resourceIds)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.conjunction() //
                                .add(getUserRoleConstraint(resourceScope, parentScopes)) //
                                .add(Restrictions.eq("stateActionAssignment.externalMode", false))) //
                        .add(getTargetUserRoleConstraintNew())) //
                .add(getUserEnabledConstraint(user)) //
                .add(getEndorsementActionFilterConstraintNew()) //
                .addOrder(Order.asc("resource.id")) //
                .addOrder(Order.desc("raisesUrgentFlag")) //
                .addOrder(Order.desc("primaryState")) //
                .addOrder(Order.asc("action.id")) //
                .setResultTransformer(Transformers.aliasToBean(ActionDTO.class)) //
                .list();
    }

    public Action getPermittedUnsecuredAction(Resource resource, Action action, boolean userLoggedIn) {
        return (Action) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.groupProperty("stateAction.action")) //
                .createAlias(resource.getResourceScope().getLowerCamelName(), "resource", JoinType.INNER_JOIN) //
                .createAlias("resource.resourceConditions", "resourceCondition", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .createAlias("action.creationScope", "creationScope", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("resource.id", resource.getId())) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("action.scope.id", SYSTEM)) //
                        .add(userLoggedIn ? Restrictions.isNotNull("resourceCondition.id") : Restrictions.eq("resourceCondition.externalMode", true))) //
                .add(Restrictions.eq("stateAction.action", action)) //
                .add(Restrictions.eq("action.systemInvocationOnly", false)) //
                .add(Restrictions.isEmpty("stateAction.stateActionAssignments")) //
                .add(getResourceStateActionConstraint()) //
                .addOrder(Order.asc("creationScope.ordinal")) //
                .uniqueResult();
    }

    public List<ActionDTO> getPermittedUnsecuredActions(PrismScope resourceScope, Collection<Integer> resourceIds) {
        return (List<ActionDTO>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("resource.id"), "resourceId") //
                        .add(Projections.groupProperty("action.id"), "actionId") //
                        .add(Projections.max("stateAction.raisesUrgentFlag"), "raisesUrgentFlag") //
                        .add(Projections.max("primaryState"), "primaryState") //
                        .add(Projections.property("action.declinableAction"), "declinable")) //
                .createAlias(resourceScope.getLowerCamelName(), "resource", JoinType.INNER_JOIN) //
                .createAlias("resource.resourceConditions", "resourceCondition", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .createAlias("action.creationScope", "creationScope", JoinType.INNER_JOIN) //
                .add(Restrictions.in("resource.id", resourceIds)) //
                .add(Restrictions.eq("resourceCondition.internalMode", true)) //
                .add(Restrictions.eq("action.systemInvocationOnly", false)) //
                .add(Restrictions.isEmpty("stateAction.stateActionAssignments")) //
                .add(getResourceStateActionConstraint()) //
                .addOrder(Order.asc("creationScope.ordinal")) //
                .setResultTransformer(Transformers.aliasToBean(ActionDTO.class)) //
                .list();
    }

    public List<PrismAction> getCreateResourceActions(PrismScope creationScope) {
        return (List<PrismAction>) sessionFactory.getCurrentSession().createCriteria(Action.class) //
                .setProjection(Projections.property("id")) //
                .add(Restrictions.eq("actionCategory", CREATE_RESOURCE)) //
                .add(Restrictions.eq("creationScope.id", creationScope)) //
                .list();
    }

    public List<ActionRedactionDTO> getRedactions(Resource resource, List<PrismRole> roleIds) {
        return (List<ActionRedactionDTO>) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("action.id"), "actionId") //
                        .add(Projections.groupProperty("redaction.redactionType"), "redactionType")) //
                .createAlias("action", "action", JoinType.INNER_JOIN) //
                .createAlias("action.redactions", "redaction", JoinType.INNER_JOIN) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
                .add(Restrictions.in("redaction.role.id", roleIds)) //
                .setResultTransformer(Transformers.aliasToBean(ActionRedactionDTO.class)) //
                .list();
    }

    public List<PrismActionEnhancement> getGlobalActionEnhancements(Resource resource, User user) {
        return (List<PrismActionEnhancement>) workflowDAO.getWorklflowCriteria(resource.getResourceScope(), Projections.groupProperty("stateAction.actionEnhancement"))
                .add(Restrictions.isNotNull("stateAction.actionEnhancement")) //
                .add(Restrictions.eq("action.actionCategory", PrismActionCategory.VIEW_EDIT_RESOURCE)) //
                .add(Restrictions.eq("resource.id", resource.getId())) //
                .add(getUserRoleWithPartnerConstraint(resource, user)) //
                .add(getEndorsementActionFilterConstraintNew())
                .list();
    }

    public List<PrismActionEnhancement> getGlobalActionEnhancements(Resource resource, PrismAction actionId, User user) {
        return (List<PrismActionEnhancement>) workflowDAO.getWorklflowCriteria(resource.getResourceScope(), Projections.groupProperty("stateAction.actionEnhancement"))
                .add(Restrictions.isNotNull("stateAction.actionEnhancement")) //
                .add(Restrictions.eq("stateAction.action.id", actionId)) //
                .add(Restrictions.eq("resource.id", resource.getId())) //
                .add(getUserRoleWithPartnerConstraint(resource, user)) //
                .add(getEndorsementActionFilterConstraintNew())
                .list();
    }

    public List<PrismActionEnhancement> getCustomActionEnhancements(Resource resource, User user) {
        return (List<PrismActionEnhancement>) workflowDAO.getWorklflowCriteria(resource.getResourceScope(), Projections.groupProperty("stateActionAssignment.actionEnhancement"))
                .add(Restrictions.isNotNull("stateActionAssignment.actionEnhancement")) //
                .add(Restrictions.eq("resource.id", resource.getId())) //
                .add(getUserRoleWithPartnerConstraint(resource, user)) //
                .add(getEndorsementActionFilterConstraintNew())
                .list();
    }

    public List<PrismActionEnhancement> getCustomActionEnhancements(Resource resource, PrismAction actionId, User user) {
        return (List<PrismActionEnhancement>) workflowDAO.getWorklflowCriteria(resource.getResourceScope(), Projections.groupProperty("stateActionAssignment.actionEnhancement"))
                .add(Restrictions.isNotNull("stateActionAssignment.actionEnhancement")) //
                .add(Restrictions.eq("stateAction.action.id", actionId)) //
                .add(Restrictions.eq("resource.id", resource.getId())) //
                .add(getUserRoleWithPartnerConstraint(resource, user)) //
                .add(getEndorsementActionFilterConstraintNew())
                .list();
    }

    public Action getViewEditAction(Resource resource) {
        return (Action) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.property("action")) //
                .createAlias("action", "action", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("state", resource.getState())) //
                .add(Restrictions.eq("action.actionCategory", PrismActionCategory.VIEW_EDIT_RESOURCE)) //
                .uniqueResult();
    }

    public List<PrismAction> getEscalationActions() {
        return (List<PrismAction>) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.groupProperty("action.id")) //
                .createAlias("action", "action", JoinType.INNER_JOIN) //
                .createAlias("action.scope", "scope", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("action.actionCategory", ESCALATE_RESOURCE)) //
                .addOrder(Order.desc("scope.ordinal")) //
                .list();
    }

    public List<Action> getCustomizableActions() {
        return (List<Action>) sessionFactory.getCurrentSession().createCriteria(Action.class) //
                .add(Restrictions.eq("customizableAction", true)) //
                .list();
    }

    public List<Action> getConfigurableActions() {
        return (List<Action>) sessionFactory.getCurrentSession().createCriteria(Action.class) //
                .add(Restrictions.eq("configurableAction", true)) //
                .list();
    }

    public List<PrismAction> getStateGroupTransitionActions() {
        return (List<PrismAction>) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.groupProperty("action.id")) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("stateTransitions", "stateTransition", JoinType.INNER_JOIN) //
                .createAlias("stateTransition.transitionState", "transitionState", JoinType.INNER_JOIN) //
                .add(Restrictions.neProperty("state.stateGroup", "transitionState.stateGroup")) //
                .list();
    }

    public void setStateGroupTransitionActions(List<PrismAction> actions) {
        sessionFactory.getCurrentSession().createQuery( //
                "update Action " //
                        + "set transitionAction = true " //
                        + "where id in (:actions)")
                .setParameterList("actions", actions) //
                .executeUpdate();
    }

    public List<ActionCreationScopeDTO> getCreationActions() {
        return (List<ActionCreationScopeDTO>) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("action"), "action") //
                        .add(Projections.groupProperty("transitionState.scope"), "creationScope"))
                .createAlias("action", "action", JoinType.INNER_JOIN) //
                .createAlias("stateTransitions", "stateTransition", JoinType.INNER_JOIN) //
                .createAlias("stateTransition.transitionState", "transitionState", JoinType.INNER_JOIN) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("action.actionCategory", CREATE_RESOURCE)) //
                        .add(Restrictions.eq("action.id", SYSTEM_STARTUP))) //
                .setResultTransformer(Transformers.aliasToBean(ActionCreationScopeDTO.class)) //
                .list();
    }

    public List<PrismActionCondition> getActionConditions(PrismScope prismScope) {
        return (List<PrismActionCondition>) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.groupProperty("actionCondition")) //
                .createAlias("action", "action") //
                .add(Restrictions.eq("action.scope.id", prismScope)) //
                .add(Restrictions.isNotNull("actionCondition")) //
                .list();
    }

    public List<PrismActionCondition> getExternalConditions(Resource resource) {
        return (List<PrismActionCondition>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.groupProperty("resourceCondition.actionCondition")) //
                .createAlias(resource.getResourceScope().getLowerCamelName(), "resource", JoinType.INNER_JOIN) //
                .createAlias("resource.resourceConditions", "resourceCondition", JoinType.INNER_JOIN) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN,
                        Restrictions.eqProperty("stateAction.actionCondition", "resourceCondition.actionCondition")) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .createAlias("action.creationScope", "creationScope", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("resource.id", resource.getId())) //
                .add(Restrictions.eq("resourceCondition.externalMode", true)) //
                .add(Restrictions.eq("action.systemInvocationOnly", false)) //
                .addOrder(Order.desc("creationScope.ordinal")) //
                .list();
    }

    public List<PrismActionEnhancement> getExpectedDefaultActionEnhancements(Resource resource, Action action) {
        return (List<PrismActionEnhancement>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.groupProperty("stateAction.actionEnhancement"))
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
                .add(Restrictions.eq("stateAction.action", action)) //
                .add(Restrictions.isNotNull("stateAction.actionEnhancement"))
                .list();
    }

    public List<PrismActionEnhancement> getExpectedCustomActionEnhancements(Resource resource, Action action) {
        return (List<PrismActionEnhancement>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.groupProperty("stateActionAssignment.actionEnhancement"))
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
                .add(Restrictions.eq("stateAction.action", action)) //
                .add(Restrictions.isNotNull("stateActionAssignment.actionEnhancement"))
                .list();
    }

}
