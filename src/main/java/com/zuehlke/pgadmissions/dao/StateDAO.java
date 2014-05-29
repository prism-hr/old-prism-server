package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.PrismResource;
import com.zuehlke.pgadmissions.domain.State;
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
                .add(Restrictions.eq("stateAction.state.id", resource.getState().getId())) //
                .add(Restrictions.eq("stateAction.action.id", action)) //
                .list();
    }

}
