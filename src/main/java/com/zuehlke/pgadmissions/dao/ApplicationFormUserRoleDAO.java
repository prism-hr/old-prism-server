package com.zuehlke.pgadmissions.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormActionRequired;
import com.zuehlke.pgadmissions.domain.ApplicationFormUserRole;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.enums.ActionScope;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.ActionDefinition;

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
        return sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
        		.add(Restrictions.eq("applicationForm", applicationForm))
                .add(Restrictions.eq("user", user)).list();
    }

    public ApplicationFormUserRole findByApplicationFormAndUserAndAuthority(ApplicationForm applicationForm, RegisteredUser user, Authority authority) {
        return (ApplicationFormUserRole) sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
                .add(Restrictions.eq("applicationForm", applicationForm))
                .add(Restrictions.eq("user", user))
                .add(Restrictions.eq("role.id", authority)).uniqueResult();
    }
    
    public Date findUpdateTimestampByApplicationFormAndAuthorityUpdateVisility(ApplicationForm applicationForm, ApplicationUpdateScope updateVisibility) {
        return (Date) sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
        		.createAlias("role", "role")
                .add(Restrictions.eq("applicationForm", applicationForm))
                .add(Restrictions.ge("role.updateVisibility", updateVisibility))
                .setProjection(Projections.max("updateTimestamp")).uniqueResult();
    }
    
    public List<ApplicationFormUserRole> findByUserAndAuthority(RegisteredUser user, Authority authority) {
        return sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
                .add(Restrictions.eq("user", user))
                .add(Restrictions.eq("role.id", authority)).list();
    }
    
    public List<ApplicationFormUserRole>findByUserAndRoleWithOutstandingActions(RegisteredUser registeredUser, Role role) {
    	return sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
        		.createAlias("actions", "applicationFormActionRequired", JoinType.INNER_JOIN)
                .add(Restrictions.eq("role", role)).list();
    }

    public List<ApplicationFormUserRole> findByApplicationFormAndAuthorities(ApplicationForm applicationForm, Authority... authorities) {
        return sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
        		.add(Restrictions.eq("applicationForm", applicationForm))
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
    
    public List<ActionDefinition> findActionsByUserAndApplicationForm(RegisteredUser user, ApplicationForm applicationForm) {
    	return findActionsByUserIdAndApplicationIdAndApplicationFormStatus(user.getId(), applicationForm.getId(), applicationForm.getStatus());
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

    public boolean checkActionAvailableForUserAndApplicationForm(RegisteredUser registeredUser, ApplicationForm applicationForm, ApplicationFormAction action) {
        List<ActionDefinition> availableActions = this.findActionsByUserAndApplicationForm(registeredUser, applicationForm);
        for (ActionDefinition availableAction : availableActions) {
        	if (availableAction.getAction() == action ||
        			(availableAction.getAction() == ApplicationFormAction.VIEW_EDIT &&
        			action == ApplicationFormAction.VIEW)) {
        		return true;
        	}
        }
        return false;
    }
    
    public List<ActionDefinition> findActionsByUserIdAndApplicationIdAndApplicationFormStatus(Integer registeredUserId, Integer applicationFormId, ApplicationFormStatus status) {
    	Query query = sessionFactory.getCurrentSession()
    		.createSQLQuery("CALL SELECT_APPLICATION_FORM_ACTIONS(?, ?, ?);")
	    		.setInteger(0, registeredUserId)
	    		.setInteger(1, applicationFormId)
	    		.setString(2, status.toString());
    	
    	List<Object[]> rows = (List<Object[]>) query.list();
    	List<ActionDefinition> actionDefinitions = new ArrayList<ActionDefinition>();
    	
    	Boolean hasViewAction = false;
    	
    	for (Object[] row : rows) {
    		ApplicationFormAction action = ApplicationFormAction.valueOf((String)row[0]);
    		Boolean raisesUrgentFlag = BooleanUtils.toBooleanObject((Integer) row[1]);
			
			if (action == ApplicationFormAction.VIEW) {
				hasViewAction = true;
			}
    		
    		if (action == ApplicationFormAction.VIEW_EDIT &&
    			BooleanUtils.isTrue(hasViewAction)) {
    			actionDefinitions.remove(actionDefinitions.size() - 1);
    		}
    		
			actionDefinitions.add(new ActionDefinition(action, raisesUrgentFlag));
    	}
    	
    	return actionDefinitions;
    }

	public void updateRaisesUrgentFlag() {
		Session session = sessionFactory.getCurrentSession();
		
		Query query = session.createSQLQuery("CALL UPDATE_RAISES_URGENT_FLAG();");
		query.executeUpdate();
		
		session.flush();
	}
	
	public void updateApplicationFormActionRequiredDeadline(ApplicationForm applicationForm, Date deadlineTimestamp) {
		applicationForm.setDueDate(deadlineTimestamp);
		Session session = sessionFactory.getCurrentSession();
		
		Query query = session.createSQLQuery("CALL UPDATE_ACTION_REQUIRED_DEADLINE(?, ?);")
			.setInteger(0, applicationForm.getId())
			.setDate(1, deadlineTimestamp);
		query.executeUpdate();
		
		session.flush();
	}
	
	public void updateApplicationFormUpdateTimestamp(ApplicationForm applicationForm, RegisteredUser registeredUser, 
			Date updateTimestamp, ApplicationUpdateScope updateVisibility) {
        applicationForm.setLastUpdated(updateTimestamp);
		Session session = sessionFactory.getCurrentSession();
		
		Query query = sessionFactory.getCurrentSession()
			.createSQLQuery("CALL INSERT_APPLICATION_FORM_UPDATE(?, ?, ?, ?);")
				.setInteger(0, applicationForm.getId())
				.setInteger(1, registeredUser.getId())
				.setString(2, javaDateToMySQLDateString(updateTimestamp))
				.setInteger(3, ApplicationUpdateScope.valueOf(updateVisibility.toString()).ordinal());
		query.executeUpdate();
		
		session.flush();
	}
	
    public void insertUserinRole(RegisteredUser user, Authority authority) {
    	insertRoles(null, null, user, authority);
    }
    	
    public void insertUserInProgramRole(RegisteredUser user, Program program, Authority authority) {
    	insertRoles(program, null, user, authority);
    }
    
    public void insertUserInProjectRole(RegisteredUser user, Project project, Authority authority) {
    	insertRoles(project.getProgram(), project, user, authority);
    }
	
	public void deleteAllProgramRoles (Program program) {
		deleteRoles(program, null, null, null, null, ActionScope.APPLICATION);
	}
	
	public void deleteAllProjectRoles (Project project) {
		deleteRoles(project.getProgram(), project, null, null, null, ActionScope.APPLICATION);
	}
	
	public void deleteAllApplicationRoles (ApplicationForm application) {
		deleteRoles(application.getProgram(), application.getProject(), application, null, null, ActionScope.APPLICATION);
	}
	
	public void deleteAllStateRoles(ApplicationForm application) {
		deleteRoles(application.getProgram(), application.getProject(), application, null, null, ActionScope.STATE);
	}
	
    public void deleteUserFromSystemRole (RegisteredUser user, Authority authority) {
		deleteRoles(null, null, null, user, authority, ActionScope.APPLICATION);
    }
    
    public void deleteUserFromProgramRole (RegisteredUser user, Program program, Authority authority) {
		deleteRoles(program, null, null, user, authority, ActionScope.APPLICATION);
	}
    
    public void deleteUserFromProjectRole (RegisteredUser user, Project project, Authority authority) {
		deleteRoles(project.getProgram(), project, null, user, authority, ActionScope.APPLICATION);
    }
	
	public void deleteActionsAndFlushToDB(ApplicationFormUserRole applicationFormUserRole) {
		applicationFormUserRole.getActions().clear();
		applicationFormUserRole.setRaisesUrgentFlag(false);
		sessionFactory.getCurrentSession().flush();
	}
	
	private String javaDateToMySQLDateString(Date date) {
		SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return outputDateFormat.format(date);
	}
	
	private void insertRoles(Program program, Project project, RegisteredUser user, Authority authority) {
		Session session = sessionFactory.getCurrentSession();
		
		Query query = session.createSQLQuery("CALL INSERT_ROLES(?, ?, ?, ?);")
					.setInteger(0, program.getId())
					.setInteger(1, project.getId())
					.setInteger(2, user.getId())
					.setText(3, authority.toString());
		query.executeUpdate();
			
		session.flush();
	}

	private void deleteRoles(Program program, Project project, ApplicationForm application, RegisteredUser user, Authority authority, ActionScope scope) {
		Session session = sessionFactory.getCurrentSession();
		
		Query query = session.createSQLQuery("CALL DELETE_ROLES(?, ?, ?, ?, ?, ?);")
					.setInteger(0, program.getId())
					.setInteger(1, project.getId())
					.setInteger(2, application.getId())
					.setInteger(3, user.getId())
					.setText(4, authority.toString())
					.setText(5, scope.toString());
		query.executeUpdate();
			
		session.flush();
	}
	
}