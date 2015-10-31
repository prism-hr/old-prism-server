package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_STARTUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.CREATE_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.ESCALATE_RESOURCE;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
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

    public List<Action> getActions(Resource resource) {
        return (List<Action>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class)
                .setProjection(Projections.groupProperty("stateAction.action"))
                .createAlias("state", "state", JoinType.INNER_JOIN)
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN)
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource))
                .list();
    }

    public Action getPermittedUnsecuredAction(boolean userLoggedIn, Resource resource, Action action) {
        return (Action) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.groupProperty("stateAction.action")) //
                .createAlias(resource.getResourceScope().getLowerCamelName(), "resource", JoinType.INNER_JOIN) //
                .createAlias("resource.resourceConditions", "resourceCondition", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .createAlias("action.creationScope", "creationScope", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("resource.id", resource.getId())) //
                .add(Restrictions.eq("stateAction.action", action)) //
                .add(Restrictions.eq("action.systemInvocationOnly", false)) //
                .add(Restrictions.isEmpty("stateAction.stateActionAssignments")) //
                .add(getUnsecuredActionVisibilityConstraint(userLoggedIn)) //
                .addOrder(Order.asc("creationScope.ordinal")) //
                .uniqueResult();
    }

    public List<ActionDTO> getPermittedUnsecuredActions(PrismScope resourceScope, Collection<Integer> resourceIds, boolean userLoggedIn) {
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
                .add(getUnsecuredActionVisibilityConstraint(userLoggedIn)) //
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

    public <T> List<T> getActionEntities(User user, PrismScope scope, Collection<Integer> resources, ProjectionList columns, Class<T> responseClass) {
        return getActionEntities(user, scope, resources, null, columns, responseClass);
    }

    public <T> List<T> getActionEntities(User user, PrismScope scope, Collection<Integer> resources, PrismAction action, ProjectionList columns, Class<T> responseClass) {
        return workflowDAO.getWorkflowCriteriaList(scope, columns) //
                .add(getActionConstraint(user, resources, action)) //
                .setResultTransformer(Transformers.aliasToBean(responseClass)) //
                .list();
    }

    public <T> List<T> getActionEntities(User user, PrismScope scope, PrismScope parentScope, Collection<Integer> resources, ProjectionList columns, Class<T> responseClass) {
        return getActionEntities(user, scope, parentScope, resources, null, columns, responseClass);
    }

    public <T> List<T> getActionEntities(User user, PrismScope scope, PrismScope parentScope, Collection<Integer> resources, PrismAction action, ProjectionList columns,
            Class<T> responseClass) {
        return workflowDAO.getWorkflowCriteriaList(scope, parentScope, columns) //
                .add(getActionConstraint(user, resources, action)) //
                .setResultTransformer(Transformers.aliasToBean(responseClass)) //
                .list();
    }

    public <T> List<T> getActionEntities(User user, PrismScope scope, PrismScope targeterScope, PrismScope targetScope, Collection<Integer> targeterEntities,
            Collection<Integer> resources, ProjectionList columns, Class<T> responseClass) {
        return getActionEntities(user, scope, targeterScope, targetScope, targeterEntities, resources, null, columns, responseClass);
    }

    public <T> List<T> getActionEntities(User user, PrismScope scope, PrismScope targeterScope, PrismScope targetScope, Collection<Integer> targeterEntities,
            Collection<Integer> resources, PrismAction action, ProjectionList columns, Class<T> responseClass) {
        return workflowDAO.getWorkflowCriteriaList(scope, targeterScope, targetScope, targeterEntities, columns)
                .add(getActionConstraint(user, resources, action))
                .setResultTransformer(Transformers.aliasToBean(responseClass)) //
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

    private static Junction getActionConstraint(User user, Collection<Integer> resources, PrismAction action) {
        Junction constraint = Restrictions.conjunction() //
                .add(Restrictions.in("resource.id", resources)) //
                .add(Restrictions.eq("userRole.user", user)) //
                .add(WorkflowDAO.getUserEnabledConstraint(user)); //

        if (action != null) {
            constraint.add(Restrictions.eq("action.id", action));
        }

        return constraint;
    }

    private static Junction getUnsecuredActionVisibilityConstraint(boolean userLoggedIn) {
        Junction conditionConstraint = Restrictions.conjunction() //
                .add(Restrictions.eqProperty("resourceCondition.actionCondition", "stateAction.actionCondition"));
        if (userLoggedIn) {
            conditionConstraint.add(Restrictions.disjunction() //
                    .add(Restrictions.eq("resourceCondition.internalMode", true)) //
                    .add(Restrictions.eq("resourceCondition.externalMode", true)));
        } else {
            conditionConstraint.add(Restrictions.eq("resourceCondition.externalMode", true));
        }

        return Restrictions.disjunction() //
                .add(Restrictions.isNull("resourceCondition.id")) //
                .add(Restrictions.isNull("stateAction.actionCondition")) //
                .add(conditionConstraint);
    }

}
