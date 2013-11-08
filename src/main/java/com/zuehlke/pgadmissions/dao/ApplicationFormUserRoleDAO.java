package com.zuehlke.pgadmissions.dao;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormActionOptional;
import com.zuehlke.pgadmissions.domain.ApplicationFormActionRequired;
import com.zuehlke.pgadmissions.domain.ApplicationFormUserRole;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
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

    public List<ApplicationFormUserRole> findByApplicationFormAndAuthorityUpdateVisility(ApplicationForm applicationForm,
            ApplicationUpdateScope updateVisibility) {
        return sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class).createAlias("role", "role")
                .add(Restrictions.eq("applicationForm", applicationForm)).add(Restrictions.ge("role.updateVisibility", updateVisibility)).list();
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

    public List<ApplicationFormUserRole> findByUserAndProgramAndAuthority(RegisteredUser registeredUser, Program program, Authority authority) {
        return sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class).createAlias("applicationForm.program", "program")
                .add(Restrictions.eq("user", registeredUser)).add(Restrictions.eq("program", program)).add(Restrictions.eq("role.id", authority)).list();
    }

    public List<ApplicationFormUserRole> findByUserAndAuthority(RegisteredUser registeredUser, Authority authority) {
        return sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class).add(Restrictions.eq("user", registeredUser))
                .add(Restrictions.eq("role.id", authority)).list();
    }

    public List<ApplicationFormUserRole> findByApplicationForm(ApplicationForm applicationForm) {
        return sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class).add(Restrictions.eq("applicationForm", applicationForm)).list();
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

    public List<ActionDefinition> findRequiredActionsByUserAndApplicationForm(RegisteredUser user, ApplicationForm applicationForm) {
        List<Object[]> actionObjects = (List<Object[]>) sessionFactory.getCurrentSession()
                //
                .createCriteria(ApplicationFormActionRequired.class)
                //
                .createAlias("applicationFormUserRole", "role")
                //
                .add(Restrictions.eq("role.applicationForm", applicationForm))
                //
                .add(Restrictions.eq("role.user", user))
                //
                .addOrder(Order.desc("raisesUrgentFlag")).addOrder(Order.asc("action"))
                .setProjection(Projections.projectionList().add(Projections.groupProperty("action")).add(Projections.max("raisesUrgentFlag"))).list();

        List<ActionDefinition> actionDefinitions = Lists.newArrayListWithCapacity(actionObjects.size());
        for (Object[] actionObject : actionObjects) {
            ActionDefinition actionDefinition = new ActionDefinition((String) actionObject[0], (Boolean) actionObject[1]);
            actionDefinitions.add(actionDefinition);
        }
        return actionDefinitions;
    }

    public List<ActionDefinition> findOptionalActionsByUserAndApplicationForm(RegisteredUser user, ApplicationForm application) {
        DetachedCriteria subquery = DetachedCriteria.forClass(ApplicationFormUserRole.class).add(Restrictions.eq("applicationForm", application))
                .add(Restrictions.eq("user", user)).setProjection(Projections.property("role.id"));

        List<Object> actionObjects = (List<Object>) sessionFactory.getCurrentSession() //
                .createCriteria(ApplicationFormActionOptional.class) //
                .add(Subqueries.propertyIn("id.role.id", subquery)) //
                .add(Restrictions.eq("id.status", application.getStatus())) //
                .addOrder(Order.asc("id.action")).setProjection(Projections.distinct(Projections.property("id.action"))).list();

        List<ActionDefinition> actionDefinitions = Lists.newArrayListWithCapacity(actionObjects.size());
        for (Object actionObject : actionObjects) {
            ActionDefinition actionDefinition = new ActionDefinition((String) actionObject, false);
            actionDefinitions.add(actionDefinition);
        }
        return actionDefinitions;
    }

    public Boolean findRaisesUpdateFlagByUserAndApplicationForm(RegisteredUser user, ApplicationForm applicationForm) {
        Boolean raisesUpdateFlag = (Boolean) sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)//
                .add(Restrictions.eq("applicationForm", applicationForm)) //
                .add(Restrictions.eq("user", user)) //
                .setProjection(Projections.projectionList().add(Projections.max("raisesUpdateFlag"))) //
                .uniqueResult();
        return BooleanUtils.toBoolean(raisesUpdateFlag);
    }

    public boolean checkActionAvailableForUserAndApplicationForm(RegisteredUser user, ApplicationForm applicationForm, ApplicationFormAction action) {
        Object requiredResult = sessionFactory.getCurrentSession().createCriteria(ApplicationFormActionRequired.class)
                .createAlias("applicationFormUserRole", "role").add(Restrictions.eq("role.applicationForm", applicationForm))
                .add(Restrictions.eq("role.user", user)).add(Restrictions.eq("action", action.name()))
                .setProjection(Projections.projectionList().add(Projections.groupProperty("action"))).uniqueResult();

        DetachedCriteria subquery = DetachedCriteria.forClass(ApplicationFormUserRole.class).add(Restrictions.eq("applicationForm", applicationForm))
                .add(Restrictions.eq("user", user)).setProjection(Projections.property("role.id"));

        Object optionalResult = sessionFactory.getCurrentSession() //
                .createCriteria(ApplicationFormActionOptional.class) //
                .add(Subqueries.propertyIn("id.role.id", subquery)) //
                .add(Restrictions.eq("id.status", applicationForm.getStatus())) //
                .add(Restrictions.eq("id.action", action.name())) //
                .uniqueResult();
        return requiredResult != null || optionalResult != null;
    }
    
    public void insertUserinRole(RegisteredUser registeredUser, Authority authority) {
    	Session session = sessionFactory.getCurrentSession();
    	Query query = session.createSQLQuery("CALL INSERT_USER_IN_ROLE(?, ?);");
    	query.setInteger(0, registeredUser.getId());
    	query.setString(1, authority.toString());
    	query.executeUpdate();
    }
    	
    public void insertUserInProgramRole(RegisteredUser registeredUser, Program program, Authority authority) {
    	Session session = sessionFactory.getCurrentSession();
    	Query query = session.createSQLQuery("CALL INSERT_USER_IN_PROGRAM_ROLE(?, ?, ?);");
    	query.setInteger(0, registeredUser.getId());
    	query.setInteger(1, program.getId());
    	query.setString(2, authority.toString());
    	query.executeUpdate();
    }
    
    public void deleteUserFromRole (RegisteredUser registeredUser, Authority authority) {
    	Session session = sessionFactory.getCurrentSession();
    	Query query = session.createSQLQuery("CALL DELETE_USER_FROM_ROLE(?, ?);");
    	query.setInteger(0, registeredUser.getId());
    	query.setString(1, authority.toString());
    	query.executeUpdate();
    }
    
    public void deleteUserFromProgramRole (RegisteredUser registeredUser, Program program, Authority authority) {
    	Session session = sessionFactory.getCurrentSession();
    	Query query = session.createSQLQuery("CALL DELETE_USER_FROM_PROGRAM_ROLE(?, ?, ?);");
    	query.setInteger(0, registeredUser.getId());
    	query.setInteger(1, program.getId());
    	query.setString(2, authority.toString());
    	query.executeUpdate();
    }
    
    public void updateRaisesUrgentFlag () {
    	Session session = sessionFactory.getCurrentSession();
    	Query query = session.createSQLQuery("CALL UPDATE_RAISES_URGENT_FLAG();");
    	query.executeUpdate();
    }
    
    public List<RegisteredUser> findUsersInterestedInApplication(ApplicationForm applicationForm) {
        return sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class)
        		.createAlias("applicationFormUserRoles", "roles")
        		.add(Restrictions.eq("roles.applicationForm", applicationForm))
                .add(Restrictions.eq("roles.interestedInApplicant", true))
                .addOrder(Order.asc("lastName"))
                .addOrder(Order.asc("firstName"))
                .addOrder(Order.asc("id")) 
                .setProjection(Projections.projectionList().add(Projections.groupProperty("registeredUser"))).list();
    }
    
    public List<RegisteredUser> findProgramUsers(Program program) {
        return sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class)
        		.createAlias("applicationFormUserRoles", "roles")
        		.createAlias("roles.applicationForm.program", "program")
        		.add(Restrictions.eq("program", program))
        		.add(Restrictions.in("roles.role", Arrays.asList("REVIEWER", "INTEVIEWER", "SUPERVISOR")))
                .add(Restrictions.eq("roles.interestedInApplicant", false))
                .setProjection(Projections.projectionList().add(Projections.groupProperty("registeredUser"))).list();
    }
}