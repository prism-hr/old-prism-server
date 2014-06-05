package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.PrismResource;
import com.zuehlke.pgadmissions.domain.PrismResourceDynamic;
import com.zuehlke.pgadmissions.domain.Scope;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.StateDuration;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.StateTransitionPending;
import com.zuehlke.pgadmissions.domain.enums.PrismActionType;
import com.zuehlke.pgadmissions.domain.enums.PrismState;

@Repository
@SuppressWarnings("unchecked")
public class StateDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public StateDAO() {
    }

    public StateDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(State state) {
        sessionFactory.getCurrentSession().saveOrUpdate(state);
    }

    public State getById(PrismState id) {
        return (State) sessionFactory.getCurrentSession().createCriteria(State.class) //
                .add(Restrictions.eq("id", id)) //
                .uniqueResult();
    }

    public List<State> getAllConfigurableStates() {
        return (List<State>) sessionFactory.getCurrentSession().createCriteria(State.class) //
                .add(Restrictions.isNotNull("duration")) //
                .list();
    }

    public List<StateTransition> getStateTransitions(PrismResource resource, Action action) {
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

    public Integer getStateDuration(PrismResourceDynamic resource) {
        return (Integer) sessionFactory.getCurrentSession().createCriteria(StateDuration.class) //
                .setProjection(Projections.property("expiryDuration")) //
                .add(Restrictions.eq("state", resource.getState())) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.eq("system", resource.getSystem())) //
                                .add(Restrictions.isNull("institution")) //
                                .add(Restrictions.isNull("program"))) //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.eq("institution", resource.getInstitution())) //
                                .add(Restrictions.isNull("program"))) //
                        .add(Restrictions.eq("program", resource.getProgram()))) //
                .uniqueResult();
    }
    
    public List<StateTransitionPending> getPendingStateTransitions() {
        List<Scope> scopes = sessionFactory.getCurrentSession().createCriteria(Scope.class) //
                .addOrder(Order.desc("precedence")) //
                .list();
        
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
    
    public HashMultimap<Action, PrismResourceDynamic> getPropagatedStateTransitions(StateTransitionPending pendingStateTransition) {
        HashMultimap<Action, PrismResourceDynamic> propagations = HashMultimap.create();
        for (Action propagateAction : pendingStateTransition.getStateTransition().getPropagatedActions()) {
            String propagateResourceName = propagateAction.getScope().getId().getLowerCaseName();
            String propagateResourceReference = propagateResourceName;
            
            if (pendingStateTransition.getStateTransition().getStateAction().getAction().getScope().getPrecedence() > propagateAction.getScope().getPrecedence()) {
                propagateResourceReference = propagateResourceName + "s";
            }
            
            List<PrismResourceDynamic> propagateResources;
            
            try {
                propagateResources = sessionFactory.getCurrentSession() //
                        .createCriteria(propagateAction.getScope().getClass()) //
                        .createAlias(propagateResourceReference, propagateResourceName, JoinType.INNER_JOIN) //
                        .createAlias(propagateResourceName + "state", "state", JoinType.INNER_JOIN) //
                        .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                        .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                        .add(Restrictions.eq("action", propagateAction)).list();
                
                propagations.putAll(propagateAction, propagateResources);
            } catch (Exception e) {
                throw new Error("Tried to propagate an invalid prism resource", e);
            }
        }
        
        return propagations;
    }

    public HashMultimap<Action, PrismResourceDynamic> getEscalatedStateTransitions() {
        List<Action> escalateActions = sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.property("action")) //
                .createAlias("action", "action", JoinType.INNER_JOIN) //
                .createAlias("action.scope", "scope", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("action.actionType", PrismActionType.SYSTEM_ESCALATION)) //
                .addOrder(Order.desc("scope.precedence")) //
                .list();

        HashMultimap<Action, PrismResourceDynamic> escalations = HashMultimap.create();
        for (Action escalateAction : escalateActions) {
            List<PrismResourceDynamic> escalateResources;
            
            try {
                escalateResources = sessionFactory.getCurrentSession() //
                        .createCriteria(escalateAction.getScope().getClass()) //
                        .createAlias("state", "state", JoinType.INNER_JOIN) //
                        .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                        .createAlias("stateAction.action", "action") //
                        .add(Restrictions.eq("action", escalateAction)) //
                        .list();

                if (escalateResources.size() > 0) {
                    escalations.putAll(escalateAction, escalateResources);
                }
            } catch (Exception e) {
                throw new Error("Tried to escalate an invalid prism resource", e);
            }
        }

        return escalations;
    }

}
