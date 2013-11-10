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

import com.zuehlke.pgadmissions.domain.ApplicationFormUserRole;
import com.zuehlke.pgadmissions.domain.ApplicationsFilter;
import com.zuehlke.pgadmissions.domain.ApplicationsFiltering;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.SearchCategory;
import com.zuehlke.pgadmissions.domain.enums.SearchPredicate;
import com.zuehlke.pgadmissions.domain.enums.SortCategory;
import com.zuehlke.pgadmissions.domain.enums.SortOrder;
import com.zuehlke.pgadmissions.domain.enums.SearchCategory.CategoryType;
import com.zuehlke.pgadmissions.dto.ApplicationDescriptor;

@Repository
public class ApplicationFormListDAO {

    public static final DateTimeFormatter USER_DATE_FORMAT = DateTimeFormat.forPattern("dd MMM yyyy");
    
    private final Criteria criteria;
    
    private final ApplicationFormUserRoleDAO applicationFormUserRoleDAO;
    
    public ApplicationFormListDAO() {
        this(null);
    }

    @Autowired
    public ApplicationFormListDAO(SessionFactory sessionFactory) {
        this.criteria = sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class);
        this.applicationFormUserRoleDAO = new ApplicationFormUserRoleDAO();
    }
    
    @SuppressWarnings("unchecked")
    public List<ApplicationDescriptor> getVisibleApplications(final RegisteredUser user, final ApplicationsFiltering filtering, final int itemsPerPage) {
    	// Now that we can understand what this is doing, it should be possible to tune the performance!
    	criteria.setReadOnly(true)
    		.setProjection(Projections.projectionList()
    			.add(Projections.groupProperty("application"), "applicationForm")
    			.add(Projections.max("raisesUrgentFlag"), "needsToSeeUrgentFlag")
    			.add(Projections.max("raisesUpdateFlag"), "needsToSeeUpdateFlag")
    			.add(Projections.property("applicant.id"), "applicantId")
    			.add(Projections.property("applicant.firstName"), "applicantFirstName")
    			.add(Projections.property("applicant.firstName2"), "applicantFirstName2")
    			.add(Projections.property("applicant.firstName3"), "applicantFirstName3")
    			.add(Projections.property("applicant.LastName"), "applicantLastName")
    			.add(Projections.property("program.title"), "programTitle")
    			.add(Projections.property("advert.title"), "projectTitle")
    			.add(Projections.property("applicationForm.projectTitle"), "oldProjectTitle")
    			.add(Projections.max("updateTimestamp"), "applicationFormUpdatedTimestmap"))
		.createAlias("applicationForm", "applicationForm", JoinType.INNER_JOIN)
		.createAlias("applicationForm.applicant", "applicant", JoinType.INNER_JOIN)
		.createAlias("applicationForm.program", "program", JoinType.INNER_JOIN)
		.createAlias("applicationForm.project", "project", JoinType.LEFT_OUTER_JOIN)
		.createAlias("project.advert", "advert", JoinType.LEFT_OUTER_JOIN)
       	.add(Restrictions.eq("user", user));
    	
    	appendWhereStatement(filtering);
    	appendOrderStatement(filtering);
    	appendLimitStatement((filtering.getBlockCount() - 1) * itemsPerPage, itemsPerPage);
    	
    	List <ApplicationDescriptor> applications = criteria.setResultTransformer(Transformers.aliasToBean(ApplicationDescriptor.class)).list();
    	
        for (ApplicationDescriptor application : applications) {
        	application.getActionDefinitions().addAll(applicationFormUserRoleDAO.findActionsByUserAndApplicationForm(user, application.getApplicationForm()));
        }
      
        return applications;
    }
    
    private void appendWhereStatement(ApplicationsFiltering filtering) {
    	
        if (filtering != null) {
            boolean useDisjunction = filtering.getUseDisjunction();
        	
            List<Criterion> criterions = new ArrayList<Criterion>();
            for (ApplicationsFilter filter : filtering.getFilters()) {
            	SearchCategory searchCategory = filter.getSearchCategory();
            	String searchTerm = filter.getSearchTerm();
            	
            	if (searchCategory != null && StringUtils.isNotBlank(searchTerm)) {
                    Criterion criterion = null;
                    SearchPredicate searchPredicate = filter.getSearchPredicate();
                    
                    if (searchCategory.getType() == CategoryType.TEXT) {
                        switch (searchCategory) {
                        case APPLICANT_NAME:
                        	criterion = ConcatenableIlikeCriterion.ilike(searchTerm, MatchMode.ANYWHERE, "applicant.firstName", "applicant.lastName");
                            break;
                        case APPLICATION_NUMBER:
                        	criterion = Restrictions.ilike("applicationForm.applicationNumber", searchTerm, MatchMode.ANYWHERE);
                            break;
                        case PROGRAMME_NAME:
                        	criterion = Restrictions.disjunction()
                        			.add(Restrictions.like("program.title", StringUtils.upperCase(searchTerm), MatchMode.ANYWHERE))
                                    .add(Restrictions.like("program.title", StringUtils.lowerCase(searchTerm), MatchMode.ANYWHERE))
                                    .add(Restrictions.like("program.code", StringUtils.upperCase(searchTerm), MatchMode.ANYWHERE))
                                    .add(Restrictions.like("program.code", StringUtils.lowerCase(searchTerm), MatchMode.ANYWHERE));
                            break;
                        case APPLICATION_STATUS:
                        	criterion = Restrictions.eq("applicationForm.status", ApplicationFormStatus.convert(searchTerm));
                            break;
                        case PROJECT_TITLE:
                        	criterion = Restrictions.disjunction()
                        			.add(Restrictions.ilike("applicationForm.projectTitle", searchTerm, MatchMode.ANYWHERE))
                        			.add(Restrictions.ilike("advert.title", searchTerm, MatchMode.ANYWHERE));
                            break;
                        case SUPERVISOR:
                            criteria.createAlias("applicationForm.programmeDetails", "programmeDetails", JoinType.LEFT_OUTER_JOIN)
                            		.createAlias("applicationForm.approvalRounds", "approvalRounds", JoinType.LEFT_OUTER_JOIN)
                            		.createAlias("programmeDetails.suggestedSupervisor", "suggestedSupervisor", JoinType.LEFT_OUTER_JOIN)
                            		.createAlias("approvalRounds.supervisors", "supervisors", JoinType.LEFT_OUTER_JOIN)
                            		.createAlias("supervisors.user", "supervisorUser", JoinType.LEFT_OUTER_JOIN)
                            		.createAlias("advert.primarySupervisor", "advertPrimarySupervisorUser", JoinType.LEFT_OUTER_JOIN)
                            		.createAlias("advert.secondarySupervisor", "advertSecondarySupervisorUser", JoinType.LEFT_OUTER_JOIN);

                            criterion = Restrictions.disjunction()
                                    .add(ConcatenableIlikeCriterion.ilike(searchTerm, MatchMode.ANYWHERE, "supervisorUser.firstName", "supervisorUser.lastName"))
                                    .add(ConcatenableIlikeCriterion.ilike(searchTerm, MatchMode.ANYWHERE, "suggestedSupervisor.firstname", "suggestedSupervisor.lastname"))
                                    .add(ConcatenableIlikeCriterion.ilike(searchTerm, MatchMode.ANYWHERE, "advertPrimarySupervisorUser.firstName", "advertPrimarySupervisorUser.lastName"))
                                    .add(ConcatenableIlikeCriterion.ilike(searchTerm, MatchMode.ANYWHERE, "advertSecondarySupervisorUser.firstName", "advertSecondarySupervisorUser.lastName"));
                        default:
                        }
                        
                        if (searchPredicate == SearchPredicate.NOT_CONTAINING) {
                            criterions.add(Restrictions.not(criterion));
                        } else {
                        	criterions.add(criterion);
                        }
                        
                    } else if (searchCategory.getType() == CategoryType.DATE) {
                    	
                        if (searchCategory == SearchCategory.SUBMISSION_DATE) {
                            criterion = getCriteriaForDate(searchPredicate, searchTerm, criteria, "applicationForm.submittedDate");
                        } else if (searchCategory == SearchCategory.LAST_EDITED_DATE) {
                            criterion = getCriteriaForDate(searchPredicate, searchTerm, criteria, "applicationForm.lastUpdated");
                        } else if (searchCategory == SearchCategory.CLOSING_DATE) {
                            criterion = getCriteriaForDate(searchPredicate, searchTerm, criteria, "applicationForm.batchDeadline");
                        }
                        
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
    
    private Criterion getCriteriaForDate(final SearchPredicate searchPredicate, final String term, final Criteria criteria, final String field) {
        Criterion newCriterion = null;
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
    
    private void appendOrderStatement(final ApplicationsFiltering filtering) {
    	SortCategory sortCategory = filtering.getSortCategory();
    	
    	if (sortCategory == null) {
            criteria.addOrder(Order.desc("raisesUrgentFlag"));
            criteria.addOrder(Order.desc("raisesUpdateFlag"));
    	} else {
	        boolean ascending = true;
	        
	        if (filtering.getOrder() == SortOrder.DESCENDING) {
	            ascending = false;
	        }
	        
	        switch (sortCategory) {
	
	        case APPLICANT_NAME:
	            criteria.addOrder(getOrderCriteria("applicant.lastName", ascending));
	            criteria.addOrder(getOrderCriteria("applicant.firstName", ascending));
	            break;
	
	        case PROGRAMME_NAME:
	            criteria.addOrder(getOrderCriteria("project.title", ascending));
	            break;
	
	        case RATING:
	            criteria.addOrder(getOrderCriteria("applicationForm.averageRating", ascending));
	
	        case APPLICATION_STATUS:
	            criteria.addOrder(getOrderCriteria("applicationForm.status", ascending));
	            break;
	
	        default:
	        case APPLICATION_DATE:
	            criteria.addOrder(getOrderCriteria("applicationForm.submittedDate", ascending));
	            criteria.addOrder(getOrderCriteria("applicationFrm=ork.applicationTimestamp", ascending));
	            break;
	        }
    	}
    }
    
    private Order getOrderCriteria(String propertyName, boolean ascending) {
        if (ascending) {
            return Order.asc(propertyName);
        } else {
            return Order.desc(propertyName);
        }
    }
    
    private void appendLimitStatement(int recordStart, int recordCount) {
    	criteria.setFirstResult(recordStart);
    	criteria.setMaxResults(recordCount);
    }
    
}