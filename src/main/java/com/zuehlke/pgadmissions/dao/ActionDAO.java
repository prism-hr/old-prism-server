package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;

@Repository
public class ActionDAO {

	private final SessionFactory sessionFactory;
	
	public ActionDAO(){
		this(null);
	}
	
	@Autowired
	public ActionDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;

	}
	
    public Action getActionById(ApplicationFormAction actionId) {
        return (Action) sessionFactory.getCurrentSession().createCriteria(Action.class)
                .add(Restrictions.eq("id", actionId)).uniqueResult();
    }
	
}