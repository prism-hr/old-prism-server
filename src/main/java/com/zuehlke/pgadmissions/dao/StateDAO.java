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

import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.StateDuration;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.StateTransitionPending;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;
import com.zuehlke.pgadmissions.dto.StateTransitionPendingDTO;

@Repository
@SuppressWarnings("unchecked")
public class StateDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public List<State> getConfigurableStates() {
        return (List<State>) sessionFactory.getCurrentSession().createCriteria(StateDuration.class) //
                .setProjection(Projections.property("state")) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .add(Restrictions.isNotNull("system")) //
                .list();
    }

    public List<StateTransition> getStateTransitions(Resource resource, Action action) {
        return (List<StateTransition>) sessionFactory.getCurrentSession().createCriteria(StateTransition.class) //
                .createAlias("stateAction", "stateAction", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("stateAction.state", resource.getState())) //
                .add(Restrictions.eq("stateAction.action", action)) //
                .list();
    }

    public StateTransition getStateTransition(State state, Action action, PrismState transitionStateId) {
        return (StateTransition) sessionFactory.getCurrentSession().createCriteria(StateTransition.class) //
                .createAlias("stateAction", "stateAction") //
                .add(Restrictions.eq("stateAction.state", state)) //
                .add(Restrictions.eq("stateAction.action", action)) //
                .add(Restrictions.eq("transitionState.id", transitionStateId)) //
                .uniqueResult();
    }

    public StateDuration getStateDuration(Resource resource, State state) {
        return (StateDuration) sessionFactory.getCurrentSession().createCriteria(StateDuration.class) //
                .add(Restrictions.eq("state", state)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("system", resource.getSystem())) //
                        .add(Restrictions.eq("institution", resource.getInstitution())) //
                        .add(Restrictions.eq("program", resource.getProgram()))) //
                .addOrder(Order.desc("program")) //
                .addOrder(Order.desc("institution")) //
                .addOrder(Order.desc("system")) //
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
                .setResultTransformer(Transformers.aliasToBean(StateTransitionPendingDTO.class))
                .list();
    }

    public void deleteObseleteStateDurations(List<State> activeStates) {
        sessionFactory.getCurrentSession().createQuery( //
                "delete StateDuration " //
                        + "where state not in (:configurableStates)") //
                .setParameterList("configurableStates", activeStates) //
                .executeUpdate();
    }

    public <T extends Resource> List<State> getDeprecatedStates(Class<T> resourceClass) {
        return (List<State>) sessionFactory.getCurrentSession().createCriteria(resourceClass) //
                .setProjection(Projections.groupProperty("state")) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .add(Restrictions.isEmpty("state.stateActions")) //
                .list();
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

    public List<PrismState> getAvailableNextStates(Resource resource, PrismAction actionId) {
        return sessionFactory.getCurrentSession().createCriteria(StateTransition.class) //
                .setProjection(Projections.property("transitionState.id")) //
                .createAlias("stateAction", "stateAction") //
                .add(Restrictions.eq("stateAction.state", resource.getState())) //
                .add(Restrictions.eq("stateAction.action.id", actionId)) //
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
    
    public List<State> getWorkflowStates() {
        return (List<State>) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.groupProperty("state")) //
                .list();
    }
    
    public List<PrismStateGroup> getAssignableStateGroups(PrismScope scopeId) {
        return (List<PrismStateGroup>) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.groupProperty("stateGroup.id")) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateGroup", "stateGroup", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("stateGroup.scope.id", scopeId)) //
                .addOrder(Order.asc("stateGroup.sequenceOrder")) //
                .list();  
    }

}
