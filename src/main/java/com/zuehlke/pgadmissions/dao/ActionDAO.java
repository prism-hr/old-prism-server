package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.enums.ActionType;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;

@Repository
@SuppressWarnings("unchecked")
public class ActionDAO {

    @Autowired
	private SessionFactory sessionFactory;
	
    public Action getById(ApplicationFormAction actionId) {
        return (Action) sessionFactory.getCurrentSession().createCriteria(Action.class)
                .add(Restrictions.eq("id", actionId)).uniqueResult();
    }
    
    public List<ApplicationFormAction> getActionIdByActionType(ActionType actionType) {
        return (List<ApplicationFormAction>) sessionFactory.getCurrentSession().createCriteria(Action.class)
                .setProjection(Projections.property("id"))
                .add(Restrictions.eq("actionType", actionType)).list();
    }
	
}
