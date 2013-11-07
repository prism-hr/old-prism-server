package com.zuehlke.pgadmissions.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationsFilter;
import com.zuehlke.pgadmissions.domain.ApplicationsFiltering;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.SearchCategory;
import com.zuehlke.pgadmissions.domain.enums.SearchCategory.CategoryType;
import com.zuehlke.pgadmissions.domain.enums.SearchPredicate;
import com.zuehlke.pgadmissions.domain.enums.SortCategory;
import com.zuehlke.pgadmissions.domain.enums.SortOrder;

public class ApplicationFormListCriteriaBuilder {

    private static final DateTimeFormatter USER_DATE_FORMAT = DateTimeFormat.forPattern("dd MMM yyyy");

    private Criteria criteria;

    private final SessionFactory sessionFactory;

    private RegisteredUser user = null;

    private int firstResult = -1;

    private int maxResults = -1;

    private ApplicationsFiltering applicationsFilter = null;

    private Boolean useDisjunction;

    public ApplicationFormListCriteriaBuilder(final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.criteria = this.sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class);
    }

    public ApplicationFormListCriteriaBuilder filter(final ApplicationsFiltering applicationsFilter) {
        this.applicationsFilter = applicationsFilter;
        return this;
    }

    public ApplicationFormListCriteriaBuilder maxResults(final int i) {
        this.maxResults = i;
        return this;
    }

    public ApplicationFormListCriteriaBuilder firstResult(final int i) {
        firstResult = i;
        return this;
    }

    public ApplicationFormListCriteriaBuilder forUser(final RegisteredUser user) {
        this.user = user;
        return this;
    }

    public ApplicationFormListCriteriaBuilder useDisjunction(final Boolean useDisjunction) {
        this.useDisjunction = useDisjunction;
        return this;
    }

    public Criteria build() {
        criteria.setReadOnly(true);
        criteria.setProjection(Projections.id());

        if (firstResult > -1) {
            criteria.setFirstResult(firstResult);
        }

        if (maxResults > -1) {
            criteria.setMaxResults(maxResults);
        }

        criteria = setAliases(criteria);

        if (applicationsFilter != null) {
            List<Criterion> criterions = new ArrayList<Criterion>();
            for (ApplicationsFilter filter : applicationsFilter.getFilters()) {
                Criterion criterion = getSearchCriterion(filter.getSearchCategory(), filter.getSearchPredicate(), filter.getSearchTerm(), criteria);
                if (criterion != null) {
                    criterions.add(criterion);
                }
            }

            if (BooleanUtils.isTrue(useDisjunction)) {
                Disjunction disjunction = Restrictions.disjunction();
                for (Criterion criterion : criterions) {
                    disjunction.add(criterion);
                }
                criteria.add(disjunction);
            } else {
                for (Criterion criterion : criterions) {
                    criteria.add(criterion);
                }
            }
            criteria = setOrderCriteria(applicationsFilter.getSortCategory(), applicationsFilter.getOrder());
        }

        return criteria;
    }

    private Criteria setAliases(final Criteria criteria) {
        criteria.createAlias("applicant", "a");
        criteria.createAlias("program", "p");
        criteria.createAlias("applicationFormUserRoles", "r");
        return criteria;
    }

    private Criterion getSearchCriterion(final SearchCategory searchCategory, final SearchPredicate searchPredicate, final String term, final Criteria criteria) {
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
                        if (status == ApplicationFormStatus.APPROVAL) {
                            newCriterion = Restrictions.disjunction().add(Restrictions.eq("status", ApplicationFormStatus.APPROVAL));
                        } else {
                            newCriterion = Restrictions.eq("status", status);
                        }
                    }
                    break;
                case PROJECT_TITLE:
                    newCriterion = Restrictions.ilike("projectTitle", term, MatchMode.ANYWHERE);
                    break;
                case SUPERVISOR:
                    criteria.createAlias("programmeDetails", "pddetails", JoinType.LEFT_OUTER_JOIN);
                    criteria.createAlias("approvalRounds", "approvRounds", JoinType.LEFT_OUTER_JOIN);
                    criteria.createAlias("pddetails.suggestedSupervisors", "programme_details_supervisor", JoinType.LEFT_OUTER_JOIN);
                    criteria.createAlias("approvRounds.supervisors", "approvrounds_supervisor", JoinType.LEFT_OUTER_JOIN);
                    criteria.createAlias("approvrounds_supervisor.user", "approvrounds_supervisor_user", JoinType.LEFT_OUTER_JOIN);

                    newCriterion = Restrictions
                            .disjunction()
                            .add(ConcatenableIlikeCriterion.ilike(term, MatchMode.ANYWHERE, "approvrounds_supervisor_user.firstName",
                                    "approvrounds_supervisor_user.lastName"))
                            .add(ConcatenableIlikeCriterion.ilike(term, MatchMode.ANYWHERE, "programme_details_supervisor.firstname",
                                    "programme_details_supervisor.lastname"));
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
                } else if (searchCategory == SearchCategory.CLOSING_DATE) {
                    newCriterion = createCriteriaForDate(searchPredicate, term, criteria, "batchDeadline");
                }
            }
            return newCriterion;
        }
        return null;
    }

    private Criteria setOrderCriteria(final SortCategory sortCategory, final SortOrder order) {
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

        case RATING:
            criteria.addOrder(getOrderCriteria("averageRating", ascending));

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
            conjunction.add(Restrictions.lt(field, new DateTime(submissionDate).plusDays(1).toDate()));
            newCriterion = conjunction;
            break;
        case TO_DATE:
            newCriterion = Restrictions.lt(field, new DateTime(submissionDate).plusDays(1).toDate());
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

}