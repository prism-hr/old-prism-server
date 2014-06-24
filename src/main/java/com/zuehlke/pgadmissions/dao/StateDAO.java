package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.Query;
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
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.ResourceDynamic;
import com.zuehlke.pgadmissions.domain.Scope;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.StateDuration;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.StateTransitionPending;
import com.zuehlke.pgadmissions.domain.enums.PrismActionType;
import com.zuehlke.pgadmissions.domain.enums.PrismScope;
import com.zuehlke.pgadmissions.domain.enums.PrismStateTransitionEvaluation;

@Repository
@SuppressWarnings("unchecked")
public class StateDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public List<State> getConfigurableStates() {
        return (List<State>) sessionFactory.getCurrentSession().createCriteria(StateDuration.class) //
                .setProjection(Projections.property("state")) //
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

    public Integer getCurrentStateDuration(ResourceDynamic resource) {
        return (Integer) sessionFactory.getCurrentSession().createCriteria(StateDuration.class) //
                .setProjection(Projections.property("duration")) //
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
    
    public Integer getStateDuration(Resource resource, State state) {
        return (Integer) sessionFactory.getCurrentSession().createCriteria(StateDuration.class) //
                .setProjection(Projections.property("duration")) //
                .add(Restrictions.eq(PrismScope.getResourceScope(resource.getClass()).getLowerCaseName(), resource)) //
                .add(Restrictions.eq("state", state)) //
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
    
    public HashMultimap<Action, ResourceDynamic> getPropagatedStateTransitions(StateTransitionPending pendingStateTransition) {
        HashMultimap<Action, ResourceDynamic> propagations = HashMultimap.create();
        for (Action propagateAction : pendingStateTransition.getStateTransition().getPropagatedActions()) {
            String propagateResourceName = propagateAction.getScope().getId().getLowerCaseName();
            String propagateResourceReference = propagateResourceName;
            
            if (pendingStateTransition.getStateTransition().getStateAction().getAction().getScope().getPrecedence() > propagateAction.getScope().getPrecedence()) {
                propagateResourceReference = propagateResourceName + "s";
            }
            
            List<ResourceDynamic> propagateResources;
            
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

    public HashMultimap<Action, ResourceDynamic> getEscalatedStateTransitions() {
        List<Action> escalateActions = sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.property("action")) //
                .createAlias("action", "action", JoinType.INNER_JOIN) //
                .createAlias("action.scope", "scope", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("action.actionType", PrismActionType.SYSTEM_ESCALATION)) //
                .addOrder(Order.desc("scope.precedence")) //
                .list();

        HashMultimap<Action, ResourceDynamic> escalations = HashMultimap.create();
        for (Action escalateAction : escalateActions) {
            List<ResourceDynamic> escalateResources;
            
            try {
                escalateResources = sessionFactory.getCurrentSession() //
                        .createCriteria(escalateAction.getScope().getId().getClass()) //
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
    
    public PrismStateTransitionEvaluation getStateTransitionEvaluationByStateAction(StateAction stateAction) {
        return (PrismStateTransitionEvaluation) sessionFactory.getCurrentSession().createCriteria(StateTransition.class) //
                .setProjection(Projections.property("stateTransitionEvaluation.id")) //
                .createAlias("stateTransitionEvaluation", "stateTransitionEvaluation", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("stateAction", stateAction)) //
                .add(Restrictions.isNotNull("stateTransitionEvaluation")) //
                .setMaxResults(1) //
                .uniqueResult();
    }
    
    public void disableStateActions() {
        Query query = sessionFactory.getCurrentSession().createQuery( //
                "update StateAction " //
                + "set enabled = :enabled");
        query.setParameter("enabled", false);
        query.executeUpdate();
    }
    
    public void disableStateActionAssignments() {
        Query query = sessionFactory.getCurrentSession().createQuery( //
                "update StateActionAssignment"
                + "set enabled = :enabled");
        query.setParameter("enabled", false);
        query.executeUpdate();
    }
    
    public void disableStateActionEnhancements() {
        Query query = sessionFactory.getCurrentSession().createQuery( //
                "update StateActionEnhancement"
                + "set enabled = :enabled");
        query.setParameter("enabled", false);
        query.executeUpdate();
    }

}
