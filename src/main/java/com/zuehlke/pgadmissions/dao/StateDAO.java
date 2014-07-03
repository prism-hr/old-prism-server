package com.zuehlke.pgadmissions.dao;

import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
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
import com.zuehlke.pgadmissions.domain.IUniqueResource;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.ResourceDynamic;
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
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

@Repository
@SuppressWarnings("unchecked")
public class StateDAO {

    private static final Set<Class<? extends IUniqueResource>> workflowConfigurationClasses = Sets.newLinkedHashSet();
    
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

    public StateTransition getStateTransition(List<StateTransition> permittedTransitions, State candidateTransitionState) {
        return (StateTransition) sessionFactory.getCurrentSession().createCriteria(StateTransition.class) //
                .add(Restrictions.in("id", permittedTransitions)) //
                .add(Restrictions.eq("transitionState", candidateTransitionState)) //
                .uniqueResult();
    }

    public StateDuration getCurrentStateDuration(ResourceDynamic resource) {
        return (StateDuration) sessionFactory.getCurrentSession().createCriteria(StateDuration.class) //
                .add(Restrictions.eq("state", resource.getState())) //
                .add(Restrictions.disjunction()
                        .add(Restrictions.conjunction() //
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
    
    public StateDuration getStateDuration(Resource resource, State state) {
        return (StateDuration) sessionFactory.getCurrentSession().createCriteria(StateDuration.class) //
                .add(Restrictions.eq(PrismScope.getResourceScope(resource.getClass()).getLowerCaseName(), resource))
                .add(Restrictions.eq("state", state)) //
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
                throw new Error(e);
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
                throw new Error(e);
            }
        }

        return escalations;
    }

    public void deleteStateActions() {
        for (Class<? extends IUniqueResource> workflowConfigurationClass : workflowConfigurationClasses) {
            sessionFactory.getCurrentSession().createQuery( //
                    "delete " + workflowConfigurationClass.getSimpleName()) //
                    .executeUpdate();
        }
    }
    
    public void deleteObseleteStateDurations() {
        sessionFactory.getCurrentSession().createQuery( //
                "delete StateDuration " //
                    + "where state not in (:configurableStates)") //
                .setParameterList("configurableStates", getConfigurableStates()) //
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
    
}
