package com.zuehlke.pgadmissions.dao;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StopWatch;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationsFiltering;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

@Repository
public class ApplicationFormListDAO {

    public static final DateTimeFormatter USER_DATE_FORMAT = DateTimeFormat.forPattern("dd MMM yyyy");

    private final SessionFactory sessionFactory;
    
    public ApplicationFormListDAO() {
        this(null);
    }

    @Autowired
    public ApplicationFormListDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<ApplicationForm> getVisibleApplications(RegisteredUser user) {
        return this.getVisibleApplications(user, new ApplicationsFiltering(), 50);
    }
    
    @SuppressWarnings("unchecked")
    public List<ApplicationForm> getVisibleApplications(final RegisteredUser user, final ApplicationsFiltering filtering, final int itemsPerPage) {
        Criteria criteria = new ApplicationFormListCriteriaBuilder(sessionFactory)
            .forUser(user)
            .filter(filtering)
            .maxResults(itemsPerPage)
            .useDisjunction(filtering.getUseDisjunction())
            .firstResult((filtering.getBlockCount() - 1) * itemsPerPage)
            .build();

        ArrayList<ApplicationForm> results = new ArrayList<ApplicationForm>();
        
        StopWatch watch = new StopWatch();
        watch.start();
        LinkedHashSet<Object> applicationIds = new LinkedHashSet<Object>(criteria.list());
        watch.stop();
        System.out.println("get ids : " + watch.getLastTaskTimeMillis());
        
        watch = new StopWatch();
        watch.start();
        for (Object id : applicationIds) {
            results.add((ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, (Integer) id));
        }
        watch.stop();
        System.out.println("get apps: " + watch.getLastTaskTimeMillis());
        return results;
    }
}
