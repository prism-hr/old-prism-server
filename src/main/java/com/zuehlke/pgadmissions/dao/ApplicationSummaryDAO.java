package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.application.ApplicationProcessing;
import com.zuehlke.pgadmissions.domain.application.ApplicationProcessingSummary;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.workflow.StateGroup;

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
    
    public ApplicationProcessingSummary getProcessingSummary(ResourceParent summaryResource, StateGroup stateGroup) {
        return (ApplicationProcessingSummary) sessionFactory.getCurrentSession().createCriteria(ApplicationProcessingSummary.class) //
                .add(Restrictions.eq(summaryResource.getResourceScope().getLowerCaseName(), summaryResource)) //
                .add(Restrictions.eq("stateGroup", stateGroup)) //
                .uniqueResult();
    }
    
}
