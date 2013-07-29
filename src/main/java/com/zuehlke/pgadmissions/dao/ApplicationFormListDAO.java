package com.zuehlke.pgadmissions.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationsFiltering;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

@Repository
public class ApplicationFormListDAO {

    public static final DateTimeFormatter USER_DATE_FORMAT = DateTimeFormat.forPattern("dd MMM yyyy");

    private final SessionFactory sessionFactory;
    
    private final ActionsProvider actionsProvider;

    public ApplicationFormListDAO() {
        this(null, null);
    }

    @Autowired
    public ApplicationFormListDAO(SessionFactory sessionFactory, ActionsProvider actionsProvider) {
        this.sessionFactory = sessionFactory;
        this.actionsProvider = actionsProvider;
    }

    public List<ApplicationForm> getVisibleApplications(RegisteredUser user) {
        return this.getVisibleApplications(user, new ApplicationsFiltering(), 50);
    }
    
    @SuppressWarnings("unchecked")
    public List<ApplicationForm> getApplicationsWorthConsideringForAttentionFlag(final RegisteredUser user, 
            final ApplicationsFiltering filtering, final int itemsPerPge) {
        HashSet<ApplicationForm> applicationsWhichNeedAttention = new LinkedHashSet<ApplicationForm>();
        
        Criteria criteria = new ApplicationFormListCriteriaBuilder(sessionFactory)
            .attentionFlag()
            .forUser(user)
            .filter(filtering)
            .useDisjunction(filtering.getUseDisjunction())
            .build();

        ArrayList<Object> applicationIds = new ArrayList<Object>(new LinkedHashSet<Object>(criteria.list()));
        int idx = 0;
        for (idx = filtering.getLatestConsideredFlagIndex(); idx < applicationIds.size(); idx++) {
            ApplicationForm form = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, (Integer) applicationIds.get(idx));
            if (actionsProvider.calculateActions(user, form).isRequiresAttention()) {
                applicationsWhichNeedAttention.add(form);
            }
            if (applicationsWhichNeedAttention.size() == itemsPerPge) {
                break;
            }
        }
        filtering.setLatestConsideredFlagIndex(idx + 1);
        return new ArrayList<ApplicationForm>(applicationsWhichNeedAttention);
    }

    @SuppressWarnings("unchecked")
    public List<ApplicationForm> getVisibleApplications(final RegisteredUser user, final ApplicationsFiltering filtering, final int itemsPerPage) {
        Criteria criteria = new ApplicationFormListCriteriaBuilder(sessionFactory)
            .allVisibleApplications()
            .forUser(user)
            .filter(filtering)
            .maxResults(itemsPerPage)
            .useDisjunction(filtering.getUseDisjunction())
            .firstResult((filtering.getBlockCount() - 1) * itemsPerPage)
            .build();

        ArrayList<ApplicationForm> results = new ArrayList<ApplicationForm>();
        LinkedHashSet<Object> applicationIds = new LinkedHashSet<Object>(criteria.list());
        for (Object id : applicationIds) {
            results.add((ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, (Integer) id));
        }
        return results;
    }
}
