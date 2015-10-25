package com.zuehlke.pgadmissions.dao;

import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceOpportunity;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.resource.ResourceState;
import com.zuehlke.pgadmissions.domain.resource.ResourceStateTransitionSummary;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.domain.workflow.StateAction;
import com.zuehlke.pgadmissions.domain.workflow.StateActionPending;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.domain.workflow.StateTransitionPending;
import com.zuehlke.pgadmissions.dto.ResourceStateDTO;
import com.zuehlke.pgadmissions.dto.StateSelectableDTO;
import com.zuehlke.pgadmissions.dto.StateTransitionDTO;
import com.zuehlke.pgadmissions.dto.StateTransitionPendingDTO;

@Repository
@SuppressWarnings("unchecked")
public class StateDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public List<StateTransition> getPotentialStateTransitions(Resource resource, Action action) {
        return (List<StateTransition>) sessionFactory.getCurrentSession().createCriteria(StateTransition.class) //
                .createAlias("stateAction", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.state", "state", JoinType.INNER_JOIN) //
                .createAlias("state." + resource.getResourceScope().getLowerCamelName() + "s", "resource", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("resource.id", resource.getId())) //
                .add(Restrictions.eq("stateAction.action", action)) //
                .add(Restrictions.isNotNull("transitionState")) //
                .list();
    }

    public List<StateTransition> getPotentialUserStateTransitions(Resource resource, Action action) {
        return (List<StateTransition>) sessionFactory.getCurrentSession().createCriteria(StateTransition.class) //
                .createAlias("stateAction", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.resourceStates", "resourceState", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("resourceState." + resource.getResourceScope().getLowerCamelName(), resource)) //
                .add(Restrictions.eq("stateAction.action", action)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.eq("resourceState.primaryState", true)) //
                                .add(Restrictions.isNotNull("transitionState"))) //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.eq("resourceState.primaryState", false)) //
                                .add(Restrictions.isNull("transitionState")))) //
                .list();
    }

    public StateTransition getSecondaryStateTransition(Resource resource, PrismState state, Action action) {
        return (StateTransition) sessionFactory.getCurrentSession().createCriteria(StateTransition.class) //
                .createAlias("stateAction", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.state", "state", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("stateAction.state", resource.getPreviousState())) //
                .add(Restrictions.eq("stateAction.action", action)) //
                .add(Restrictions.eq("transitionState.id", state)) //
                .uniqueResult();
    }

    public StateTransition getStateTransition(Resource resource, Action action) {
        return (StateTransition) sessionFactory.getCurrentSession().createCriteria(StateTransition.class) //
                .createAlias("stateAction", "stateAction") //
                .createAlias("stateAction.state", "state") //
                .createAlias("state.resourceStates", "resourceState", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("stateAction.action", action)) //
                .add(Restrictions.eq("resourceState." + resource.getResourceScope().getLowerCamelName(), resource)) //
                .add(Restrictions.isNull("transitionState")) //
                .addOrder(Order.desc("resourceState.primaryState")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public StateTransition getStateTransition(Resource resource, Action action, PrismState transitionStateId) {
        return (StateTransition) sessionFactory.getCurrentSession().createCriteria(StateTransition.class) //
                .createAlias("stateAction", "stateAction") //
                .createAlias("stateAction.state", "state") //
                .createAlias("state.resourceStates", "resourceState", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("stateAction.action", action)) //
                .add(Restrictions.eq("resourceState." + resource.getResourceScope().getLowerCamelName(), resource))
                .add(Restrictions.eq("transitionState.id", transitionStateId)) //
                .addOrder(Order.desc("resourceState.primaryState")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public List<StateTransitionPendingDTO> getStateTransitionsPending(PrismScope scopeId) {
        String scopeReference = scopeId.getLowerCamelName();
        return (List<StateTransitionPendingDTO>) sessionFactory.getCurrentSession().createCriteria(StateTransitionPending.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("id"), "id") //
                        .add(Projections.property(scopeReference + ".id"), "resourceId") //
                        .add(Projections.property("action.id"), "actionId")) //
                .add(Restrictions.isNotNull(scopeReference)) //
                .addOrder(Order.asc(scopeReference + ".id")) //
                .addOrder(Order.asc("id")) //
                .setResultTransformer(Transformers.aliasToBean(StateTransitionPendingDTO.class)).list();
    }

    public List<State> getConfigurableStates() {
        return (List<State>) sessionFactory.getCurrentSession().createCriteria(State.class) //
                .add(Restrictions.isNotNull("stateDurationDefinition")) //
                .list();
    }

    public void deleteObsoleteStateDurations() {
        sessionFactory.getCurrentSession().createQuery( //
                "delete StateDurationConfiguration " //
                        + "where definition not in ( " //
                        + "select stateDurationDefinition " //
                        + "from State " //
                        + "group by stateDurationDefinition)") //
                .executeUpdate();
    }

    public List<State> getOrderedTransitionStates(State state, State... excludedTransitionStates) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.groupProperty("stateTransition.transitionState")) //
                .createAlias("stateTransitions", "stateTransition", JoinType.INNER_JOIN) //
                .createAlias("stateTransition.transitionState", "transitionState", JoinType.INNER_JOIN) //
                .createAlias("transitionState.stateGroup", "stateGroup", JoinType.INNER_JOIN) //
                .createAlias("transitionState.scope", "scope", JoinType.INNER_JOIN) //
                .createAlias("action", "action", JoinType.INNER_JOIN) //
                .createAlias("action.creationScope", "creationScope", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.eq("state", state));

        for (State excludedTransitionState : excludedTransitionStates) {
            criteria.add(Restrictions.ne("stateTransition.transitionState", excludedTransitionState));
        }

        return (List<State>) criteria //
                .addOrder(Order.asc("creationScope.ordinal")) //
                .addOrder(Order.asc("scope.ordinal")) //
                .addOrder(Order.asc("stateGroup.ordinal")) //
                .list();
    }

    public List<PrismState> getResourceStates(PrismScope resourceScope) {
        return (List<PrismState>) sessionFactory.getCurrentSession().createCriteria(State.class) //
                .setProjection(Projections.groupProperty("id")) //
                .add(Restrictions.eq("scope.id", resourceScope)) //
                .list();
    }

    public List<PrismState> getActiveResourceStates(PrismScope resourceScope) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.groupProperty("state.id")) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("action", "action", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("action.scope.id", resourceScope)) //
                .add(Restrictions.isNull("state.hidden"));

        Class<? extends Resource> resourceClass = resourceScope.getResourceClass();
        if (ResourceParent.class.isAssignableFrom(resourceClass)) {
            String resourceName = resourceScope.name();
            if (ResourceOpportunity.class.isAssignableFrom(resourceClass)) {
                criteria.add(Restrictions.ne("state.id", PrismState.valueOf(resourceName + "_APPROVAL_PARENT_APPROVAL")));
            }
            criteria.add(Restrictions.ne("state.id", PrismState.valueOf(resourceName + "_APPROVAL")));
            criteria.add(Restrictions.ne("state.id", PrismState.valueOf(resourceName + "_APPROVAL_PENDING_CORRECTION")));
        }

        return (List<PrismState>) criteria.add(Restrictions.isNotNull("action.creationScope")) //
                .list();
    }

    public List<PrismState> getStatesByStateGroup(PrismStateGroup stateGroupId) {
        return (List<PrismState>) sessionFactory.getCurrentSession().createCriteria(State.class) //
                .setProjection(Projections.property("id")) //
                .add(Restrictions.eq("stateGroup.id", stateGroupId)) //
                .list();
    }

    public List<State> getCurrentStates(Resource resource) {
        return (List<State>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.property("state")) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
                .addOrder(Order.desc("primaryState")) //
                .list();
    }

    public String getRecommendedNextStates(Resource resource) {
        Resource parentResource = resource.getParentResource();
        return (String) sessionFactory.getCurrentSession().createCriteria(ResourceStateTransitionSummary.class) //
                .setProjection(Projections.property("transitionStateSelection")) //
                .add(Restrictions.eq(parentResource.getResourceScope().getLowerCamelName(), parentResource)) //
                .add(Restrictions.eq("stateGroup", resource.getState().getStateGroup())) //
                .add(Restrictions.ge("frequency", 3)) //
                .addOrder(Order.desc("frequency")) //
                .addOrder(Order.desc("updatedTimestamp")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public List<PrismState> getSecondaryResourceStates(Resource resource) {
        return (List<PrismState>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.property("state.id")) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
                .add(Restrictions.eq("primaryState", false)) //
                .list();
    }

    public List<ResourceStateDTO> getSecondaryResourceStates(PrismScope resourceScope, Collection<Integer> resourceIds) {
        String resourceIdReference = resourceScope.getLowerCamelName() + ".id";
        return (List<ResourceStateDTO>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property(resourceIdReference), "resourceId") //
                        .add(Projections.property("state.id"), "stateId")) //
                .add(Restrictions.in(resourceIdReference, resourceIds)) //
                .add(Restrictions.eq("primaryState", false)) //
                .addOrder(Order.asc(resourceIdReference)) //
                .addOrder(Order.asc("state.id")) //
                .setResultTransformer(Transformers.aliasToBean(ResourceStateDTO.class)) //
                .list();
    }

    public List<StateSelectableDTO> getSelectableTransitionStates(State state, PrismAction actionId) {
        return (List<StateSelectableDTO>) sessionFactory.getCurrentSession().createCriteria(StateTransition.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("transitionState.id"), "state") //
                        .add(Projections.property("transitionState.parallelizable"), "parallelizable")) //
                .createAlias("stateAction", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("transitionState", "transitionState", JoinType.INNER_JOIN) //
                .createAlias("transitionState.stateGroup", "transitionStateGroup", JoinType.INNER_JOIN) //
                .createAlias("stateTransitionEvaluation", "stateTransitionEvaluation", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("stateAction.state", state)) //
                .add(Restrictions.eq("stateAction.action.id", actionId)) //
                .add(Restrictions.eq("stateTransitionEvaluation.nextStateSelection", true)) //
                .add(Restrictions.isNotNull("transitionState")) //
                .addOrder(Order.asc("transitionStateGroup.ordinal")) //
                .setResultTransformer(Transformers.aliasToBean(StateSelectableDTO.class)) //
                .list();
    }

    public List<StateTransitionDTO> getStateTransitions() {
        return (List<StateTransitionDTO>) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("state"), "state") //
                        .add(Projections.property("action"), "action") //
                        .add(Projections.property("stateTransition.transitionState"), "transitionState")) //
                .createAlias("action", "action", JoinType.INNER_JOIN) //
                .createAlias("stateTransitions", "stateTransition", JoinType.INNER_JOIN) //
                .add(Restrictions.isNotNull("action.transitionAction")) //
                .add(Restrictions.isNotNull("stateTransition.transitionState")) //
                .addOrder(Order.asc("state")) //
                .addOrder(Order.asc("action")) //
                .addOrder(Order.asc("stateTransition.transitionState")) //
                .setResultTransformer(Transformers.aliasToBean(StateTransitionDTO.class)) //
                .list();
    }

    public void setHiddenStates(List<PrismState> states) {
        sessionFactory.getCurrentSession().createQuery( //
                "update State " //
                        + "set hidden = true " //
                        + "where id in (:states)")
                .setParameterList("states", states) //
                .executeUpdate();
    }

    public List<PrismState> getParallelizableStates() {
        return (List<PrismState>) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.groupProperty("state.id")) //
                .createAlias("stateTransitions", "stateTransition", JoinType.INNER_JOIN) //
                .add(Restrictions.isNull("stateTransition.transitionState")) //
                .list();
    }

    public void setParallelizableStates(List<PrismState> states) {
        sessionFactory.getCurrentSession().createQuery( //
                "update State " //
                        + "set parallelizable = true " //
                        + "where id in (:states)")
                .setParameterList("states", states) //
                .executeUpdate();
    }

    public PrismState getPreviousPrimaryState(Resource resource, PrismState currentState) {
        return (PrismState) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .setProjection(Projections.property("transitionState.id")) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
                .add(Restrictions.ne("transitionState.id", currentState)) //
                .addOrder(Order.desc("id")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public List<Integer> getStateActionPendings() {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(StateActionPending.class) //
                .setProjection(Projections.property("id")) //
                .addOrder(Order.asc("id")) //
                .list();
    }

    public void deleteStateActionPendings(Resource resource) {
        sessionFactory.getCurrentSession().createQuery( //
                "delete StateActionPending "
                        + resource.getResourceScope().getLowerCamelName() + " = :resource") //
                .setParameter("resource", resource) //
                .executeUpdate();
    }

    public void deleteStateActionPendings(Resource resource, List<Action> actions) {
        sessionFactory.getCurrentSession().createQuery( //
                "delete StateActionPending "
                        + resource.getResourceScope().getLowerCamelName() + " = :resource "
                        + "and action not in (:actions)") //
                .setParameter("resource", resource) //
                .setParameterList("actions", actions) //
                .executeUpdate();
    }

}
