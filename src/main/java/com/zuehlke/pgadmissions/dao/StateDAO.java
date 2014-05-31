package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.PrismResource;
import com.zuehlke.pgadmissions.domain.PrismResourceTransient;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.StateDuration;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.enums.PrismAction;
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

    public List<StateTransition> getStateTransitions(PrismResource resource, PrismAction action) {
        return (List<StateTransition>) sessionFactory.getCurrentSession().createCriteria(StateTransition.class) //
                .createAlias("stateAction", "stateAction", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("stateAction.state", resource.getState())) //
                .add(Restrictions.eq("stateAction.action.id", action)) //
                .list();
    }
    
    public StateTransition getStateTransition(List<StateTransition> permittedTransitions, State candidateTransitionState) {
        return (StateTransition) sessionFactory.getCurrentSession().createCriteria(StateTransition.class) //
                .add(Restrictions.in("id", permittedTransitions)) //
                .add(Restrictions.eq("transitionState", candidateTransitionState)) //
                .uniqueResult();
    }
    
    public Integer getStateDuration(PrismResourceTransient resource) {
        return (Integer) sessionFactory.getCurrentSession().createCriteria(StateDuration.class)
                .setProjection(Projections.property("expiryDuration"))
                .add(Restrictions.eq("state", resource.getState()))
                .add(Restrictions.disjunction()
                        .add(Restrictions.conjunction()
                                .add(Restrictions.eq("system", resource.getSystem()))
                                .add(Restrictions.isNull("institution"))
                                .add(Restrictions.isNull("program")))
                        .add(Restrictions.conjunction()
                                .add(Restrictions.eq("institution", resource.getInstitution()))
                                .add(Restrictions.isNull("program")))
                        .add(Restrictions.eq("program", resource.getProgram())))
                .uniqueResult();
    }

    public void executePropagatedStateTransitions(PrismResource resource, StateTransition stateTransition) {
        // TODO: Write the HQL update statements
    }

    public void executeEscalatedStateTransitions() {
        // TODO: Write the HQL update statements
    }

}
