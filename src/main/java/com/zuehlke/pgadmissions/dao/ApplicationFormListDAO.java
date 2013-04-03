package com.zuehlke.pgadmissions.dao;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.SearchCategory;
import com.zuehlke.pgadmissions.domain.enums.SortCategory;
import com.zuehlke.pgadmissions.domain.enums.SortOrder;

@Repository
public class ApplicationFormListDAO {

    private final SessionFactory sessionFactory;

    ApplicationFormListDAO() {
        this(null);
    }

    @Autowired
    public ApplicationFormListDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    public List<ApplicationForm> getVisibleApplications(RegisteredUser user) {
        return this.getVisibleApplications(user, null, null, SortCategory.APPLICATION_DATE, SortOrder.DESCENDING, 1, 50);
    }
    
    @SuppressWarnings("unchecked")
    public List<ApplicationForm> getVisibleApplications(RegisteredUser user, SearchCategory searchCategory,
            String term, SortCategory sortCategory, SortOrder sortOrder, int pageCount, int itemsPerPage) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class);
        criteria.setFirstResult((pageCount - 1) * itemsPerPage);
        criteria.setMaxResults(itemsPerPage);
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        if (user.isInRole(Authority.SUPERADMINISTRATOR)) {
            criteria.add(getAllApplicationsForSuperAdministrator());
        } else {
            Disjunction disjunction = Restrictions.disjunction();
            
            if (user.isInRole(Authority.APPLICANT)) {
                disjunction.add(Subqueries.propertyIn("id", applicationsOfWhichApplicant(user)));
                disjunction.add(Subqueries.propertyIn("id", applicationsOfWhichReferee(user)));
            }
            
            if (user.isInRole(Authority.REFEREE)) {
                disjunction.add(Subqueries.propertyIn("id", applicationsOfWhichReferee(user)));
            }
            
            if (!user.getProgramsOfWhichAdministrator().isEmpty()) {
                disjunction.add(Subqueries.propertyIn("id", getSubmittedApplicationsInProgramsOfWhichAdmin(user)));
            }

            if (!user.getProgramsOfWhichApprover().isEmpty()) {
                disjunction.add(Subqueries.propertyIn("id", getApprovedApplicationsInProgramsOfWhichApprover(user)));
            }
            
            if (!user.getProgramsOfWhichViewer().isEmpty()) {
                disjunction.add(Subqueries.propertyIn("id", getApplicationsInProgramsOfWhichViewer(user)));
            }
            
            disjunction.add(Subqueries.propertyIn("id", getSubmittedApplicationsOfWhichApplicationAdministrator(user)));

            disjunction.add(Subqueries.propertyIn("id", getApplicationsCurrentlyInReviewOfWhichReviewerOfLatestRound(user)));
            
            disjunction.add(Subqueries.propertyIn("id", getApplicationsCurrentlyInInterviewOfWhichInterviewerOfLatestInterview(user)));
            
            disjunction.add(Subqueries.propertyIn("id", getApplicationsCurrentlyInApprovalOrApprovedOfWhichSupervisorOfLatestApprovalRound(user)));                

            criteria.add(disjunction);
        }
        
        criteria = setSearchCriteria(searchCategory, term, criteria);
        
        if (criteria == null) {
            return Collections.emptyList();
        }
        
        criteria = setOrderCriteria(sortCategory, sortOrder, searchCategory, criteria);
        
        return criteria.list();
    }

    private Criteria setSearchCriteria(final SearchCategory searchCategory, final String term, final Criteria criteria) {
        if (searchCategory != null && StringUtils.isNotBlank(term)) {
            switch (searchCategory) {
            case APPLICANT_NAME:
                criteria.createAlias("applicant", "a");
                criteria.add(Restrictions.disjunction()
                        .add(Restrictions.ilike("a.firstName", term, MatchMode.ANYWHERE))
                        .add(Restrictions.ilike("a.lastName", term, MatchMode.ANYWHERE)));
                break;
            case APPLICATION_NUMBER:
                criteria.add(Restrictions.ilike("applicationNumber", term, MatchMode.ANYWHERE));
                break;
            case PROGRAMME_NAME:
                criteria.createAlias("program", "p");
                criteria.add(Restrictions.disjunction()
                        .add(Restrictions.ilike("p.title", term, MatchMode.ANYWHERE))
                        .add(Restrictions.ilike("p.code", term, MatchMode.ANYWHERE)));
                break;
            case APPLICATION_STATUS:
                ApplicationFormStatus status = ApplicationFormStatus.convert(term);
                if (status != null) {
                    criteria.add(Restrictions.eq("status", status));
                } else {
                    return null;
                }
                break;
            case APPLICATION_DATE:
                break;
            default:
            }
        }
        return criteria;
    }
    
    public Criteria setOrderCriteria(SortCategory sortCategory, SortOrder order, SearchCategory searchCategory, Criteria criteria) {
        boolean ascending = true;
        if (order == SortOrder.DESCENDING) {
            ascending = false;
        }
        
        switch (sortCategory) {

        case APPLICANT_NAME:
            if (searchCategory != SearchCategory.APPLICANT_NAME) {
                criteria.createAlias("applicant", "a");
            }
            criteria.addOrder(getOrderCriteria("a.lastName", ascending));
            criteria.addOrder(getOrderCriteria("a.firstName", ascending));
            break;
            
        case PROGRAMME_NAME:
            if (searchCategory != SearchCategory.PROGRAMME_NAME) {
                criteria.createAlias("program", "p");
            }
            criteria.addOrder(getOrderCriteria("p.title", ascending));
            break;

        case APPLICATION_STATUS:
            criteria.addOrder(getOrderCriteria("status", ascending));
            break;

        default:
        case APPLICATION_DATE:
            criteria.addOrder(getOrderCriteria("submittedDate", ascending));
            criteria.addOrder(getOrderCriteria("applicationTimestamp", ascending));
            break;
        }
        return criteria;
    }
    
    private Order getOrderCriteria(String propertyName, boolean ascending) {
        if (ascending) {
            return Order.asc(propertyName);
        } else {
            return Order.desc(propertyName);
        }
    }

    private Criterion getAllApplicationsForSuperAdministrator() {
        return Restrictions.not(Restrictions.eq("status", ApplicationFormStatus.UNSUBMITTED));
    }
    
    private DetachedCriteria applicationsOfWhichReferee(RegisteredUser user) {
        return DetachedCriteria.forClass(ApplicationForm.class)
                .setProjection(Projections.property("id"))
                .createAlias("referees", "referee")
                .add(Restrictions.eq("referee.user", user));
    }

    private DetachedCriteria applicationsOfWhichApplicant(RegisteredUser user) {
        return DetachedCriteria.forClass(ApplicationForm.class)
                .setProjection(Projections.property("id"))
                .add(Restrictions.eq("applicant", user));    
    }

    private DetachedCriteria getSubmittedApplicationsOfWhichApplicationAdministrator(RegisteredUser user) {
        return DetachedCriteria.forClass(ApplicationForm.class)
                .setProjection(Projections.property("id"))
                .add(Restrictions.not(Restrictions.eq("status", ApplicationFormStatus.UNSUBMITTED)))
                .add(Restrictions.eq("applicationAdministrator", user));
    }

    private DetachedCriteria getSubmittedApplicationsInProgramsOfWhichAdmin(RegisteredUser user) {
        return DetachedCriteria.forClass(ApplicationForm.class)
                .setProjection(Projections.property("id"))
                .add(Restrictions.not(Restrictions.eq("status", ApplicationFormStatus.UNSUBMITTED)))
                .add(Restrictions.in("program", user.getProgramsOfWhichAdministrator()));
    }

    private DetachedCriteria getApprovedApplicationsInProgramsOfWhichApprover(RegisteredUser user) {
        return DetachedCriteria.forClass(ApplicationForm.class)
                .setProjection(Projections.property("id"))
                .add(Restrictions.eq("status", ApplicationFormStatus.APPROVAL))
                .add(Restrictions.in("program", user.getProgramsOfWhichApprover()));
    }

    private DetachedCriteria getApplicationsCurrentlyInReviewOfWhichReviewerOfLatestRound(RegisteredUser user) {
        return DetachedCriteria.forClass(ApplicationForm.class)
                .setProjection(Projections.property("id"))
                .add(Restrictions.eq("status", ApplicationFormStatus.REVIEW))
                .createAlias("latestReviewRound", "latestReviewRound")
                .createAlias("latestReviewRound.reviewers", "reviewer")
                .add(Restrictions.eq("reviewer.user", user));
    }

    private DetachedCriteria getApplicationsCurrentlyInInterviewOfWhichInterviewerOfLatestInterview(RegisteredUser user) {
        return DetachedCriteria.forClass(ApplicationForm.class)
                .setProjection(Projections.property("id"))
                .add(Restrictions.eq("status", ApplicationFormStatus.INTERVIEW))
                .createAlias("latestInterview", "latestInterview")
                .createAlias("latestInterview.interviewers", "interviewer")
                .add(Restrictions.eq("interviewer.user", user));
    }

    private DetachedCriteria getApplicationsCurrentlyInApprovalOrApprovedOfWhichSupervisorOfLatestApprovalRound(RegisteredUser user) {
        return DetachedCriteria.forClass(ApplicationForm.class)
                .setProjection(Projections.property("id"))
                .add(Restrictions.in("status", Arrays.asList(ApplicationFormStatus.APPROVAL, ApplicationFormStatus.APPROVED)))
                .createAlias("latestApprovalRound", "latestApprovalRound")
                .createAlias("latestApprovalRound.supervisors", "supervisor")
                .add(Restrictions.eq("supervisor.user", user));
    }
    
    private DetachedCriteria getApplicationsInProgramsOfWhichViewer(RegisteredUser user) {
        return DetachedCriteria.forClass(ApplicationForm.class)
                .setProjection(Projections.property("id"))
                .add(Restrictions.not(Restrictions.eq("status", ApplicationFormStatus.UNSUBMITTED)))
                .add(Restrictions.in("program", user.getProgramsOfWhichViewer()));
    }
}
