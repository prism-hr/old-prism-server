package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.NotificationMethod;

@Repository
@SuppressWarnings("unchecked")
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
    
    public List<Action> getActionsWithSyndicateNotification() {
        return (List<Action>) sessionFactory.getCurrentSession().createCriteria(Action.class)
                .add(Restrictions.eq("notificaiton", NotificationMethod.SYNDICATED)).list();
    }
    
    public List<Action> getActionsWithIndividualNotification() {
        return (List<Action>) sessionFactory.getCurrentSession().createCriteria(Action.class)
                .add(Restrictions.eq("notificaiton", NotificationMethod.INDIVIDUAL)).list();
    }
    
    public void save(Action action) {
        if (getActionById(action.getId()) == null) {
            sessionFactory.getCurrentSession().save(action);
        }
    }
	
}