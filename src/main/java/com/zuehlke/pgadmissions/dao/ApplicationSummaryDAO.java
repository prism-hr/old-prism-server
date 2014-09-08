package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationProcessing;
import com.zuehlke.pgadmissions.domain.ApplicationProcessingSummary;
import com.zuehlke.pgadmissions.domain.ParentResource;
import com.zuehlke.pgadmissions.domain.StateGroup;

@Repository
public class ApplicationSummaryDAO {
    
    @Autowired
    private SessionFactory sessionFactory;

    public ApplicationProcessing getProcessing(Application application, StateGroup stateGroup) {
        return (ApplicationProcessing) sessionFactory.getCurrentSession().createCriteria(ApplicationProcessing.class) //
                .add(Restrictions.eq("application", application)) //
                .add(Restrictions.eq("stateGroup", stateGroup)) //
                .uniqueResult();
    }
    
    public ApplicationProcessingSummary getProcessingSummary(ParentResource summaryResource, StateGroup stateGroup) {
        return (ApplicationProcessingSummary) sessionFactory.getCurrentSession().createCriteria(ApplicationProcessingSummary.class) //
                .add(Restrictions.eq(summaryResource.getResourceScope().getLowerCaseName(), summaryResource)) //
                .add(Restrictions.eq("stateGroup", stateGroup)) //
                .uniqueResult();
    }
    
}
