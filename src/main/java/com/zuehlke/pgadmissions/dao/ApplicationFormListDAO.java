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
import org.hibernate.transform.Transformers;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Filter;
import com.zuehlke.pgadmissions.domain.FilterConstraint;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.enums.ApplicationListFilterCategory;
import com.zuehlke.pgadmissions.domain.enums.ApplicationListFilterCategory.CategoryType;
import com.zuehlke.pgadmissions.domain.enums.ApplicationListSortCategory;
import com.zuehlke.pgadmissions.domain.enums.ResourceListSearchPredicate;
import com.zuehlke.pgadmissions.domain.enums.ResourceListSortOrder;

@Repository
public class ApplicationFormListDAO {

    public static final DateTimeFormatter USER_DATE_FORMAT = DateTimeFormat.forPattern("dd MMM yyyy");
    
    @Autowired
    private SessionFactory sessionFactory;

    // TODO: generalise for different list types
    
    @SuppressWarnings("unchecked")
    public List<Application> getVisibleApplicationsForList(final User user, final Filter filter, final int itemsPerPage) {
        Integer blockCount = filter.getPage();

        Criteria criteria = sessionFactory
                .getCurrentSession()
                .createCriteria(UserRole.class)
                .setReadOnly(true)
                .setProjection(
                        Projections
                                .projectionList()
                                .add(Projections.groupProperty("application.id"), "applicationFormId")
                                // TODO join with state_action_assignment and state_action to check urgent flag
                                // .add(Projections.max("raisesUrgentFlag"), "needsToSeeUrgentFlag")
                                // .add(Projections.max("raisesUpdateFlag"), "needsToSeeUpdateFlag")
                                .add(Projections.property("applicant.id"), "applicantId")
                                .add(Projections.property("applicant.firstName"), "applicantFirstName")
                                .add(Projections.property("applicant.firstName2"), "applicantFirstName2")
                                .add(Projections.property("applicant.firstName3"), "applicantFirstName3")
                                .add(Projections.property("applicant.lastName"), "applicantLastName")
                                .add(Projections.property("applicant.email"), "applicantEmail")
                                .add(Projections.property("application.applicationNumber"), "applicationFormNumber")
                                .add(Projections.property("program.title"), "programTitle")
                                .add(Projections.property("project.title"), "projectTitle")
                                // TODO compute rating
                                // .add(Projections.property("application.averageRating"), "applicantAverageRating")
                                .add(Projections.property("application.state.id"), "applicationFormStatus")
                                // TODO compute next and last status based on comments
                                // .add(Projections.property("application.nextStatus"), "applicationFormNextStatus")
                                // .add(Projections.property("application.statusWhenWithdrawn"), "applicationFormStatusWhenWithdrawn")
                                .add(Projections.property("applicationDocument.personalStatement.id"), "applicationFormPersonalStatementId")
                                .add(Projections.property("applicationDocument.cv.id"), "applicationFormCvId")
                                .add(Projections.property("application.submittedTimestamp"), "applicationFormSubmittedTimestamp"));
                                // TODO get updateTimestamp based on comments
//                                .add(Projections.max("updateTimestamp"), "applicationFormUpdatedTimestamp"))

        appendJoinStatements(criteria);
        appendWhereStatement(criteria, user, filter);

        criteria.add(Restrictions.leProperty("assignedTimestamp", "userAccount.applicationListLastAccessTimestamp"));

        appendOrderStatement(criteria, filter);
        appendLimitStatement(criteria, (blockCount - 1) * itemsPerPage, itemsPerPage);

        return criteria.setResultTransformer(Transformers.aliasToBean(Application.class)).list();
    }

    @SuppressWarnings("unchecked")
    public List<Application> getVisibleApplicationsForReport(final User user, final Filter filtering) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(UserRole.class).setReadOnly(true)
                .setProjection(Projections.groupProperty("application"));

        appendJoinStatements(criteria);
        appendWhereStatement(criteria, user, filtering);
        appendOrderStatement(criteria, filtering);

        return criteria.list();
    }

    private void appendJoinStatements(Criteria criteria) {
        criteria.createAlias("application", "application", JoinType.INNER_JOIN).createAlias("application.user", "applicant", JoinType.INNER_JOIN)
                .createAlias("application.program", "program", JoinType.INNER_JOIN).createAlias("user", "user", JoinType.INNER_JOIN)
                .createAlias("user.account", "userAccount", JoinType.INNER_JOIN)
                .createAlias("application.project", "project", JoinType.LEFT_OUTER_JOIN).createAlias("application.applicationDocument", "applicationDocument", JoinType.LEFT_OUTER_JOIN);
    }

    private void appendWhereStatement(Criteria criteria, User user, Filter filtering) {
        if (filtering != null) {
            boolean useDisjunction = filtering.isSatisfyAllConditions();

            List<Criterion> criterions = new ArrayList<Criterion>();
            for (FilterConstraint filter : filtering.getFilterConstraints()) {
                ApplicationListFilterCategory searchCategory = filter.getSearchCategory();
                String searchTerm = filter.getSearchTerm();

                if (searchCategory != null && StringUtils.isNotBlank(searchTerm)) {
                    Criterion criterion = null;
                    ResourceListSearchPredicate searchPredicate = filter.getSearchPredicate();

                    if (searchCategory.getType() == CategoryType.TEXT) {
                        switch (searchCategory) {
                        case APPLICANT_NAME:
                            criterion = ConcatenableIlikeCriterion.ilike(searchTerm, MatchMode.ANYWHERE, "applicant.firstName", "applicant.lastName");
                            break;
                        case APPLICATION_NUMBER:
                            criterion = Restrictions.ilike("application.applicationNumber", searchTerm, MatchMode.ANYWHERE);
                            break;
                        case PROGRAMME_NAME:
                            criterion = Restrictions.disjunction()
                                    .add(Restrictions.like("program.title", StringUtils.upperCase(searchTerm), MatchMode.ANYWHERE))
                                    .add(Restrictions.like("program.title", StringUtils.lowerCase(searchTerm), MatchMode.ANYWHERE))
                                    .add(Restrictions.like("program.code", StringUtils.upperCase(searchTerm), MatchMode.ANYWHERE))
                                    .add(Restrictions.like("program.code", StringUtils.lowerCase(searchTerm), MatchMode.ANYWHERE));
                            break;
                        case APPLICATION_STATUS:
                            // TODO: Implement this in a more sensible way, e.g. pass the actual enumeration value
                            // criterion = Restrictions.eq("application.state.id", PrismState.convert(searchTerm));
                            break;
                        case PROJECT_TITLE:
                            criterion = Restrictions.disjunction() //
                                    .add(Restrictions.isNull("project.title")) //
                                    .add(Restrictions.like("project.title", searchTerm, MatchMode.ANYWHERE));
                            break;
                        case SUPERVISOR:
                            criteria.createAlias("application.programDetails", "programDetails", JoinType.LEFT_OUTER_JOIN)
                                    .createAlias("application.approvalRounds", "approvalRounds", JoinType.LEFT_OUTER_JOIN)
                                    .createAlias("programDetails.suggestedSupervisors", "suggestedSupervisors", JoinType.LEFT_OUTER_JOIN)
                                    .createAlias("approvalRounds.supervisors", "supervisors", JoinType.LEFT_OUTER_JOIN)
                                    .createAlias("supervisors.user", "supervisorUser", JoinType.LEFT_OUTER_JOIN)
                                    .createAlias("project.primarySupervisor", "projectPrimarySupervisorUser", JoinType.LEFT_OUTER_JOIN)
                                    .createAlias("project.secondarySupervisor", "projectSecondarySupervisorUser", JoinType.LEFT_OUTER_JOIN);

                            criterion = Restrictions
                                    .disjunction()
                                    .add(ConcatenableIlikeCriterion
                                            .ilike(searchTerm, MatchMode.ANYWHERE, "supervisorUser.firstName", "supervisorUser.lastName"))
                                    .add(ConcatenableIlikeCriterion.ilike(searchTerm, MatchMode.ANYWHERE, "suggestedSupervisors.firstname",
                                            "suggestedSupervisors.lastname"))
                                    .add(ConcatenableIlikeCriterion.ilike(searchTerm, MatchMode.ANYWHERE, "projectPrimarySupervisorUser.firstName",
                                            "projectPrimarySupervisorUser.lastName"))
                                    .add(ConcatenableIlikeCriterion.ilike(searchTerm, MatchMode.ANYWHERE, "projectSecondarySupervisorUser.firstName",
                                            "projectSecondarySupervisorUser.lastName"));
                        default:
                        }

                    } else if (searchCategory.getType() == CategoryType.DATE) {
                        if (searchCategory == ApplicationListFilterCategory.SUBMISSION_DATE) {
                            criterion = getCriteriaForDate(searchPredicate, searchTerm, "application.submittedTimestamp");
                        } else if (searchCategory == ApplicationListFilterCategory.LAST_EDITED_DATE) {
                            // FIXME use comments
                            // criterion = getCriteriaForDate(searchPredicate, searchTerm, "application.lastUpdated");
                        } else if (searchCategory == ApplicationListFilterCategory.CLOSING_DATE) {
                            criterion = getCriteriaForDate(searchPredicate, searchTerm, "application.dueDate");
                        }

                    }

                    if (searchPredicate == ResourceListSearchPredicate.TEXT_NOT_CONTAINING) {
                        criterions.add(Restrictions.not(criterion));
                    } else {
                        criterions.add(criterion);
                    }
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

        }
    }

    private Criterion getCriteriaForDate(final ResourceListSearchPredicate searchPredicate, final String term, final String field) {
        Criterion criterion = null;
        Date submissionDate;
        try {
            submissionDate = USER_DATE_FORMAT.parseDateTime(term).toDate();
        } catch (IllegalArgumentException e) {
            return null;
        }

        if (submissionDate == null) {
            return null;
        }
        switch (searchPredicate) {
        case DATE_FROM:
            criterion = Restrictions.ge(field, submissionDate);
            break;
        case DATE_ON:
            Conjunction conjunction = Restrictions.conjunction();
            conjunction.add(Restrictions.ge(field, submissionDate));
            conjunction.add(Restrictions.lt(field, new DateTime(submissionDate).plusDays(1).toDate()));
            criterion = conjunction;
            break;
        case DATE_TO:
            criterion = Restrictions.lt(field, new DateTime(submissionDate).plusDays(1).toDate());
            break;
        default:
            return null;
        }
        return criterion;
    }

    private void appendOrderStatement(Criteria criteria, final Filter filtering) {
        ApplicationListSortCategory sortCategory = filtering.getSortCategory();

        boolean doSortAscending = true;
        if (filtering.getSortOrder() == ResourceListSortOrder.DESCENDING) {
            doSortAscending = false;
        }

        switch (sortCategory) {

        case URGENT:
            applyOrderByUrgentAndUpdate(criteria, doSortAscending);
            break;

        case UPDATE:
            applyOrderByUrgentAndUpdate(criteria, doSortAscending);
            break;

        case APPLICANT_NAME:
            criteria.addOrder(getOrderCriteria("applicant.lastName", doSortAscending));
            criteria.addOrder(getOrderCriteria("applicant.firstName", doSortAscending));
            applyDefaultSortOrder(criteria, doSortAscending);
            break;

        case PROGRAMME_NAME:
            criteria.addOrder(getOrderCriteria("program.title", doSortAscending));
            applyDefaultSortOrder(criteria, doSortAscending);
            break;

        case RATING:
            // TODO apply sort based on rating
//            criteria.addOrder(getOrderCriteria("application.averageRating", doSortAscending));
//            applyDefaultSortOrder(criteria, doSortAscending);

        case APPLICATION_STATUS:
            criteria.addOrder(getOrderCriteria("application.state.id", doSortAscending));
            applyDefaultSortOrder(criteria, doSortAscending);
            break;

        default:
        case APPLICATION_DATE:
            applyDefaultSortOrder(criteria, doSortAscending);
            break;

        }

    }

    private void applyDefaultSortOrder(Criteria criteria, Boolean doSortAscending) {
        criteria.addOrder(getOrderCriteria("application.submittedTimestamp", doSortAscending));
        criteria.addOrder(getOrderCriteria("application.createdTimestamp", doSortAscending));
    }

    private void applyOrderByUrgentAndUpdate(Criteria criteria, Boolean doSortAscending) {
        criteria.addOrder(Order.desc("raisesUrgentFlag"));
        criteria.addOrder(Order.desc("raisesUpdateFlag"));
        applyDefaultSortOrder(criteria, doSortAscending);
    }

    private Order getOrderCriteria(String propertyName, boolean ascending) {
        if (ascending) {
            return Order.asc(propertyName);
        } else {
            return Order.desc(propertyName);
        }
    }

    private void appendLimitStatement(Criteria criteria, int recordStart, int recordCount) {
        criteria.setFirstResult(recordStart);
        criteria.setMaxResults(recordCount);
    }

}