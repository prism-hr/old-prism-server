package com.zuehlke.pgadmissions.dao;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.BooleanUtils;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.internal.TypeLocatorImpl;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.hibernate.type.EnumType;
import org.hibernate.type.Type;
import org.hibernate.type.TypeResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormUserRole;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ActionType;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
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
    
    public ApplicationFormUserRole get(Integer id) {
        return (ApplicationFormUserRole) sessionFactory.getCurrentSession().get(ApplicationFormUserRole.class, id);
    }
    
    public void save(ApplicationFormUserRole applicationFormUserRole) {
        sessionFactory.getCurrentSession().saveOrUpdate(applicationFormUserRole);
    }

    public Date getUpdateTimestampByApplicationFormAndAuthorityUpdateVisility(ApplicationForm applicationForm, ApplicationUpdateScope updateVisibility) {
        return (Date) sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
                .createAlias("role", "applicationRole")
                .add(Restrictions.eq("applicationForm", applicationForm))
                .add(Restrictions.ge("applicationRole.updateVisibility", updateVisibility))
                .setProjection(Projections.projectionList()
                        .add(Projections.max("updateTimestamp"))).uniqueResult();
    }
    
    public List<ApplicationFormUserRole> getByApplicationFormAndAuthorities(ApplicationForm applicationForm, Authority... authorities) {
        return sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
                .add(Restrictions.eq("applicationForm", applicationForm))
                .add(Restrictions.in("role.id", authorities)).list();
    }

    public List<ApplicationFormUserRole> getByApplicationFormAndUserAndAuthorities(ApplicationForm applicationForm, RegisteredUser user, Authority... authorities) {
        return sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
                .add(Restrictions.eq("applicationForm", applicationForm))
                .add(Restrictions.eq("user", user))
                .add(Restrictions.in("role.id", authorities)).list();
    }
    
    public List<ApplicationFormUserRole> getByApplicationFormAndUserAndAuthoritiesWithActions(ApplicationForm applicationForm, RegisteredUser user, List<Authority> authorities) {
        return sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
                .createAlias("actions", "applicationFormActionRequired", JoinType.INNER_JOIN)
                .add(Restrictions.eq("applicationForm", applicationForm))
                .add(Restrictions.eq("user", user))
                .add(Restrictions.in("role.id", authorities)).list();
    }
    
    public Boolean getRaisesUrgentFlagByUserAndApplicationForm(RegisteredUser user, ApplicationForm applicationForm) {
        Boolean raisesUrgentFlag = (Boolean) sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
                .add(Restrictions.eq("applicationForm", applicationForm))
                .add(Restrictions.eq("user", user))
                .addOrder(Order.desc("raisesUrgentFlag"))
                .setProjection(Projections.projectionList()
                        .add(Projections.max("raisesUrgentFlag"))).uniqueResult();
        return BooleanUtils.toBoolean(raisesUrgentFlag);
    }

    public Boolean getRaisesUpdateFlagByUserAndApplicationForm(RegisteredUser user, ApplicationForm applicationForm) {
        Boolean raisesUpdateFlag = (Boolean) sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
                .add(Restrictions.eq("applicationForm", applicationForm))
                .add(Restrictions.eq("user", user))
                .addOrder(Order.desc("raisesUpdateFlag"))
                .setProjection(Projections.projectionList()
                        .add(Projections.max("raisesUpdateFlag"))).uniqueResult();
        return BooleanUtils.toBoolean(raisesUpdateFlag);
    }

    public List<RegisteredUser> getUsersInterestedInApplication(ApplicationForm applicationForm) {
        return sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
                .setProjection(Projections.projectionList().add(Projections.groupProperty("user"), "user"))
                .createAlias("user", "registeredUser", JoinType.INNER_JOIN)
                .add(Restrictions.eq("applicationForm", applicationForm))
                .add(Restrictions.eq("interestedInApplicant", true))
                .add(Restrictions
                        .disjunction()
                        .add(Restrictions.eq("registeredUser.enabled", true))
                        .add(Restrictions.conjunction().add(Restrictions.eq("registeredUser.enabled", false))
                                .add(Restrictions.in("role.id", Arrays.asList(Authority.SUGGESTEDSUPERVISOR)))))
                .addOrder(Order.asc("registeredUser.lastName"))
                .addOrder(Order.asc("registeredUser.firstName")).addOrder(Order.asc("registeredUser.id")).list();
    }

    public List<RegisteredUser> getUsersPotentiallyInterestedInApplication(ApplicationForm applicationForm) {
        DetachedCriteria usersInterestedInApplicant = DetachedCriteria.forClass(ApplicationFormUserRole.class)
                .setProjection(Projections.projectionList()
                        .add(Projections.groupProperty("user")))
                .add(Restrictions.eq("applicationForm", applicationForm))
                .add(Restrictions.eq("interestedInApplicant", true));

        return sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
                .setProjection(Projections.projectionList().add(Projections.groupProperty("user"), "user"))
                .createAlias("applicationForm", "applicationForm", JoinType.INNER_JOIN)
                .createAlias("applicationForm.program", "program", JoinType.INNER_JOIN)
                .createAlias("user", "registeredUser", JoinType.INNER_JOIN)
                .add(Restrictions.eq("program.id", applicationForm.getProgram().getId()))
                .add(Restrictions.in("role.id", AuthorityGroup.getAllInternalRecruiterAuthorities()))
                .add(Restrictions.isNull("registeredUser.primaryAccount"))
                .add(Restrictions.eq("registeredUser.enabled", true))
                .add(Property.forName("user").notIn(usersInterestedInApplicant))
                .addOrder(Order.asc("registeredUser.lastName"))
                .addOrder(Order.asc("registeredUser.firstName"))
                .addOrder(Order.asc("registeredUser.id")).list();
    }

    public void deleteApplicationActions(ApplicationForm applicationForm) {
        sessionFactory.getCurrentSession().createSQLQuery("CALL SP_DELETE_APPLICATION_ACTIONS(?);")
                .setInteger(0, applicationForm.getId()).executeUpdate();
    }

    public void deleteApplicationUpdate(ApplicationForm applicationForm, RegisteredUser registeredUser) {
        sessionFactory.getCurrentSession()
                .createSQLQuery("CALL SP_DELETE_APPLICATION_UPDATE(?, ?);")
                .setInteger(0, applicationForm.getId())
                .setInteger(1, registeredUser.getId()).executeUpdate();
    }
    
    public void deleteApplicationRole(ApplicationForm application, RegisteredUser user, Authority authority) {
        sessionFactory.getCurrentSession().createSQLQuery("CALL SP_DELETE_APPLICATION_ROLE(?, ?, ?);")
                .setInteger(0, application.getId())
                .setInteger(1, user.getId())
                .setString(2, authority.toString()).executeUpdate();
    }

    public void deleteProgramRole(RegisteredUser registeredUser, Program program, Authority authority) {
        sessionFactory.getCurrentSession().createSQLQuery("CALL SP_DELETE_PROGRAM_ROLE(?, ?, ?);")
                .setInteger(0, registeredUser.getId())
                .setInteger(1, program.getId())
                .setString(2, authority.toString()).executeUpdate();
    }

    public void deleteStateActions(ApplicationForm applicationForm) {
        sessionFactory.getCurrentSession().createSQLQuery("CALL SP_DELETE_STATE_ACTIONS(?);")
                .setInteger(0, applicationForm.getId()).executeUpdate();
    }

    public void deleteRoleAction(ApplicationForm applicationForm, Authority authority, ApplicationFormAction action) {
        sessionFactory.getCurrentSession().createSQLQuery("CALL SP_DELETE_ROLE_ACTION(?, ?, ?);")
                .setInteger(0, applicationForm.getId())
                .setString(1, authority.toString())
                .setString(2, action.toString()).executeUpdate();
    }

    public void deleteUserAction(ApplicationForm applicationForm, RegisteredUser registeredUser, Authority authority, ApplicationFormAction action) {
       sessionFactory.getCurrentSession().createSQLQuery("CALL SP_DELETE_USER_ACTION(?, ?, ?, ?);")
                .setInteger(0, applicationForm.getId())
                .setInteger(1, registeredUser.getId())
                .setString(2, authority.toString())
                .setString(3, action.toString()).executeUpdate();
    }

    public void deleteUserRole(RegisteredUser registeredUser, Authority authority) {
        sessionFactory.getCurrentSession().createSQLQuery("CALL SP_DELETE_USER_ROLE(?, ?);")
                .setInteger(0, registeredUser.getId())
                .setString(1, authority.toString()).executeUpdate();
    }

    public void insertApplicationUpdate(ApplicationForm applicationForm, RegisteredUser registeredUser, Date updateTimestamp,
            ApplicationUpdateScope updateVisibility) {
        sessionFactory.getCurrentSession().createSQLQuery("CALL SP_INSERT_APPLICATION_UPDATE(?, ?, ?, ?);")
                .setInteger(0, applicationForm.getId())
                .setInteger(1, registeredUser.getId())
                .setString(2, javaDateToMySQLDateString(updateTimestamp))
                .setInteger(3, ApplicationUpdateScope.valueOf(updateVisibility.toString()).ordinal()).executeUpdate();
    }

    public void insertProgramRole(RegisteredUser registeredUser, Program program, Authority authority) {
        sessionFactory.getCurrentSession().createSQLQuery("CALL SP_INSERT_PROGRAM_ROLE(?, ?, ?);")
                .setInteger(0, registeredUser.getId())
                .setInteger(1, program.getId())
                .setString(2, authority.toString()).executeUpdate();
    }

    public void insertUserRole(RegisteredUser registeredUser, Authority authority) {
        sessionFactory.getCurrentSession().createSQLQuery("CALL SP_INSERT_USER_ROLE(?, ?);")
                .setInteger(0, registeredUser.getId())
                .setString(1, authority.toString()).executeUpdate();
    }

    public void updateApplicationDueDate(ApplicationForm applicationForm, Date deadlineTimestamp) {
        sessionFactory.getCurrentSession().createSQLQuery("CALL SP_UPDATE_APPLICATION_FORM_DUE_DATE(?, ?);")
                .setInteger(0, applicationForm.getId()).setDate(1, deadlineTimestamp).executeUpdate();
    }

    public void updateApplicationInterest(ApplicationForm applicationForm, RegisteredUser registeredUser, Boolean interested) {
        sessionFactory.getCurrentSession().createSQLQuery("CALL SP_UPDATE_APPLICATION_INTEREST(?, ?, ?);")
                .setInteger(0, applicationForm.getId())
                .setInteger(1, registeredUser.getId())
                .setBoolean(2, interested).executeUpdate();
    }
    
    public List<ActionDefinition> selectUserActions(Integer applicationFormId, Integer registeredUserId) {
        return selectUserActionsBase(applicationFormId, registeredUserId, null, null);
    }
    
    public List<ActionDefinition> selectUserActionById(Integer applicationFormId, Integer registeredUserId, ApplicationFormAction action) {
        return selectUserActionsBase(applicationFormId, registeredUserId, action, null);
    }
    
    public List<ActionDefinition> selectUserActionByActionType(Integer applicationFormId, Integer registeredUserId, ActionType actionType) {
        return selectUserActionsBase(applicationFormId, registeredUserId, null, actionType);
    }

    public void updateUrgentApplications() {
        sessionFactory.getCurrentSession().createSQLQuery("CALL SP_UPDATE_URGENT_APPLICATIONS();").executeUpdate();
    }

    private String javaDateToMySQLDateString(Date date) {
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return outputDateFormat.format(date);
    }
    
    private List<ActionDefinition> selectUserActionsBase(Integer applicationFormId, Integer registeredUserId, ApplicationFormAction action, ActionType actionType) {
        Properties customDTOProperties = new Properties();
        customDTOProperties.put("enumClass", ApplicationFormAction.class.getCanonicalName());
        customDTOProperties.put("type", "12");
        Type actionEnum = new TypeLocatorImpl(new TypeResolver()).custom(EnumType.class, customDTOProperties);
        
        return (sessionFactory.getCurrentSession().createSQLQuery("CALL SP_SELECT_USER_ACTIONS(?, ?, ?, ?);")
                .addScalar("action_id", actionEnum)
                .addScalar("raises_urgent_flag", BooleanType.INSTANCE)
                .setInteger(0, applicationFormId)
                .setInteger(1, registeredUserId))
                .setString(2, action.toString())
                .setString(3, actionType.toString())
                .setResultTransformer(Transformers.aliasToBean(ActionDefinition.class)).list();
    }

}
