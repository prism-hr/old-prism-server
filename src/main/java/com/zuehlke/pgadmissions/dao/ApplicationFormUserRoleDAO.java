package com.zuehlke.pgadmissions.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormActionRequired;
import com.zuehlke.pgadmissions.domain.ApplicationFormUserRole;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.AuthorityGroup;
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

    public Date findUpdateTimestampByApplicationFormAndAuthorityUpdateVisility(ApplicationForm applicationForm, ApplicationUpdateScope updateVisibility) {
        return (Date) sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class).createAlias("role", "role")
                .add(Restrictions.eq("applicationForm", applicationForm))
                .add(Restrictions.ge("role.updateVisibility", updateVisibility))
                .setProjection(Projections.projectionList()
                		.add(Projections.max("updateTimestamp"))).uniqueResult();
    }

    public ApplicationFormUserRole findByApplicationFormAndUserAndAuthority(ApplicationForm applicationForm, RegisteredUser user, Authority authority) {
        return (ApplicationFormUserRole) sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
                .add(Restrictions.eq("applicationForm", applicationForm))
                .add(Restrictions.eq("user", user))
                .add(Restrictions.eq("role.id", authority)).uniqueResult();
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
        List<ActionDefinition> availableActions = selectUserActions(registeredUser.getId(), applicationForm.getId(), applicationForm.getStatus());
        for (ActionDefinition availableAction : availableActions) {
        	if (availableAction.getAction() == action ||
        			(availableAction.getAction() == ApplicationFormAction.VIEW_EDIT &&
        			action == ApplicationFormAction.VIEW)) {
        		return true;
        	}
        }
        return false;
    }
    
    public List<RegisteredUser> findUsersInterestedInApplication(ApplicationForm applicationForm) {
        return sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
                .setProjection(Projections.projectionList()
                    .add(Projections.groupProperty("user"), "user"))
                .createAlias("user", "registeredUser", JoinType.INNER_JOIN)
                .add(Restrictions.eq("applicationForm", applicationForm))
                .add(Restrictions.eq("interestedInApplicant", true))
                .add(Restrictions.disjunction()
                        .add(Restrictions.eq("registeredUser.enabled", true))
                        .add(Restrictions.conjunction()
                            .add(Restrictions.eq("registeredUser.enabled", false))
                            .add(Restrictions.in("role.id", Arrays.asList(Authority.SUGGESTEDSUPERVISOR)))))
                .addOrder(Order.asc("registeredUser.lastName"))
                .addOrder(Order.asc("registeredUser.firstName"))
                .addOrder(Order.asc("registeredUser.id")).list();
    }

    public List<RegisteredUser> findUsersPotentiallyInterestedInApplication(ApplicationForm applicationForm) {
        DetachedCriteria usersInterestedInApplicant = DetachedCriteria.forClass(ApplicationFormUserRole.class)
                .setProjection(Projections.projectionList()
                    .add(Projections.groupProperty("user")))
                .add(Restrictions.eq("applicationForm", applicationForm))
                .add(Restrictions.eq("interestedInApplicant", true));
        
        return sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
                .setProjection(Projections.projectionList()
                    .add(Projections.groupProperty("user"), "user"))
                .createAlias("applicationForm", "applicationForm", JoinType.INNER_JOIN)
                .createAlias("applicationForm.program", "program", JoinType.INNER_JOIN)
                .createAlias("user", "registeredUser", JoinType.INNER_JOIN)
                .add(Restrictions.eq("program.id", applicationForm.getAdvert().getId()))
                .add(Restrictions.in("role.id", AuthorityGroup.getAllInternalRecruiterAuthorities()))
                .add(Restrictions.isNull("registeredUser.primaryAccount"))
                .add(Restrictions.eq("registeredUser.enabled", true))
                .add(Property.forName("user").notIn(usersInterestedInApplicant))
                .addOrder(Order.asc("registeredUser.lastName"))
                .addOrder(Order.asc("registeredUser.firstName"))
                .addOrder(Order.asc("registeredUser.id")).list();
    }
	
	public void deleteApplicationActions (ApplicationForm applicationForm) {
		Query query = sessionFactory.getCurrentSession()
			.createSQLQuery("CALL SP_DELETE_APPLICATION_ACTIONS(?);")
				.setInteger(0, applicationForm.getId());
		query.executeUpdate();
	}
	
    public void deleteApplicationUpdate(ApplicationForm applicationForm, RegisteredUser registeredUser) {
        Query query = sessionFactory.getCurrentSession()
            .createSQLQuery("CALL SP_DELETE_APPLICATION_UPDATE(?, ?);")
                .setInteger(0, applicationForm.getId())
                .setInteger(1, registeredUser.getId());
        query.executeUpdate();
    }
	
    public void deleteProgramRole (RegisteredUser registeredUser, Program program, Authority authority) {
        Query query = sessionFactory.getCurrentSession()
            .createSQLQuery("CALL SP_DELETE_PROGRAM_ROLE(?, ?, ?);")
                .setInteger(0, registeredUser.getId())
                .setInteger(1, program.getId())
                .setString(2, authority.toString());
        query.executeUpdate();
    }
    
	
    public void deleteStateActions(ApplicationForm applicationForm) {
        Query query = sessionFactory.getCurrentSession()
            .createSQLQuery("CALL SP_DELETE_STATE_ACTIONS(?);")
                .setInteger(0, applicationForm.getId());
        query.executeUpdate();
    }
	
    public void deleteRoleAction(ApplicationForm applicationForm, Authority authority, ApplicationFormAction action) {
        Query query = sessionFactory.getCurrentSession()
            .createSQLQuery("CALL SP_DELETE_ROLE_ACTION(?, ?, ?);")
                .setInteger(0, applicationForm.getId())
                .setString(1, authority.toString())
                .setString(2, action.toString());
        query.executeUpdate();
    }
    
    public void deleteUserAction(ApplicationForm applicationForm, RegisteredUser registeredUser, Authority authority, ApplicationFormAction action) {
        Query query = sessionFactory.getCurrentSession()
            .createSQLQuery("CALL SP_DELETE_USER_ACTION(?, ?, ?, ?);")
                .setInteger(0, applicationForm.getId())
                .setInteger(1, registeredUser.getId())
                .setString(2, authority.toString())
                .setString(3, action.toString());
        query.executeUpdate();
    }
    
    public void deleteUserRole (RegisteredUser registeredUser, Authority authority) {
        Query query = sessionFactory.getCurrentSession()
            .createSQLQuery("CALL SP_DELETE_USER_ROLE(?, ?);")
                .setInteger(0, registeredUser.getId())
                .setString(1, authority.toString());
        query.executeUpdate();
    }
    
    public void insertApplicationUpdate(ApplicationForm applicationForm, RegisteredUser registeredUser, 
            Date updateTimestamp, ApplicationUpdateScope updateVisibility) {
        Query query = sessionFactory.getCurrentSession()
            .createSQLQuery("CALL SP_INSERT_APPLICATION_UPDATE(?, ?, ?, ?);")
                .setInteger(0, applicationForm.getId())
                .setInteger(1, registeredUser.getId())
                .setString(2, javaDateToMySQLDateString(updateTimestamp))
                .setInteger(3, ApplicationUpdateScope.valueOf(updateVisibility.toString()).ordinal());
        query.executeUpdate();
    }
    
    public void insertProgramRole(RegisteredUser registeredUser, Program program, Authority authority) {
        Query query = sessionFactory.getCurrentSession()
            .createSQLQuery("CALL SP_INSERT_PROGRAM_ROLE(?, ?, ?);")
                .setInteger(0, registeredUser.getId())
                .setInteger(1, program.getId())
                .setString(2, authority.toString());
        query.executeUpdate();
    }
    
    public void insertUserRole(RegisteredUser registeredUser, Authority authority) {
        Query query = sessionFactory.getCurrentSession()
            .createSQLQuery("CALL SP_INSERT_USER_ROLE(?, ?);")
                .setInteger(0, registeredUser.getId())
                .setString(1, authority.toString());
        query.executeUpdate();
    }
    
    public List<ActionDefinition> selectUserActions(Integer registeredUserId, Integer applicationFormId, ApplicationFormStatus status) {
        Query query = sessionFactory.getCurrentSession()
            .createSQLQuery("CALL SP_SELECT_USER_ACTIONS(?, ?, ?);")
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
    
    public void updateApplicationDueDate(ApplicationForm applicationForm, Date deadlineTimestamp) {        
        Query query = sessionFactory.getCurrentSession()
            .createSQLQuery("CALL SP_UPDATE_APPLICATION_FORM_DUE_DATE(?, ?);")
                .setInteger(0, applicationForm.getId())
                .setDate(1, deadlineTimestamp);
        query.executeUpdate();
    }
    
    public void updateApplicationInterest(ApplicationForm applicationForm, RegisteredUser registeredUser, Boolean interested) {
        Query query = sessionFactory.getCurrentSession()
            .createSQLQuery("CALL SP_UPDATE_APPLICATION_INTEREST(?, ?, ?);")
                .setInteger(0, applicationForm.getId())
                .setInteger(1, registeredUser.getId())
                .setBoolean(2, interested);
        query.executeUpdate();
    }
    
    public void updateUrgentApplications() {
        Query query = sessionFactory.getCurrentSession().
            createSQLQuery("CALL SP_UPDATE_URGENT_APPLICATIONS();");
        query.executeUpdate();
    }
    
	private String javaDateToMySQLDateString(Date date) {
		SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return outputDateFormat.format(date);
	}
	
}
