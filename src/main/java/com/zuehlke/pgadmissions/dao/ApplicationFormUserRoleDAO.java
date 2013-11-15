package com.zuehlke.pgadmissions.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormActionOptional;
import com.zuehlke.pgadmissions.domain.ApplicationFormActionRequired;
import com.zuehlke.pgadmissions.domain.ApplicationFormUserRole;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.ActionDefinition;
import com.zuehlke.pgadmissions.dto.ApplicationFormAction;

@Repository
@SuppressWarnings("unchecked")
public class ApplicationFormUserRoleDAO {

    private final SessionFactory sessionFactory;

    public ApplicationFormUserRoleDAO() {
        this(null);
    }

    @Autowired
    public ApplicationFormUserRoleDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(ApplicationFormUserRole applicationFormUserRole) {
        sessionFactory.getCurrentSession().saveOrUpdate(applicationFormUserRole);
    }

    public List<ApplicationFormUserRole> findByApplicationFormAndUser(ApplicationForm applicationForm, RegisteredUser user) {
        return sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class).add(Restrictions.eq("applicationForm", applicationForm))
                .add(Restrictions.eq("user", user)).list();
    }

    public Date findUpdateTimestampByApplicationFormAndAuthorityUpdateVisility(ApplicationForm applicationForm, ApplicationUpdateScope updateVisibility) {
        return (Date) sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class).createAlias("role", "role")
                .add(Restrictions.eq("applicationForm", applicationForm)).add(Restrictions.ge("role.updateVisibility", updateVisibility))
                .setProjection(Projections.projectionList().add(Projections.max("updateTimestamp"))).uniqueResult();
    }

    public ApplicationFormUserRole findByApplicationFormAndUserAndAuthority(ApplicationForm applicationForm, RegisteredUser user, Authority authority) {
        return (ApplicationFormUserRole) sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
                .add(Restrictions.eq("applicationForm", applicationForm)).add(Restrictions.eq("user", user)).add(Restrictions.eq("role.id", authority))
                .uniqueResult();
    }

    public List<ApplicationFormUserRole> findByApplicationFormAndAuthorities(ApplicationForm applicationForm, Authority... authorities) {
        return sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class).add(Restrictions.eq("applicationForm", applicationForm))
                .add(Restrictions.in("role.id", authorities)).list();
    }

    public ApplicationFormUserRole get(Integer id) {
        return (ApplicationFormUserRole) sessionFactory.getCurrentSession().get(ApplicationFormUserRole.class, id);
    }

    public void delete(ApplicationFormUserRole applicationFormUserRole) {
        sessionFactory.getCurrentSession().delete(applicationFormUserRole);
        sessionFactory.getCurrentSession().flush();
    }

    public void delete(ApplicationFormActionRequired action) {
        sessionFactory.getCurrentSession().delete(action);
        sessionFactory.getCurrentSession().flush();
    }

    public void clearActions(ApplicationFormUserRole role) {
        role.getActions().clear();
        sessionFactory.getCurrentSession().flush();
    }
    
    public List<ActionDefinition> findActionsByUserAndApplicationForm (RegisteredUser user, ApplicationForm applicationForm) {
    	return findActionsByUserIdAndApplicationIdAndApplicationFormStatus(user.getId(), applicationForm.getId(), applicationForm.getStatus());
    }
    
    public List<ActionDefinition> findActionsByUserIdAndApplicationIdAndApplicationFormStatus(Integer registeredUserId, Integer applicationFormId, ApplicationFormStatus status) {
    	Query query = sessionFactory.getCurrentSession()
    		.createSQLQuery("CALL SELECT_USER_APPLICATION_FORM_ACTION_LIST(?, ?, ?);")
	    		.setInteger(0, registeredUserId)
	    		.setInteger(1, applicationFormId)
	    		.setString(2, status.toString());
    	
    	List<Object[]> rows = (List<Object[]>) query.list();
    	List<ActionDefinition> actionDefinitions = new ArrayList<ActionDefinition>();
    	
    	boolean hasViewAction = false;
    	
    	for (Object[] row : rows) {
    		String action = (String) row[0];
    		Boolean raisesUrgentFlag = BooleanUtils.toBooleanObject((Integer) row[1]);
			
			if (action.equals("VIEW")) {
				hasViewAction = true;
			}
    		
    		if (action.equals("VIEW_EDIT") &&
    			BooleanUtils.isTrue(hasViewAction)) {
    			actionDefinitions.remove(actionDefinitions.size() - 1);
    		}
    		
			actionDefinitions.add(new ActionDefinition(action, raisesUrgentFlag));
    	}
    	
    	return actionDefinitions;
    }

    public Boolean findRaisesUpdateFlagByUserAndApplicationForm(RegisteredUser user, ApplicationForm applicationForm) {
        Boolean raisesUpdateFlag = (Boolean) sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
                .add(Restrictions.eq("applicationForm", applicationForm))
                .add(Restrictions.eq("user", user))
                .addOrder(Order.desc("raisesUpdateFlag"))
                .setProjection(Projections.projectionList().add(Projections.max("raisesUpdateFlag")))
                .uniqueResult();
        return BooleanUtils.toBoolean(raisesUpdateFlag);
    }
    
    public Boolean findRaisesUrgentFlagByUserAndApplicationForm(RegisteredUser user, ApplicationForm applicationForm) {
        Boolean raisesUrgentFlag = (Boolean) sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
                .add(Restrictions.eq("applicationForm", applicationForm))
                .add(Restrictions.eq("user", user))
                .addOrder(Order.desc("raisesUrgentFlag"))
                .setProjection(Projections.projectionList().add(Projections.max("raisesUrgentFlag")))
                .uniqueResult();
        return BooleanUtils.toBoolean(raisesUrgentFlag);
    }

    public boolean checkActionAvailableForUserAndApplicationForm(RegisteredUser user, ApplicationForm applicationForm, ApplicationFormAction action) {
        Object requiredResult = sessionFactory.getCurrentSession().createCriteria(ApplicationFormActionRequired.class)
                .createAlias("applicationFormUserRole", "role")
                .add(Restrictions.eq("role.applicationForm", applicationForm))
                .add(Restrictions.eq("role.user", user))
                .add(Restrictions.eq("action", action.name()))
                .setProjection(Projections.projectionList()
                .add(Projections.groupProperty("action"))).uniqueResult();

        DetachedCriteria subquery = DetachedCriteria.forClass(ApplicationFormUserRole.class).add(Restrictions.eq("applicationForm", applicationForm))
                .add(Restrictions.eq("user", user)).setProjection(Projections.property("role.id"));

        Object optionalResult = sessionFactory.getCurrentSession()
                .createCriteria(ApplicationFormActionOptional.class)
                .add(Subqueries.propertyIn("id.role.id", subquery))
                .add(Restrictions.eq("id.status", applicationForm.getStatus()))
                .add(Restrictions.eq("id.action", action.name()))
                .uniqueResult();
        return requiredResult != null || optionalResult != null;
    }
    
    public void insertUserinRole(RegisteredUser registeredUser, Authority authority) {
    	Query query = sessionFactory.getCurrentSession()
    		.createSQLQuery("CALL INSERT_USER_IN_ROLE(?, ?);")
	    		.setInteger(0, registeredUser.getId())
	    		.setString(1, authority.toString());
    		query.executeUpdate();
    }
    	
    public void insertUserInProgramRole(RegisteredUser registeredUser, Program program, Authority authority) {
    	Query query = sessionFactory.getCurrentSession()
    		.createSQLQuery("CALL INSERT_USER_IN_PROGRAM_ROLE(?, ?, ?);")
	    		.setInteger(0, registeredUser.getId())
	    		.setInteger(1, program.getId())
	    		.setString(2, authority.toString());
    	query.executeUpdate();
    }
    
    public void deleteUserFromRole (RegisteredUser registeredUser, Authority authority) {
    	Query query = sessionFactory.getCurrentSession()
    		.createSQLQuery("CALL DELETE_USER_FROM_ROLE(?, ?);")
	    		.setInteger(0, registeredUser.getId())
	    		.setString(1, authority.toString());
    	query.executeUpdate();
    }
    
    public void deleteUserFromProgramRole (RegisteredUser registeredUser, Program program, Authority authority) {
		Query query = sessionFactory.getCurrentSession()
			.createSQLQuery("CALL DELETE_USER_FROM_PROGRAM_ROLE(?, ?, ?);")
				.setInteger(0, registeredUser.getId())
				.setInteger(1, program.getId())
				.setString(2, authority.toString());
		query.executeUpdate();
	}

	public void updateRaisesUrgentFlag() {
		Query query = sessionFactory.getCurrentSession().
			createSQLQuery("CALL UPDATE_RAISES_URGENT_FLAG();");
		query.executeUpdate();
	}
	
	public void updateApplicationFormActionRequiredDeadline(ApplicationForm applicationForm, Date deadlineTimestamp) {
		applicationForm.setDueDate(deadlineTimestamp);
		Session session = sessionFactory.getCurrentSession();
		session.flush();
		
		Query query = session.createSQLQuery("CALL UPDATE_APPLICATION_FORM_ACTION_REQUIRED_DEADLINE(?, ?);")
			.setInteger(0, applicationForm.getId())
			.setDate(1, deadlineTimestamp);
		query.executeUpdate();
	}
	
	public void updateApplicationFormUpdateTimestamp(ApplicationForm applicationForm, RegisteredUser registeredUser, 
			Date updateTimestamp, ApplicationUpdateScope updateVisibility) {
		Query query = sessionFactory.getCurrentSession()
			.createSQLQuery("CALL INSERT_APPLICATION_FORM_USER_ROLE_UPDATE(?, ?, ?, ?);")
				.setInteger(0, applicationForm.getId())
				.setInteger(1, registeredUser.getId())
				.setDate(2, updateTimestamp)
				.setInteger(3, ApplicationUpdateScope.valueOf(updateVisibility.toString()).ordinal());
		query.executeUpdate();
		
	}
	
	public void deleteAllApplicationFormActions (ApplicationForm applicationForm) {
		Query query = sessionFactory.getCurrentSession()
			.createSQLQuery("CALL DELETE_APPLICATION_FORM_ACTIONS(?);")
				.setInteger(0, applicationForm.getId());
		query.executeUpdate();
	}
	
	public void deleteApplicationFormActionsForStateBoundedWorkers(ApplicationForm applicationForm) {
		Query query = sessionFactory.getCurrentSession()
			.createSQLQuery("CALL DELETE_ACTIONS_FOR_STATE_BOUNDED_WORKERS(?);")
				.setInteger(0, applicationForm.getId());
		query.executeUpdate();
	}

	public List<RegisteredUser> findUsersInterestedInApplication(ApplicationForm applicationForm) {
		return sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
				.setProjection(Projections.projectionList()
					.add(Projections.groupProperty("user"), "user"))
				.createAlias("user", "registeredUser", JoinType.INNER_JOIN)
				.add(Restrictions.eq("applicationForm", applicationForm))
				.add(Restrictions.eq("interestedInApplicant", true))
				.add(Restrictions.eq("registeredUser.enabled", true))
				.addOrder(Order.asc("registeredUser.lastName"))
				.addOrder(Order.asc("registeredUser.firstName"))
				.addOrder(Order.asc("registeredUser.id")).list();
	}

	public List<RegisteredUser> findUsersPotentiallyInterestedInApplication(Program program) {
		return sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
				.setProjection(Projections.projectionList()
					.add(Projections.groupProperty("user"), "user"))
				.createAlias("applicationForm", "applicationForm", JoinType.INNER_JOIN)
				.createAlias("applicationForm.program", "program", JoinType.INNER_JOIN)
				.createAlias("user", "registeredUser", JoinType.INNER_JOIN)
				.add(Restrictions.eq("program.id", program.getId()))
				.add(Restrictions.in("role.id", Arrays.asList(Authority.REVIEWER, Authority.INTERVIEWER, 
					Authority.SUPERVISOR, Authority.SUGGESTEDSUPERVISOR, Authority.APPROVER, Authority.REVIEWADMINISTRATOR,
					Authority.INTERVIEWADMINISTRATOR, Authority.APPROVALADMINISTRATOR, Authority.PROJECTADMINISTRATOR)))
				.add(Restrictions.eq("registeredUser.enabled", true))
				.add(Restrictions.isNull("registeredUser.primaryAccount"))
				.addOrder(Order.asc("registeredUser.lastName"))
				.addOrder(Order.asc("registeredUser.firstName"))
				.addOrder(Order.asc("registeredUser.id")).list();
	}
	
	public void deleteActionsAndFlushToDB(ApplicationFormUserRole applicationFormUserRole) {
		applicationFormUserRole.getActions().clear();
		applicationFormUserRole.setRaisesUrgentFlag(false);
		sessionFactory.getCurrentSession().flush();
	}
}