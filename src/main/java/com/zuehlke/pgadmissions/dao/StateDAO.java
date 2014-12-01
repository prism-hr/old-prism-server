package com.zuehlke.pgadmissions.dao;

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

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceState;
import com.zuehlke.pgadmissions.domain.resource.ResourceStateTransitionSummary;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.domain.workflow.StateAction;
import com.zuehlke.pgadmissions.domain.workflow.StateGroup;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.domain.workflow.StateTransitionPending;
import com.zuehlke.pgadmissions.dto.StateTransitionPendingDTO;
import com.zuehlke.pgadmissions.rest.representation.resource.ActionRepresentation.NextStateRepresentation;

@Repository
@SuppressWarnings("unchecked")
public class StateDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public List<StateTransition> getStateTransitions(State state, Action action) {
        return (List<StateTransition>) sessionFactory.getCurrentSession().createCriteria(StateTransition.class) //
                .createAlias("stateAction", "stateAction", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("stateAction.state", state)) //
                .add(Restrictions.eq("stateAction.action", action)) //
                .list();
    }

    public StateTransition getStateTransition(Resource resource, Action action, PrismState transitionStateId) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StateTransition.class) //
                .createAlias("stateAction", "stateAction") //
                .createAlias("stateAction.state", "state") //
                .createAlias("state.resourceStates", "resourceState", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("stateAction.action", action)) //
                .add(Restrictions.eq("resourceState." + resource.getResourceScope().getLowerCaseName(), resource));

        if (transitionStateId == null) {
            criteria.add(Restrictions.isNull("transitionState"));
        } else {
            criteria.add(Restrictions.eq("transitionState.id", transitionStateId));
        }

        return (StateTransition) criteria.addOrder(Order.desc("resourceState.primaryState")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public List<StateTransitionPendingDTO> getStateTransitionsPending(PrismScope scopeId) {
        String scopeReference = scopeId.getLowerCaseName();
        return (List<StateTransitionPendingDTO>) sessionFactory.getCurrentSession().createCriteria(StateTransitionPending.class, "stateTransitionPending") //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("id"), "id") //
                        .add(Projections.property(scopeReference + ".id"), "resourceId")) //
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

    public void deleteObseleteStateDurations() {
        sessionFactory.getCurrentSession().createQuery( //
                "delete StateDurationConfiguration " //
                        + "where stateDurationDefinition not in ( " //
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
                .addOrder(Order.asc("creationScope.precedence")) //
                .addOrder(Order.asc("scope.precedence")) //
                .addOrder(Order.asc("stateGroup.sequenceOrder")) //
                .list();
    }

    public List<PrismState> getActiveProgramStates() {
        return (List<PrismState>) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.groupProperty("state.id")) //
                .add(Restrictions.eq("action.id", PrismAction.PROGRAM_CREATE_APPLICATION)) //
                .list();
    }

    public List<PrismState> getActiveProjectStates() {
        return (List<PrismState>) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.groupProperty("state.id")) //
                .add(Restrictions.eq("action.id", PrismAction.PROJECT_CREATE_APPLICATION)) //
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
                .add(Restrictions.eq(resource.getResourceScope().getLowerCaseName(), resource)) //
                .addOrder(Order.desc("primaryState")) //
                .list();
    }

    public String getRecommendedNextStates(Resource resource) {
        Resource parentResource = resource.getParentResource();
        return (String) sessionFactory.getCurrentSession().createCriteria(ResourceStateTransitionSummary.class) //
                .setProjection(Projections.property("transitionStateSelection")) //
                .add(Restrictions.eq(parentResource.getResourceScope().getLowerCaseName(), parentResource)) //
                .add(Restrictions.eq("stateGroup", resource.getState().getStateGroup())) //
                .add(Restrictions.ge("frequency", 3)) //
                .addOrder(Order.desc("frequency")) //
                .addOrder(Order.desc("updatedTimestamp")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public List<State> getResourceStates(Resource resource) {
        return (List<State>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.property("state")) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCaseName(), resource)) //
                .addOrder(Order.desc("primaryState")) //
                .list();
    }

    public List<PrismStateGroup> getSecondaryResourceStateGroups(PrismScope resourceScope, Integer resourceId) {
        return (List<PrismStateGroup>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.property("state.stateGroup.id")) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .add(Restrictions.eq(resourceScope.getLowerCaseName() + ".id", resourceId)) //
                .add(Restrictions.eq("primaryState", false)) //
                .list();
    }

    public List<NextStateRepresentation> getSelectableTransitionStates(State state, PrismAction actionId) {
        return (List<NextStateRepresentation>) sessionFactory.getCurrentSession().createCriteria(StateTransition.class, "stateTransition") //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("transitionState.id"), "state") //
                        .add(Projections.property("transitionState.parallelizable"), "parallelizable")) //
                .createAlias("stateAction", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("transitionState", "transitionState", JoinType.INNER_JOIN) //
                .createAlias("transitionState.stateGroup", "transitionStateGroup", JoinType.INNER_JOIN) //
                .createAlias("stateTransitionEvaluation", "stateTransitionEvaluation", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("stateAction.state", state)) //
                .add(Restrictions.eq("stateAction.action.id", actionId)) //
                .add(Restrictions.eq("stateTransitionEvaluation.nextStateSelection", true)) //
                .add(Restrictions.isNotNull("transitionState")) //
                .addOrder(Order.asc("transitionStateGroup.sequenceOrder")) //
                .setResultTransformer(Transformers.aliasToBean(NextStateRepresentation.class)) //
                .list();
    }

    public List<NextStateRepresentation> getSelectableTransitionStates(State state) {
        return (List<NextStateRepresentation>) sessionFactory.getCurrentSession().createCriteria(StateTransition.class, "stateTransition") //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("transitionState.id"), "state") //
                        .add(Projections.property("transitionState.parallelizable"), "parallelizable")) //
                .createAlias("stateAction", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("transitionState", "transitionState", JoinType.INNER_JOIN) //
                .createAlias("transitionState.stateGroup", "transitionStateGroup", JoinType.INNER_JOIN) //
                .createAlias("stateTransitionEvaluation", "stateTransitionEvaluation", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("stateAction.state", state)) //
                .add(Restrictions.eq("stateTransitionEvaluation.nextStateSelection", true)) //
                .add(Restrictions.isNotNull("transitionState")) //
                .addOrder(Order.asc("transitionStateGroup.sequenceOrder")) //
                .setResultTransformer(Transformers.aliasToBean(NextStateRepresentation.class)) //
                .list();
    }

    public List<PrismStateGroup> getStateGroups(PrismScope scopeId) {
        return (List<PrismStateGroup>) sessionFactory.getCurrentSession().createCriteria(StateGroup.class) //
                .setProjection(Projections.property("id")) //
                .add(Restrictions.eq("scope.id", scopeId)) //
                .addOrder(Order.asc("sequenceOrder")) //
                .list();
    }

}
