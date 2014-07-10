package com.zuehlke.pgadmissions.dao;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.IUniqueEntity;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.RoleTransition;
import com.zuehlke.pgadmissions.domain.Scope;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.StateActionAssignment;
import com.zuehlke.pgadmissions.domain.StateActionEnhancement;
import com.zuehlke.pgadmissions.domain.StateActionNotification;
import com.zuehlke.pgadmissions.domain.StateDuration;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.StateTransitionPending;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;

@Repository
@SuppressWarnings("unchecked")
public class StateDAO {

    private static final Set<Class<? extends IUniqueEntity>> workflowConfigurationClasses = Sets.newLinkedHashSet();

    static {
        workflowConfigurationClasses.add(RoleTransition.class);
        workflowConfigurationClasses.add(StateTransition.class);
        workflowConfigurationClasses.add(StateActionEnhancement.class);
        workflowConfigurationClasses.add(StateActionAssignment.class);
        workflowConfigurationClasses.add(StateActionNotification.class);
        workflowConfigurationClasses.add(StateAction.class);
    }

    @Autowired
    private ScopeDAO scopeDAO;

    @Autowired
    private SessionFactory sessionFactory;

    public List<State> getActiveStates() {
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

    public StateTransition getStateTransition(List<StateTransition> permittedTransitions, State candidateTransitionState) {
        return (StateTransition) sessionFactory.getCurrentSession().createCriteria(StateTransition.class) //
                .add(Restrictions.in("id", permittedTransitions)) //
                .add(Restrictions.eq("transitionState", candidateTransitionState)) //
                .uniqueResult();
    }

    public StateDuration getStateDuration(Resource resource, State state) {
        return (StateDuration) sessionFactory.getCurrentSession().createCriteria(StateDuration.class) //
                .add(Restrictions.eq("state", state)) //
                .add(Restrictions.disjunction().add(Restrictions.conjunction() //
                        .add(Restrictions.eq("system", resource.getSystem())) //
                        .add(Restrictions.isNull("institution")) //
                        .add(Restrictions.isNull("program"))) //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.eq("institution", resource.getInstitution())) //
                                .add(Restrictions.isNull("program"))) //
                        .add(Restrictions.eq("program", resource.getProgram()))) //
                .addOrder(Order.desc("system")) //
                .addOrder(Order.desc("institution")) //
                .addOrder(Order.desc("program")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public List<StateTransitionPending> getPendingStateTransitions() {
        List<Scope> scopes = scopeDAO.getScopesDescending();

        List<StateTransitionPending> pendingStateTransitions = Lists.newArrayList();
        for (Scope scope : scopes) {
            String scopeName = scope.getId().getLowerCaseName();

            pendingStateTransitions.addAll(sessionFactory.getCurrentSession().createCriteria(StateTransitionPending.class) //
                    .add(Restrictions.isNotNull(scopeName)) //
                    .addOrder(Order.asc(scopeName + ".id")) //
                    .addOrder(Order.asc("id")) //
                    .list());
        }

        return pendingStateTransitions;
    }

    public HashMultimap<Action, Resource> getPropagatedStateTransitions(StateTransitionPending pendingStateTransition) {
        HashMultimap<Action, Resource> propagations = HashMultimap.create();
        for (Action propagateAction : pendingStateTransition.getStateTransition().getPropagatedActions()) {
            String propagateResourceName = propagateAction.getScope().getId().getLowerCaseName();
            String propagateResourceReference = propagateResourceName;

            if (pendingStateTransition.getStateTransition().getStateAction().getState().getScope().getPrecedence() > propagateAction.getScope().getPrecedence()) {
                propagateResourceReference = propagateResourceName + "s";
            }

            propagations.putAll(propagateAction, sessionFactory.getCurrentSession() //
                    .createCriteria(propagateAction.getScope().getClass()) //
                    .createAlias(propagateResourceReference, propagateResourceName, JoinType.INNER_JOIN) //
                    .createAlias(propagateResourceName + "state", "state", JoinType.INNER_JOIN) //
                    .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                    .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                    .add(Restrictions.eq("action", propagateAction)).list());
        }

        return propagations;
    }

    public HashMultimap<Action, Resource> getEscalatedStateTransitions() {
        List<Action> escalateActions = sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.property("action")) //
                .createAlias("action", "action", JoinType.INNER_JOIN) //
                .createAlias("action.scope", "scope", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("action.actionType", PrismActionType.SYSTEM_ESCALATION)) //
                .addOrder(Order.desc("scope.precedence")) //
                .list();

        HashMultimap<Action, Resource> escalations = HashMultimap.create();
        for (Action escalateAction : escalateActions) {

            escalations.putAll(escalateAction, sessionFactory.getCurrentSession() //
                    .createCriteria(escalateAction.getScope().getId().getClass()) //
                    .createAlias("state", "state", JoinType.INNER_JOIN) //
                    .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                    .createAlias("stateAction.action", "action") //
                    .add(Restrictions.eq("action", escalateAction)) //
                    .list());
        }

        return escalations;
    }

    public void deleteStateActions() {
        for (Class<? extends IUniqueEntity> workflowConfigurationClass : workflowConfigurationClasses) {
            sessionFactory.getCurrentSession().createQuery( //
                    "delete " + workflowConfigurationClass.getSimpleName()) //
                    .executeUpdate();
        }
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

    public State getDegradationState(State state) {
        return (State) sessionFactory.getCurrentSession().createCriteria(State.class) //
                .add(Restrictions.le("sequenceOrder", state.getParentState().getSequenceOrder())) //
                .addOrder(Order.desc("sequenceOrder")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public List<State> getOrderedTransitionStates(State state, State... excludedTransitionStates) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.groupProperty("stateTransition.transitionState")) //
                .createAlias("stateTransitions", "stateTransition", JoinType.INNER_JOIN) //
                .createAlias("stateTransition.transitionState", "transitionState", JoinType.INNER_JOIN) //
                .createAlias("transitionState.parentState", "parentState", JoinType.INNER_JOIN) //
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
                .addOrder(Order.asc("parentState.sequenceOrder")) //
                .list();
    }

    public List<State> getWorkflowStates() {
        return (List<State>) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.groupProperty("state")) //
                .list();
    }

    public List<State> getRootState() {
        return (List<State>) sessionFactory.getCurrentSession().createCriteria(State.class) //
                .add(Restrictions.eq("initialState", true)).add(Restrictions.eq("finalState", true)) //
                .list();
    }

    public List<State> getUpstreamStates(State state) {
        return (List<State>) sessionFactory.getCurrentSession().createCriteria(State.class) //
                .setProjection(Projections.groupProperty("stateAction.state")) //
                .createAlias("inverseStateTransitions", "stateTransition", JoinType.INNER_JOIN) //
                .createAlias("stateTransition.stateAction", "stateAction", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("id", state.getId())).list();
    }

    public List<State> getDownstreamStates(State state) {
        return (List<State>) sessionFactory.getCurrentSession().createCriteria(State.class) //
                .setProjection(Projections.groupProperty("stateTransition.transitionState")) //
                .createAlias("stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateTransitions", "stateTransition", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("id", state.getId())).list();
    }

    public List<PrismState> getActionableStates(Collection<PrismAction> actions) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.groupProperty("state.id")) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("action", "action", JoinType.INNER_JOIN);

        Disjunction disjunction = Restrictions.disjunction();

        for (PrismAction action : actions) {
            disjunction.add(Restrictions.eq("action.id", action));
        }

        return criteria.add(disjunction).list();
    }

}
