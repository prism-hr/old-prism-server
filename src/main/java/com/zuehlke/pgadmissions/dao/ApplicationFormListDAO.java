package com.zuehlke.pgadmissions.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationsFilter;
import com.zuehlke.pgadmissions.domain.ApplicationsFiltering;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.ApplicationsPreFilter;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.SearchCategory;
import com.zuehlke.pgadmissions.domain.enums.SearchCategory.CategoryType;
import com.zuehlke.pgadmissions.domain.enums.SearchPredicate;
import com.zuehlke.pgadmissions.domain.enums.SortCategory;
import com.zuehlke.pgadmissions.domain.enums.SortOrder;
import com.zuehlke.pgadmissions.services.ApplicationsService;

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
    
    public List<ApplicationForm> getApplicationsWorthConsideringForAttentionFlag(final RegisteredUser user, final ApplicationsFiltering filtering, final ApplicationsService service) {
        HashSet<ApplicationForm> applicationsWhichNeedAttention = new LinkedHashSet<ApplicationForm>();
//        StringBuilder queryBuilder = new StringBuilder(""
//                + "SELECT apform.id "  
//                + "FROM application_form apform " 
//                + "JOIN PROGRAM prog ON apform.program_id = prog.id " 
//                + "JOIN REGISTERED_USER ru ON apform.applicant_id = ru.id " 
//                + "WHERE apform.status NOT IN (\"UNSUBMITTED\", \"REJECTED\", \"WITHDRAWN\", \"APPROVED\") " 
//                + "AND apform.program_id IN " 
//                + "( "
//                + "    SELECT program_id "  
//                + "    FROM REGISTERED_USER ru "  
//                + "    JOIN PROGRAM_ADMINISTRATOR_LINK pal ON ru.id = pal.administrator_id "  
//                + "    WHERE ru.id = :user_id "
//                + "    /* Programme  administrators */ "
//                + ""                      
//                + "    UNION "
//                + ""                
//                + "    SELECT program_id "  
//                + "    FROM REGISTERED_USER ru "  
//                + "    JOIN PROGRAM_APPROVER_LINK papl ON ru.id = papl.registered_user_id "  
//                + "    WHERE ru.id = :user_id "
//                + "    /* Programme approvers */ "
//                + ""
//                + ") OR apform.id IN ( "
//                + ""
//                + "     SELECT appform.id " 
//                + "     FROM APPLICATION_FORM appform " 
//                + "     JOIN INTERVIEW iv ON appform.latest_interview_id = iv.id "
//                + "     JOIN INTERVIEWER ivr ON ivr.interview_id = iv.id "
//                + "     WHERE ivr.registered_user_id = :user_id "
//                + "     /* Current interviewers */ "
//                + ""
//                + ") OR apform.id IN ( "
//                + ""
//                + "     SELECT appform.id "
//                + "     FROM APPLICATION_FORM appform " 
//                + "     JOIN REVIEW_ROUND iv ON appform.latest_review_round_id = iv.id "
//                + "     JOIN REVIEWER ivr ON ivr.review_round_id = iv.id "
//                + "     WHERE ivr.registered_user_id = :user_id "
//                + "     /* Current reviewers */ "
//                + ""
//                + ") OR apform.id IN ( "
//                + ""
//                + "     SELECT appform.id " 
//                + "     FROM APPLICATION_FORM appform " 
//                + "     JOIN APPROVAL_ROUND iv ON appform.latest_approval_round_id = iv.id "
//                + "     JOIN SUPERVISOR ivr ON ivr.approval_round_id = iv.id "
//                + "     WHERE ivr.registered_user_id = :user_id "
//                + "     /* Current supervisors */ "
//                + ""
//                + ") "
//                + ""
//                + "OR apform.app_administrator_id = :user_id "
//                + "/* Delegated interview administrator */ "
//                + ""
//                + "OR apform.applicant_id = :user_id "
//                + "/* Applicant */ "
//                + ""
//                + "OR apform.id IN ( "
//                + ""
//                + "     SELECT appform.id "
//                + "     FROM APPLICATION_FORM appform "     
//                + "     JOIN APPLICATION_FORM_REFEREE ref ON ref.application_form_id = appform.id "
//                + "     WHERE ref.registered_user_id = :user_id "
//                + "     /* Application referees */ "
//                + ") "
//                + ""
//                + "OR apform.id IN ( "
//                + ""
//                + "     SELECT appform.id " 
//                + "     FROM APPLICATION_FORM appform "
//                + "     WHERE registry_users_notified = true " 
//                + "     AND 1 = ( "
//                + "         SELECT DISTINCT 1 " 
//                + "         FROM REGISTERED_USER user "
//                + "         JOIN USER_ROLE_LINK urole ON urole.registered_user_id = user.id "
//                + "         JOIN APPLICATION_ROLE arole ON urole.application_role_id = arole.id "
//                + "         WHERE arole.authority = \"ADMITTER\" "
//                + "         AND user.id = :user_id "
//                + ") "
//                + "/* Applications which need registry attention if current user is an ADMITTER */ "
//                + ") ORDER BY ");
        
        StringBuilder queryBuilder = new StringBuilder(""
                + "SELECT apform.id " 
                + "FROM APPLICATION_FORM apform "
                + "JOIN PROGRAM prog ON apform.program_id = prog.id "
                + "JOIN REGISTERED_USER ru ON apform.applicant_id = ru.id "
                + "WHERE apform.status NOT IN ('UNSUBMITTED', 'REJECTED', 'WITHDRAWN', 'APPROVED')" 
                + "AND apform.program_id IN "
                + "( "
                + "     SELECT program_id " 
                + "     FROM REGISTERED_USER ru " 
                + "     JOIN PROGRAM_ADMINISTRATOR_LINK pal ON ru.id = pal.administrator_id " 
                + "     WHERE ru.id = :user_id "
                + ""
                + "     UNION "
                + ""
                + "     SELECT program_id " 
                + "     FROM REGISTERED_USER ru " 
                + "     JOIN PROGRAM_APPROVER_LINK papl ON ru.id = papl.registered_user_id " 
                + "     WHERE ru.id = :user_id "
                + ""
                + "     UNION " 
                + ""
                + "     SELECT program_id "
                + "     FROM REGISTERED_USER ru " 
                + "     JOIN PROGRAM_INTERVIEWER_LINK pil ON ru.id = pil.interviewer_id " 
                + "     WHERE ru.id = :user_id "
                + ""
                + "     UNION " 
                + ""
                + "     SELECT program_id "
                + "     FROM REGISTERED_USER ru " 
                + "     JOIN PROGRAM_REVIEWER_LINK prl ON ru.id = prl.reviewer_id " 
                + "     WHERE ru.id = :user_id "
                + ""
                + "     UNION "
                + ""
                + "     SELECT program_id " 
                + "     FROM REGISTERED_USER ru " 
                + "     JOIN PROGRAM_SUPERVISOR_LINK psl ON ru.id = psl.supervisor_id " 
                + "     WHERE ru.id = :user_id "
                + ""
                + ") ORDER BY ");

        switch (filtering.getSortCategory()) {
            case APPLICANT_NAME:
                queryBuilder.append("ru.lastName");
                getSqlOrder(filtering, queryBuilder);
                queryBuilder.append(", ru.firstName");
                getSqlOrder(filtering, queryBuilder);
                break;

            case PROGRAMME_NAME:
                queryBuilder.append("prog.title");
                getSqlOrder(filtering, queryBuilder);
                break;

            case APPLICATION_STATUS:
                queryBuilder.append("apform.status");
                getSqlOrder(filtering, queryBuilder);
                break;

            default:
            case APPLICATION_DATE:
                queryBuilder.append("apform.submitted_on_timestamp");
                getSqlOrder(filtering, queryBuilder);
                queryBuilder.append(", apform.app_date_time");
                break;
        }

        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(queryBuilder.toString());
        query.setInteger("user_id", user.getId());

        for (Object id : query.list()) {
            ApplicationForm form = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, (Integer) id);
            if (service.calculateActions(user, form).isRequiresAttention()) {
                applicationsWhichNeedAttention.add(form);
            }
        }
        return new ArrayList<ApplicationForm>(applicationsWhichNeedAttention);
    }
    

    private StringBuilder getSqlOrder(final ApplicationsFiltering filtering, final StringBuilder builder) {
        return filtering.getOrder() == SortOrder.DESCENDING ? builder.append(" DESC ") : builder.append(" ASC ");
    }

    public List<ApplicationForm> getVisibleApplications(final RegisteredUser user, final ApplicationsFiltering filtering, final int itemsPerPage) {
        Criteria criteria = buildCriteriaForVisibleApplications(user, filtering);

        if (criteria == null) {
            return Collections.emptyList();
        }

        criteria.setFirstResult((filtering.getBlockCount() - 1) * itemsPerPage);
        criteria.setMaxResults(itemsPerPage);

        ArrayList<ApplicationForm> results = new ArrayList<ApplicationForm>();
        for (Object id : criteria.list()) {
            results.add((ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, (Integer) id));
        }

        return results;
    }
    
    private Criteria buildCriteriaForVisibleApplications(final RegisteredUser user, final ApplicationsFiltering filtering) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class);

        criteria.setReadOnly(true);
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        criteria.setProjection(Projections.id());
        
        if (user.isInRole(Authority.SUPERADMINISTRATOR) || user.isInRole(Authority.ADMITTER)) {
            criteria.add(getAllApplicationsForSuperAdministrator());
            criteria.add(getAllApplicationsWhichHaveBeenWithdrawnAfterInitialSubmit());
        } else {
            Disjunction disjunction = Restrictions.disjunction();

            if (user.isInRole(Authority.APPLICANT)) {
                disjunction.add(Subqueries.propertyIn("id", applicationsOfWhichApplicant(user)));
                disjunction.add(Subqueries.propertyIn("id", applicationsOfWhichReferee(user)));
            } else {
                criteria.add(getAllApplicationsWhichHaveBeenWithdrawnAfterInitialSubmit());
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

            if (filtering.getPreFilter() == ApplicationsPreFilter.ALL) {
                if (!user.getProgramsOfWhichViewer().isEmpty()) {
                    disjunction.add(Subqueries.propertyIn("id", getApplicationsInProgramsOfWhichViewer(user)));
                }
            }

            disjunction.add(Subqueries.propertyIn("id", getSubmittedApplicationsOfWhichApplicationAdministrator(user)));

            disjunction.add(Subqueries.propertyIn("id", getApplicationsCurrentlyInReviewOfWhichReviewerOfLatestRound(user)));

            disjunction.add(Subqueries.propertyIn("id", getApplicationsCurrentlyInInterviewOfWhichInterviewerOfLatestInterview(user)));

            disjunction.add(Subqueries.propertyIn("id", getApplicationsCurrentlyInApprovalOrApprovedOfWhichSupervisorOfLatestApprovalRound(user)));

            criteria.add(disjunction);
        }

        criteria = setAliases(criteria);

        for (ApplicationsFilter filter : filtering.getFilters()) {
            criteria = setSearchCriteria(filter.getSearchCategory(), filter.getSearchPredicate(), filter.getSearchTerm(), criteria);
        }

        criteria = setOrderCriteria(filtering.getSortCategory(), filtering.getOrder(), criteria);

        return criteria;
    }

    private Criteria setAliases(final Criteria criteria) {
        criteria.createAlias("applicant", "a");
        criteria.createAlias("program", "p");
        return criteria;
    }

    private Criteria setSearchCriteria(final SearchCategory searchCategory, final SearchPredicate searchPredicate, final String term, final Criteria criteria) {
        if (searchCategory != null && StringUtils.isNotBlank(term)) {
            Criterion newCriterion = null;
            if (searchCategory.getType() == CategoryType.TEXT) {
                switch (searchCategory) {
                case APPLICANT_NAME:
                    newCriterion = ConcatenableIlikeCriterion.ilike(term, MatchMode.ANYWHERE, "a.firstName", "a.lastName");
                    break;
                case APPLICATION_NUMBER:
                    newCriterion = Restrictions.ilike("applicationNumber", term, MatchMode.ANYWHERE);
                    break;
                case PROGRAMME_NAME:
                    newCriterion = Restrictions.disjunction().add(Restrictions.like("p.title", StringUtils.upperCase(term), MatchMode.ANYWHERE))
                            .add(Restrictions.like("p.title", StringUtils.lowerCase(term), MatchMode.ANYWHERE))
                            .add(Restrictions.like("p.code", StringUtils.upperCase(term), MatchMode.ANYWHERE))
                            .add(Restrictions.like("p.code", StringUtils.lowerCase(term), MatchMode.ANYWHERE));
                    break;
                case APPLICATION_STATUS:
                    ApplicationFormStatus status = ApplicationFormStatus.convert(term);
                    if (status != null) {
                        newCriterion = Restrictions.eq("status", status);
                    }
                    break;
                default:
                }
                if (searchPredicate == SearchPredicate.NOT_CONTAINING) {
                    newCriterion = Restrictions.not(newCriterion);
                }
            } else if (searchCategory.getType() == CategoryType.DATE) {
                if (searchCategory == SearchCategory.SUBMISSION_DATE) {
                    newCriterion = createCriteriaForDate(searchPredicate, term, criteria, "submittedDate");
                } else if (searchCategory == SearchCategory.LAST_EDITED_DATE) {
                    newCriterion = createCriteriaForDate(searchPredicate, term, criteria, "lastUpdated");
                }
            }
            if (newCriterion != null) {
                criteria.add(newCriterion);
            }
        }
        return criteria;
    }

    public Criteria setOrderCriteria(final SortCategory sortCategory, final SortOrder order, final Criteria criteria) {
        boolean ascending = true;
        if (order == SortOrder.DESCENDING) {
            ascending = false;
        }

        switch (sortCategory) {

        case APPLICANT_NAME:
            criteria.addOrder(getOrderCriteria("a.lastName", ascending));
            criteria.addOrder(getOrderCriteria("a.firstName", ascending));
            break;

        case PROGRAMME_NAME:
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

    private Criterion createCriteriaForDate(final SearchPredicate searchPredicate, final String term, final Criteria criteria, final String field) {
        Criterion newCriterion = null;
        Date submissionDate = convertToSqlDate(term);
        if (submissionDate == null) {
            return null;
        }
        switch (searchPredicate) {
        case FROM_DATE:
            newCriterion = Restrictions.ge(field, submissionDate);
            break;
        case ON_DATE:
            Conjunction conjunction = Restrictions.conjunction();
            conjunction.add(Restrictions.ge(field, submissionDate));
            conjunction.add(Restrictions.lt(field, new Date(submissionDate.getTime() + TimeUnit.DAYS.toMillis(1))));
            newCriterion = conjunction;
            break;
        case TO_DATE:
            newCriterion = Restrictions.lt(field, new Date(submissionDate.getTime() + TimeUnit.DAYS.toMillis(1)));
            break;
        default:
            return null;
        }
        return newCriterion;
    }

    private Date convertToSqlDate(String term) {
        try {
            return USER_DATE_FORMAT.parseDateTime(term).toDate();
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private Order getOrderCriteria(String propertyName, boolean ascending) {
        if (ascending) {
            return Order.asc(propertyName);
        } else {
            return Order.desc(propertyName);
        }
    }

    private Criterion getAllApplicationsWhichHaveBeenWithdrawnAfterInitialSubmit() {
        return Restrictions.eq("withdrawnBeforeSubmit", false);
    }

    private Criterion getAllApplicationsForSuperAdministrator() {
        return Restrictions.not(Restrictions.eq("status", ApplicationFormStatus.UNSUBMITTED));
    }

    private DetachedCriteria applicationsOfWhichReferee(RegisteredUser user) {
        return DetachedCriteria.forClass(ApplicationForm.class).setProjection(Projections.property("id")).createAlias("referees", "referee")
                .add(Restrictions.eq("referee.user", user));
    }

    private DetachedCriteria applicationsOfWhichApplicant(RegisteredUser user) {
        return DetachedCriteria.forClass(ApplicationForm.class).setProjection(Projections.property("id")).add(Restrictions.eq("applicant", user));
    }

    private DetachedCriteria getSubmittedApplicationsOfWhichApplicationAdministrator(RegisteredUser user) {
        return DetachedCriteria.forClass(ApplicationForm.class).setProjection(Projections.property("id"))
                .add(Restrictions.not(Restrictions.eq("status", ApplicationFormStatus.UNSUBMITTED))).add(Restrictions.eq("applicationAdministrator", user));
    }

    private DetachedCriteria getSubmittedApplicationsInProgramsOfWhichAdmin(RegisteredUser user) {
        return DetachedCriteria.forClass(ApplicationForm.class).setProjection(Projections.property("id"))
                .add(Restrictions.not(Restrictions.eq("status", ApplicationFormStatus.UNSUBMITTED)))
                .add(Restrictions.in("program", user.getProgramsOfWhichAdministrator()));
    }

    private DetachedCriteria getApprovedApplicationsInProgramsOfWhichApprover(RegisteredUser user) {
        return DetachedCriteria.forClass(ApplicationForm.class).setProjection(Projections.property("id"))
                .add(Restrictions.eq("status", ApplicationFormStatus.APPROVAL)).add(Restrictions.in("program", user.getProgramsOfWhichApprover()));
    }

    private DetachedCriteria getApplicationsCurrentlyInReviewOfWhichReviewerOfLatestRound(RegisteredUser user) {
        return DetachedCriteria.forClass(ApplicationForm.class).setProjection(Projections.property("id"))
                .add(Restrictions.eq("status", ApplicationFormStatus.REVIEW)).createAlias("latestReviewRound", "latestReviewRound")
                .createAlias("latestReviewRound.reviewers", "reviewer").add(Restrictions.eq("reviewer.user", user));
    }

    private DetachedCriteria getApplicationsCurrentlyInInterviewOfWhichInterviewerOfLatestInterview(RegisteredUser user) {
        return DetachedCriteria.forClass(ApplicationForm.class).setProjection(Projections.property("id"))
                .add(Restrictions.eq("status", ApplicationFormStatus.INTERVIEW)).createAlias("latestInterview", "latestInterview")
                .createAlias("latestInterview.interviewers", "interviewer").add(Restrictions.eq("interviewer.user", user));
    }

    private DetachedCriteria getApplicationsCurrentlyInApprovalOrApprovedOfWhichSupervisorOfLatestApprovalRound(RegisteredUser user) {
        return DetachedCriteria.forClass(ApplicationForm.class).setProjection(Projections.property("id"))
                .add(Restrictions.in("status", Arrays.asList(ApplicationFormStatus.APPROVAL, ApplicationFormStatus.APPROVED)))
                .createAlias("latestApprovalRound", "latestApprovalRound").createAlias("latestApprovalRound.supervisors", "supervisor")
                .add(Restrictions.eq("supervisor.user", user));
    }

    private DetachedCriteria getApplicationsInProgramsOfWhichViewer(RegisteredUser user) {
        return DetachedCriteria.forClass(ApplicationForm.class).setProjection(Projections.property("id"))
                .add(Restrictions.not(Restrictions.eq("status", ApplicationFormStatus.UNSUBMITTED)))
                .add(Restrictions.in("program", user.getProgramsOfWhichViewer()));
    }
}
