package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.enums.PrismState;

@Repository
@SuppressWarnings("unchecked")
public class StateDAO {

    @Autowired
	private SessionFactory sessionFactory;
	
	public void save(State state) {
        sessionFactory.getCurrentSession().saveOrUpdate(state);
	}
	
	public State getById(PrismState id) {
		return (State)sessionFactory.getCurrentSession().createCriteria(State.class)
				.add(Restrictions.eq("id", id)).uniqueResult();
	}

	public List<State> getAllConfigurableStates() {
        return (List<State>) sessionFactory.getCurrentSession().createCriteria(State.class)
                .add(Restrictions.isNotNull("duration")).list();
	}
	
	public List<PrismState> getAllStatesThatApplicationsCanBeAssignedTo() {
        return (List<PrismState>) sessionFactory.getCurrentSession().createCriteria(State.class)
                .setProjection(Projections.property("id"))
                .add(Restrictions.eq("canBeAssignedTo", true)).list();
    }
	
	public List<PrismState> getAllStatesThatApplicationsCanBeAssignedFrom() {
        return (List<PrismState>) sessionFactory.getCurrentSession().createCriteria(State.class)
                .setProjection(Projections.property("id"))
                .add(Restrictions.eq("canBeAssignedFrom", true)).list();
	}
	
}
